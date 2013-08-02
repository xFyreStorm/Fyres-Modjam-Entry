package assets.fyresmodjam;

import java.util.Properties;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

import assets.fyresmodjam.ItemStatHelper.*;
import assets.fyresmodjam.EntityStatHelper.*;

@Mod(modid = "fyresmodjam", name = "Fyres ModJam Mod", version = "0.0.0a")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = {"FyresModJamMod"}, packetHandler = PacketHandler.class)
public class ModjamMod  {
	
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
		
		new ItemStatHelper().register();
		new EntityStatHelper().register();
		
		NetworkRegistry.instance().registerGuiHandler(this, new GUIHandler());
		
		//Item and Block loading
		
		GameRegistry.registerBlock(blockPillar, "blockPillar");
		GameRegistry.registerTileEntity(TileEntityPillar.class, "Pillar Tile Entity");
		LanguageRegistry.addName(blockPillar, "Pillar");
		
		LanguageRegistry.addName(itemPillar, "Pillar");
		
		//Entity Trackers
		
		EntityStatTracker creeperTracker = new EntityStatTracker(EntityCreeper.class);
		
		creeperTracker.addStat(new EntityStat("Level", "") {
			public Object getNewValue(Random r) {return 1 + r.nextInt(5);}
			public String getAlteredEntityName(EntityLiving entity) {return entity.getEntityName() + ", Level " + entity.getEntityData().getString(name);}
			
			public void modifyEntity(Entity entity) {
				int healthGain = Integer.parseInt(entity.getEntityData().getString(name)) * 2;
				((EntityLivingBase) entity).func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(((EntityLivingBase) entity).func_110138_aP() + healthGain);
				((EntityLivingBase) entity).setEntityHealth(((EntityLivingBase) entity).func_110143_aJ() + healthGain);
			}
		});
		
		EntityStatHelper.addStatTracker(creeperTracker);
		
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
}
