package assets.fyresmodjam;

import java.util.Properties;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerEvent;
import assets.fyresmodjam.EntityStatHelper.EntityStat;
import assets.fyresmodjam.EntityStatHelper.EntityStatTracker;
import assets.fyresmodjam.ItemStatHelper.ItemStat;
import assets.fyresmodjam.ItemStatHelper.ItemStatTracker;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = "fyresmodjam", name = "Fyres ModJam Mod", version = "0.0.0a")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = {"FyresModJamMod"}, packetHandler = PacketHandler.class)
public class ModjamMod extends CommandHandler implements IPlayerTracker {
	
	@SidedProxy(clientSide = "assets.fyresmodjam.ClientProxy", serverSide = "assets.fyresmodjam.CommonProxy")
    public static CommonProxy proxy;
    
    @Instance("fyresmodjam")
    public static ModjamMod instance;
    
    public static Random r = new Random();
    
    public static int itemID = 2875, blockID = 2875;
    public static boolean pillarGlow = true;
    
    public static Block blockPillar;
    
    public static Item itemPillar;
    public static Item mysteryPotion;
	
    public static void loadProperties() {
		Properties prop = new Properties();
		
		try {
            prop.load(ModjamMod.class.getResourceAsStream("/FyresModJamMod.properties"));
        } catch (Exception e) {e.printStackTrace();}
		
		itemID = Integer.parseInt(prop.getProperty("itemID", "" + itemID));
		blockID = Integer.parseInt(prop.getProperty("blockID", "" + blockID));
		pillarGlow = Boolean.parseBoolean(prop.getProperty("pillarGlow", "" + pillarGlow));
	}
    
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {loadProperties();}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		
		//Registering
		
		proxy.register();
		
		TickRegistry.registerTickHandler(new CommonTickHandler(), Side.SERVER);
		
		MinecraftForge.EVENT_BUS.register(this);
		GameRegistry.registerPlayerTracker(this);
		
		new ItemStatHelper().register();
		new EntityStatHelper().register();
		
		NetworkRegistry.instance().registerGuiHandler(this, new GUIHandler());
		
		GameRegistry.registerWorldGenerator(new PillarGen());
		for(int i = 0; i < 3; i++) {GameRegistry.registerWorldGenerator(new WorldGenMoreDungeons());}
		
		//Item and Block loading
		
		blockPillar = new BlockPillar(blockID).setBlockUnbreakable().setResistance(6000000.0F);
		
		itemPillar = new ItemPillar(itemID).setUnlocalizedName("blockPillar");
		mysteryPotion = new ItemMysteryPotion(itemID + 1).setUnlocalizedName("mysteryPotion").setCreativeTab(CreativeTabs.tabBrewing);
		
		GameRegistry.registerBlock(blockPillar, "blockPillar");
		GameRegistry.registerTileEntity(TileEntityPillar.class, "Pillar Tile Entity");
		
		LanguageRegistry.addName(blockPillar, "Pillar Block");
		
		LanguageRegistry.addName(itemPillar, "Pillar");
		LanguageRegistry.addName(mysteryPotion, "Mystery Potion");
		
		LanguageRegistry.instance().addStringLocalization("commands.currentBlessing.usage", "/currentBlessing - used to check your current blessing");
		
		//Entity Trackers
		
		EntityStatTracker mobTracker = new EntityStatTracker(EntityMob.class, true);
		
