package fyresmodjam.handlers;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import fyresmodjam.ModjamMod;
import fyresmodjam.misc.EntityStatHelper;
import fyresmodjam.worldgen.FyresWorldData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.EnumSet;

public class CommonTickHandler implements ITickHandler
{
    public static FyresWorldData worldData = null;

    public static ArrayList<Entity> addLater = new ArrayList<Entity>();

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        if (type.equals(EnumSet.of(TickType.WORLDLOAD)))
        {
            for (Object aTickData : tickData)
            {
                if (aTickData instanceof World && ((World) aTickData).provider.dimensionId == 0)
                {
                    worldData = FyresWorldData.forWorld((World) aTickData);
                    worldData.markDirty();
                }
            }
        }
        else if (type.equals(EnumSet.of(TickType.SERVER)))
        {
            serverTick();
        }

    }

    public void serverTick()
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

        for (int i = 0; i < server.worldServers.length; i++)
        {
            WorldServer s = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[i];

            if (s == null) {continue;}

            for (Object o : s.playerEntities)
            {
                if (o == null || !(o instanceof EntityPlayer)) {continue;}

                EntityPlayer player = (EntityPlayer) o;

                String blessing = player.getEntityData().hasKey("Blessing") ? player.getEntityData().getString("Blessing") : null;
                int coolDown = 0, counter = 0, timer = 0;
                boolean blessingActive = EntityStatHelper.hasStat(player, "BlessingActive") && Boolean.parseBoolean(EntityStatHelper.getStat(player, "BlessingActive"));

				/*if(EntityStatHelper.hasStat(player, "BlessingCooldown")) {
                    coolDown = Integer.parseInt(EntityStatHelper.getStat(player, "BlessingCooldown"));
					if(coolDown > 0) {coolDown--;}
				}*/

                if (EntityStatHelper.hasStat(player, "BlessingTimer"))
                {
                    timer = Integer.parseInt(EntityStatHelper.getStat(player, "BlessingTimer"));
                    if (blessingActive)
                    {timer++;}
                    else
                    {timer = 0;}
                }

                if (EntityStatHelper.hasStat(player, "BlessingCounter"))
                {
                    counter = Integer.parseInt(EntityStatHelper.getStat(player, "BlessingCounter"));

                    if (blessing != null)
                    {
                        if (blessingActive && blessing.equals("Berserker") && timer % 40 == 0)
                        {
                            counter = Math.max(0, counter - 1);

                            if (counter == 0)
                            {
                                PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7cYou calm down."}), (Player) player);
                                coolDown = (int) (player.worldObj.getWorldTime() + 1200);
                                timer = 0;
                                blessingActive = false;
                            }
                        }
                    }
                }

                if (blessing != null)
                {
                    if (player.isSneaking() && blessing.equals("Ninja"))
                    {
                        PotionEffect e = player.getActivePotionEffect(Potion.invisibility);
                        if (e == null || player.getActivePotionEffect(Potion.invisibility).getDuration() < 10) {player.addPotionEffect(new PotionEffect(Potion.invisibility.id, 10, 1, false));}
                    }
                    else if (blessing.equals("Diver"))
                    {
                        player.setAir(0);
                    }
                    else if (blessing.equals("Inferno") && player.isWet() && player.ticksExisted % 10 == 0)
                    {
                        player.attackEntityFrom(DamageSource.drown, 1.0F);
                    }/* else if(blessing.equals("Healer")) {
                        if(player.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration") && ((player.worldObj.difficultySetting == 0 && player.func_110143_aJ() < player.func_110138_aP() && player.ticksExisted % 20 * 12 == 0) || (player.getFoodStats().getFoodLevel() >= 18 && player.getEntityData().getInteger("foodTickTimer") >= 80 && player.shouldHeal()))) {
							player.heal(1.0F);
							System.out.println(true);
				        }
					}*/
                }

                EntityStatHelper.giveStat(player, "BlessingActive", blessingActive);
                if (coolDown != 0) {EntityStatHelper.giveStat(player, "BlessingCooldown", coolDown);}
                EntityStatHelper.giveStat(player, "BlessingCounter", counter);
                EntityStatHelper.giveStat(player, "BlessingTimer", timer);

                PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.UPDATE_STATS, new Object[] {1, "BlessingCounter", "" + counter}), (Player) player);
                //PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.UPDATE_STATS, new Object[] {2, "BlessingCounter", "" + counter, "BlessingActive", "" + blessingActive}), (Player) player);
            }

            for (Object o : s.loadedEntityList)
            {
                if (o == null || !(o instanceof Entity)) {continue;}

                if (o instanceof EntityItem)
                {
                    EntityItem item = (EntityItem) o;

                    if (((EntityItem) o).isBurning())
                    {
                        ItemStack stack = item.getDataWatcher().getWatchableObjectItemStack(10);

                        if (worldData.currentTask.equals("Burn") && stack.getItem().itemID == worldData.currentTaskID)
                        {
                            worldData.progress += stack.stackSize;
                            ((EntityItem) o).isDead = true;

                            String name1 = CommonTickHandler.worldData.currentTask.equals("Kill") ? FyresWorldData.validMobNames[CommonTickHandler.worldData.currentTaskID] : new ItemStack(Item.itemsList[CommonTickHandler.worldData.currentTaskID], 1).getDisplayName();

                            if (name1.contains("Block"))
                            {if (name1.contains("Block")) {name1 = name1.replace("Block", "Blocks").replace("block", "blocks");}}
                            else
                            {name1 += "s";}

                            PacketDispatcher.sendPacketToAllPlayers(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7fCurrent Goal Progress: " + worldData.progress + "/" + worldData.currentTaskAmount + " " + name1 + " " + worldData.currentTask + "ed."}));

                            if (worldData.progress >= worldData.currentTaskAmount)
                            {
                                worldData.progress = 0;
                                worldData.tasksCompleted++;

                                PacketDispatcher.sendPacketToAllPlayers(PacketHandler.newPacket(PacketHandler.LEVEL_UP, new Object[] {worldData.rewardLevels}));

                                worldData.giveNewTask();


                                String name = worldData.currentTask.equals("Kill") ? FyresWorldData.validMobNames[worldData.currentTaskID] : new ItemStack(Item.itemsList[worldData.currentTaskID], 1).getDisplayName();

                                if (worldData.currentTaskAmount > 1)
                                {
                                    if (name.contains("Block"))
                                    {if (name.contains("Block")) {name = name.replace("Block", "Blocks").replace("block", "blocks");}}
                                    else
                                    {name += "s";}
                                }

                                PacketDispatcher.sendPacketToAllPlayers(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7eA world goal has been completed!" + (!worldData.getDisadvantage().equals("None") ? " World disadvantage has been lifted!" : "")}));
                                PacketDispatcher.sendPacketToAllPlayers(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE,
                                        new Object[] {"\u00A7eA new world goal has been set: " + (worldData.currentTask + " " + worldData.currentTaskAmount + " " + name + ". (" + worldData.progress + " " + worldData.currentTask + "ed)")}));
                                //PacketDispatcher.sendPacketToAllPlayers(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7eGoal Reward: " + worldData.rewardLevels + " experience levels"}));

                                worldData.currentDisadvantage = "None";
                            }

                            PacketDispatcher.sendPacketToAllPlayers(PacketHandler.newPacket(PacketHandler.UPDATE_WORLD_DATA,
                                    new Object[] {worldData.potionValues, worldData.potionDurations, worldData.getDisadvantage(), worldData.currentTask, worldData.currentTaskID, worldData.currentTaskAmount, worldData.progress, worldData.tasksCompleted, worldData.enderDragonKilled, ModjamMod.spawnTraps, worldData.rewardLevels, worldData.mushroomColors}));

                            worldData.setDirty(true);
                        }
                    }
                }
            }
        }

        for (Entity e : addLater)
        {
            WorldServer world = null;
            for (WorldServer s : MinecraftServer.getServer().worldServers)
            {
                if (s.provider.dimensionId == e.dimension)
                {
                    world = s;
                    break;
                }
            }
            if (world != null) {world.spawnEntityInWorld(e);}
        }
        addLater.clear();

        if (worldData != null)
        {
            if (worldData.getDisadvantage().equals("Neverending Rain"))
            {
                if (!MinecraftServer.getServer().worldServers[0].getWorldInfo().isRaining()) {MinecraftServer.getServer().worldServers[0].getWorldInfo().setRaining(true);}
                if (!MinecraftServer.getServer().worldServers[0].getWorldInfo().isThundering()) {MinecraftServer.getServer().worldServers[0].getWorldInfo().setThundering(true);}
            }
            else if (worldData.getDisadvantage().equals("Neverending Night"))
            {
                MinecraftServer.getServer().worldServers[0].getWorldInfo().setWorldTime(18000);
            }
        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {

    }

    @Override
    public EnumSet ticks()
    {
        return EnumSet.of(TickType.SERVER, TickType.WORLDLOAD);
    }

    @Override
    public String getLabel()
    {
        return "FyresModJamCommonTicker";
    }
}