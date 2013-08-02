package assets.fyresmodjam;

import java.util.Properties;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
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
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "fyresmodjam", name = "Fyres ModJam Mod", version = "0.0.0a")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = {"FyresModJamMod"}, packetHandler = PacketHandler.class)
public class ModjamMod implements IPlayerTracker {
	
	@SidedProxy(clientSide = "assets.fyresmodjam.ClientProxy", serverSide = "assets.fyresmodjam.CommonProxy")
    public static CommonProxy proxy;
    
    @Instance("fyresmodjam")
    public static ModjamMod instance;
    
    public static int itemID = 2875;
    public static int blockID = 2875;
    
    static {
    	loadProperties();
    }
    
    public static Random r = new Random();
    
    public static Block blockPillar = new BlockPillar(blockID).setBlockUnbreakable().setResistance(6000000.0F);
    public static Item itemPillar = new ItemPillar(itemID);
    
    public static PillarGen pillarGen = new PillarGen();
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
	}
	
	public static void loadProperties() {
		Properties prop = new Properties();
		
		try {
            prop.load(ModjamMod.class.getResourceAsStream("/FyresModJamMod.properties"));
        } catch (Exception e) {e.printStackTrace();}
		
		itemID = Integer.parseInt(prop.getProperty("itemID", "" + itemID));
		blockID = Integer.parseInt(prop.getProperty("blockID", "" + blockID));
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		
		//Registering
		
		proxy.register();
		
		MinecraftForge.EVENT_BUS.register(this);
		GameRegistry.registerPlayerTracker(this);
		
		new ItemStatHelper().register();
		new EntityStatHelper().register();
		
		NetworkRegistry.instance().registerGuiHandler(this, new GUIHandler());
		
		GameRegistry.registerWorldGenerator(pillarGen);
		
		//Item and Block loading
		
		GameRegistry.registerBlock(blockPillar, "blockPillar");
		GameRegistry.registerTileEntity(TileEntityPillar.class, "Pillar Tile Entity");
		LanguageRegistry.addName(blockPillar, "Pillar");
		
		LanguageRegistry.addName(itemPillar, "Pillar");
		
		//Entity Trackers
		
		EntityStatTracker mobTracker = new EntityStatTracker(new Class[] {EntityBlaze.class, EntityCaveSpider.class, EntityCreeper.class, EntityEnderman.class, EntityGhast.class, EntityIronGolem.class, EntityMagmaCube.class, EntityPigZombie.class, EntitySilverfish.class, EntitySkeleton.class, EntitySlime.class, EntitySpider.class, EntityWitch.class, EntityZombie.class});
		
		mobTracker.addStat(new EntityStat("Level", "") {
			public Object getNewValue(Random r) {return 1 + r.nextInt(5);}
			public String getAlteredEntityName(EntityLiving entity) {return entity.getEntityName() + ", Level " + entity.getEntityData().getString(name);}
			
			public void modifyEntity(Entity entity) {
				int level = Integer.parseInt(entity.getEntityData().getString(name));
				int healthGain = level * 2;
				
				((EntityLivingBase) entity).func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(((EntityLivingBase) entity).func_110138_aP() + healthGain);
				((EntityLivingBase) entity).setEntityHealth(((EntityLivingBase) entity).func_110143_aJ() + healthGain);
			
				if(entity instanceof IRangedAttackMob && level == 5) {entity.getEntityData().setString("Blessing", "Hunter");}
			}
		});
		
		EntityStatHelper.addStatTracker(mobTracker);
		
		//Item Trackers
		
		ItemStatTracker swordTracker = new ItemStatTracker(ItemSword.class, -1);
		
		swordTracker.addStat(new ItemStat("Prefix", "") {
			public String[] prefixes = new String[] {"Old", "Sharp", "Average"};
			public Object getNewValue(Random r) {return prefixes[r.nextInt(prefixes.length)];}
			public String getAlteredStackName(ItemStack stack) {return "\u00A7f" + stack.getTagCompound().getString(name) + " " + stack.getDisplayName();}
		});
		
		swordTracker.addStat(new ItemStat("BonusDamage", "") {
			public Object getNewValue(Random r) {return r.nextInt(7);}
			
			public String getLore(ItemStack stack) {
				int damage = Integer.parseInt(stack.getTagCompound().getString(name));
				return damage > 0 ? "\u00A77\u00A7o  +" + damage + " bonus damage" : null;
			}
		});
		
		swordTracker.addStat(new ItemStat("Rank", "") {
			public Object getNewValue(Random r) {return 1 + r.nextInt(5);}
			public String getLore(ItemStack stack) {return "\u00A7eRank: "+ Integer.parseInt(stack.getTagCompound().getString(name));}
		});
		
		//swordTracker.giveStat("BonusDamage,+%v bonus damage", "#i,0,6");
		//swordTracker.giveStat("Rank,Rank: %v", "#i,1,5");
		
		ItemStatHelper.addStatTracker(swordTracker);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}

	@Override
	public void onPlayerLogin(EntityPlayer player) {
		if(!player.worldObj.isRemote && player.getEntityData().hasKey("Blessing")) {
			PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.UPDATE_BLESSING, new Object[] {player.getEntityData().getString("Blessing")}), (Player) player);
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
}
