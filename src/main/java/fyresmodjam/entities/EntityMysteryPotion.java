package fyresmodjam.entities;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;
import fyresmodjam.handlers.CommonTickHandler;
import fyresmodjam.handlers.PacketHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class EntityMysteryPotion extends EntityThrowable
{
    public EntityMysteryPotion(World par1World)
    {
        super(par1World);
    }

    public EntityMysteryPotion(World par1World, EntityLivingBase par2EntityLivingBase, int par3)
    {
        this(par1World, par2EntityLivingBase, null);
    }

    public EntityMysteryPotion(World par1World, EntityLivingBase par2EntityLivingBase, ItemStack par4ItemStack)
    {
        super(par1World, par2EntityLivingBase);

        if (par4ItemStack != null)
        {
            int damage = par4ItemStack.getItemDamage();
            this.dataWatcher.updateObject(24, damage);
            if (damage % 13 < 12) {this.dataWatcher.updateObject(25, CommonTickHandler.worldData.potionDurations[damage % 13]);}
        }
    }

    public void entityInit()
    {
        super.entityInit();

        this.dataWatcher.addObject(24, 0);
        this.dataWatcher.addObject(25, 0);
    }

    @SideOnly(Side.CLIENT)
    public EntityMysteryPotion(World par1World, double par2, double par4, double par6, int par8)
    {
        this(par1World, par2, par4, par6, new ItemStack(Item.potion, 1, par8));
    }

    public EntityMysteryPotion(World par1World, double par2, double par4, double par6, ItemStack par4ItemStack)
    {
        super(par1World, par2, par4, par6);

        if (par4ItemStack != null)
        {
            int damage = par4ItemStack.getItemDamage();
            this.dataWatcher.updateObject(24, damage);
            this.dataWatcher.updateObject(25, CommonTickHandler.worldData.potionDurations[damage % 13]);
        }
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    protected float getGravityVelocity()
    {
        return 0.05F;
    }

    protected float func_70182_d()
    {
        return 0.5F;
    }

    protected float func_70183_g()
    {
        return -20.0F;
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(MovingObjectPosition par1MovingObjectPosition)
    {
        if (!this.worldObj.isRemote)
        {
            AxisAlignedBB axisalignedbb = this.boundingBox.expand(4.0D, 2.0D, 4.0D);
            List list1 = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);

            if (list1 != null && !list1.isEmpty())
            {
                int type = getDataWatcher().getWatchableObjectInt(24) % 13;
                int j = type >= 12 ? (5 + ModjamMod.r.nextInt(26)) : getDataWatcher().getWatchableObjectInt(25);

                int damage = 0;

                if (type >= 12 || (getThrower().getEntityData().hasKey("Blessing") && getThrower().getEntityData().getString("Blessing").equals("Alchemist")))
                {
                    damage = ModjamMod.r.nextInt(Potion.potionTypes.length);
                    while (Potion.potionTypes[damage] == null) {damage = ModjamMod.r.nextInt(Potion.potionTypes.length);}
                }
                else
                {
                    damage = CommonTickHandler.worldData.potionValues[type];
                }

                for (Object aList1 : list1)
                {
                    EntityLivingBase entitylivingbase = (EntityLivingBase) aList1;
                    double d0 = this.getDistanceSqToEntity(entitylivingbase);

                    if (d0 < 16.0D)
                    {
                        if (Potion.potionTypes[damage].isInstant())
                        {
                            Potion.potionTypes[damage].affectEntity(getThrower(), entitylivingbase, 1, 1);
                        }
                        else
                        {
                            entitylivingbase.addPotionEffect(new PotionEffect(damage, j * 20, 1, false));
                        }
                    }
                }

                if (getThrower() instanceof EntityPlayer)
                {
                    EntityPlayer par3EntityPlayer = (EntityPlayer) getThrower();

                    if (type < 12 && (!getThrower().getEntityData().hasKey("Blessing") || !getThrower().getEntityData().getString("Blessing").equals("Alchemist")))
                    {
                        if (!par3EntityPlayer.getEntityData().hasKey("PotionKnowledge")) {par3EntityPlayer.getEntityData().setIntArray("PotionKnowledge", new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});}

                        if (par3EntityPlayer.getEntityData().getIntArray("PotionKnowledge")[type] == -1)
                        {
                            par3EntityPlayer.getEntityData().getIntArray("PotionKnowledge")[type] = 1;

                            PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.UPDATE_POTION_KNOWLEDGE, new Object[] {par3EntityPlayer.getEntityData().getIntArray("PotionKnowledge")}), (Player) par3EntityPlayer);

                            Potion potion = Potion.potionTypes[CommonTickHandler.worldData.potionValues[type]];
                            String name = StatCollector.translateToLocal(potion.getName()) + " Potion";

                            if (!potion.isInstant())
                            {
                                int time = CommonTickHandler.worldData.potionDurations[type];
                                name += " (" + time + " seconds)";
                            }

                            PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7oYou learnt Mystery Potion #" + (type + 1) + " was a " + name + "!"}), (Player) par3EntityPlayer);
                        }
                    }
                    else
                    {
                        Potion potion = Potion.potionTypes[damage];
                        String name = StatCollector.translateToLocal(potion.getName()) + " Potion";
                        if (!potion.isInstant()) {name += " (" + j + " seconds)";}
                        PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7oYou threw a " + name + "."}), (Player) par3EntityPlayer);
                    }
                }
            }

            this.worldObj.playAuxSFX(2002, (int) Math.round(this.posX), (int) Math.round(this.posY), (int) Math.round(this.posZ), 1);
            this.setDead();
        }
    }

}