		mobTracker.addStat(new EntityStat("Level", "") {
			public Object getNewValue(Random r) {
				int i = 1;
				for(; i < 5; i++) {if(ModjamMod.r.nextInt(10) < 4) {break;}}
				return i;
			}
			
			public String getAlteredEntityName(EntityLiving entity) {
				int level = Integer.parseInt(entity.getEntityData().getString(name));
				return (level == 5 ? "\u00A7c" : "") + entity.getEntityName() + ", Level " + level;
			}
			
			public void modifyEntity(Entity entity) {
				int level = Integer.parseInt(entity.getEntityData().getString(name));
				int healthGain = (level - 1) * 5;
				
				if(healthGain != 0) {
					((EntityLivingBase) entity).func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(((EntityLivingBase) entity).func_110138_aP() + healthGain);
					((EntityLivingBase) entity).setEntityHealth(((EntityLivingBase) entity).func_110143_aJ() + healthGain);
				}
				
				if(level == 5) {
					if(ModjamMod.r.nextBoolean()) {
						if(entity instanceof IRangedAttackMob) {entity.getEntityData().setString("Blessing", "Hunter");}
						else {entity.getEntityData().setString("Blessing", "Warrior");}
					} else {
						entity.getEntityData().setString("Blessing", "Swamp");
					}
					
					if(entity instanceof EntityCreeper) {
						((EntityCreeper) entity).getDataWatcher().updateObject(17, (byte) 1);
						((EntityCreeper) entity).getEntityData().setBoolean("powered", true);
					}
				}
			}
		});
		
		EntityStatHelper.addStatTracker(mobTracker);
		
		//Item Trackers
		
		ItemStatTracker weaponTracker = new ItemStatTracker(new Class[] {ItemSword.class, ItemAxe.class}, null, false);
		
		weaponTracker.addStat(new ItemStat("Prefix", "") {
			public String[] prefixes = new String[] {"Old", "Sharp", "Average"};
			public Object getNewValue(Random r) {return prefixes[r.nextInt(prefixes.length)];}
			public String getAlteredStackName(ItemStack stack) {return "\u00A7f" + stack.getTagCompound().getString(name) + " " + stack.getDisplayName();}
		});
		
		weaponTracker.addStat(new ItemStat("Rank", "") {
			public Object getNewValue(Random r) {
				int i = 1;
				for(; i < 5; i++) {if(ModjamMod.r.nextInt(10) < 4) {break;}}
				return i;
			}
			
			public void modifyStack(ItemStack stack) {
				int rank = Integer.parseInt(stack.getTagCompound().getString(name));
				int bonusDamage = (rank - 1)/2 + (int) (ModjamMod.r.nextInt(rank + 1));
				
				ItemStatHelper.giveStat(stack, "BonusDamage", bonusDamage);
				ItemStatHelper.addLore(stack, bonusDamage != 0 ? "\u00A77\u00A7o  " + (bonusDamage > 0 ? "+" : "") + bonusDamage + " bonus damage" : null);
				
				ItemStatHelper.addLore(stack, "\u00A7eRank: "+ rank);
			}
		});
		
		ItemStatHelper.addStatTracker(weaponTracker);
		
		//ItemStatTracker foodTracker = new ItemStatTracker(ItemFood.class, -1);
		//foodTracker.addStat(new ItemStat("Spoiled", false));
		//ItemStatHelper.addStatTracker(foodTracker);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}

	@Override
	public void onPlayerLogin(EntityPlayer player) {
		if(!player.worldObj.isRemote) {
			if(player.getEntityData().hasKey("Blessing")) {PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.UPDATE_BLESSING, new Object[] {player.getEntityData().getString("Blessing")}), (Player) player);}
			if(player.getEntityData().hasKey("PotionKnowledge")) {PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.UPDATE_POTION_KNOWLEDGE, new Object[] {player.getEntityData().getIntArray("PotionKnowledge")}), (Player) player);}
		}
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
		
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		
	}
	
	@ForgeSubscribe
    public void checkBreakSpeed(PlayerEvent.BreakSpeed event) {
    	if(event.entityPlayer != null && event.entityPlayer.getEntityData().hasKey("Blessing")) {
    		String blessing = event.entityPlayer.getEntityData().getString("Blessing");
    		
    		if(blessing.equals("Miner")) {
    			if(event.block.blockMaterial == Material.rock || event.block.blockMaterial == Material.iron) {event.newSpeed = event.originalSpeed * 1.25F;}
    		} else if(blessing.equals("Lumberjack")) {
    			if(event.block.blockMaterial == Material.wood) {event.newSpeed = event.originalSpeed * 1.25F;}
    		}
    	}
    }
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		this.initCommands(event);
	}
	
	public void initCommands(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandCurrentBlessing());
    }
}
