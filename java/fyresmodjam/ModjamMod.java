package fyresmodjam;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.EnumMap;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerEvent;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import fyresmodjam.blocks.BlockMysteryMushroom;
import fyresmodjam.blocks.BlockPillar;
import fyresmodjam.blocks.BlockTrap;
import fyresmodjam.commands.CommandCraftingStats;
import fyresmodjam.commands.CommandCurrentBlessing;
import fyresmodjam.commands.CommandCurrentDisadvantage;
import fyresmodjam.commands.CommandCurrentWorldTask;
import fyresmodjam.commands.CommandKillStats;
import fyresmodjam.commands.CommandWeaponStats;
import fyresmodjam.entities.EntityMysteryPotion;
import fyresmodjam.handlers.CommonTickHandler;
import fyresmodjam.handlers.GUIHandler;
import fyresmodjam.handlers.NewPacketHandler;
import fyresmodjam.handlers.NewPacketHandler.ChannelHandler;
import fyresmodjam.items.ItemMysteryMushroom;
import fyresmodjam.items.ItemMysteryPotion;
import fyresmodjam.items.ItemObsidianSceptre;
import fyresmodjam.items.ItemPillar;
import fyresmodjam.items.ItemTrap;
import fyresmodjam.misc.CreativeTabModjamMod;
import fyresmodjam.misc.EntityStatHelper;
import fyresmodjam.misc.EntityStatHelper.EntityStat;
import fyresmodjam.misc.EntityStatHelper.EntityStatTracker;
import fyresmodjam.misc.ItemStatHelper;
import fyresmodjam.misc.ItemStatHelper.ItemStat;
import fyresmodjam.misc.ItemStatHelper.ItemStatTracker;
import fyresmodjam.tileentities.TileEntityPillar;
import fyresmodjam.tileentities.TileEntityTrap;
import fyresmodjam.worldgen.FyresWorldData;
import fyresmodjam.worldgen.PillarGen;
import fyresmodjam.worldgen.WorldGenMoreDungeons;
import fyresmodjam.worldgen.WorldGenTrapsTowersAndMore;

@Mod(modid = "fyresmodjam", name = "Fyres ModJam Mod", version = "0.0.3d")
public class ModjamMod extends CommandHandler {

	@SidedProxy(clientSide = "fyresmodjam.ClientProxy", serverSide = "fyresmodjam.CommonProxy")
	public static CommonProxy proxy;

	@Instance("fyresmodjam")
	public static ModjamMod instance;
	
	public static EnumMap<Side, FMLEmbeddedChannel> channels = NetworkRegistry.INSTANCE.newChannel("YWDMod", new ChannelHandler());

	public static Random r = new Random();

	public static int achievementID = 2500, examineKey = Keyboard.KEY_X, blessingKey = Keyboard.KEY_K;
	public static int pillarGenChance = 75, maxPillarsPerChunk = 3, towerGenChance = 225, trapGenChance = 300, mushroomReplaceChance = 15;
	public static boolean pillarGlow = true, spawnTraps = true, spawnTowers = true, spawnRandomPillars = true, disableDisadvantages = false, versionChecking = true, trapsBelowGroundOnly = false, showAllPillarsInCreative = false, enableWeaponKillStats = true, enableMobKillStats = true, enableCraftingStats = true;

	public static CreativeTabs tabModjamMod = new CreativeTabModjamMod(CreativeTabs.getNextID(), "The \"You Will Die\" Mod");

	public static Block blockPillar;
	public static Block blockTrap;
	public static Block mysteryMushroomBlock;

	public static Item itemPillar;
	public static Item mysteryPotion;
	public static Item itemTrap;
	public static Item mysteryMushroom;
	public static Item sceptre;

	public static Item crystalItem;
	public static Item scroll;

	public static Block crystal;
	public static Block crystalStand;

	public static Achievement startTheGame;
	public static Achievement losingIsFun;
	public static Achievement whoops;

	public static Achievement theHunt;
	public static Achievement jackOfAllTrades;

	public static AchievementPage page;

	public static String version = "v0.0.3e";
	public static String foundVersion = "v0.0.3e";
	public static boolean newerVersion = false;

	public static String configPath = null;

	public static final String versionOrder = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		File old = new File(event.getSuggestedConfigurationFile().getAbsolutePath().replace("fyresmodjam", "YouWillDieMod"));
		if(old.exists()) {old.delete(); System.out.println(true);}

		configPath = event.getSuggestedConfigurationFile().getAbsolutePath().replace("fyresmodjam", "TheYouWillDieMod");

		Configuration config = new Configuration(new File(configPath));

		config.load();

		proxy.loadFromConfig(config);

		config.save();

		if(versionChecking) {
			InputStream in = null;
			BufferedReader reader = null;

			try {
				in = new URL("https://dl.dropboxusercontent.com/s/n30va53f6uh2mki/versions.txt?token_hash=AAE89oZXZUV7Khx4mAbLhJS1Q4UuMZW2CXAO52yW1Ef9fw").openStream();
				reader = new BufferedReader(new InputStreamReader(in));

				String inputLine;
				while((inputLine = reader.readLine()) != null && !inputLine.startsWith("YWDMod")) {}
				if(inputLine != null) {foundVersion = inputLine.split("=")[1];}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if(reader != null) {reader.close();}

					if(in != null) {in.close();}
				} catch (Exception e) {e.printStackTrace();}
			}

			String[] versionSplit = version.replace("v", "").split("\\."), foundSplit = foundVersion.replace("v", "").split("\\.");

			if(!version.equals(foundVersion) && (Integer.parseInt(versionSplit[0]) < Integer.parseInt(foundSplit[0]) ? (Integer.parseInt(versionSplit[1]) < Integer.parseInt(foundSplit[1]) ? (Integer.parseInt("" + versionSplit[2].charAt(0)) < Integer.parseInt("" + foundSplit[2].charAt(0)) ? (versionOrder.indexOf(versionSplit[2].charAt(1)) < versionOrder.indexOf(foundSplit[2].charAt(1))) : false) : false) : false)) {
				System.out.println("A newer version of the \"You Will Die\" Mod has been found (" + foundVersion + ").");
				newerVersion = true;
			} else {
				System.out.println("No newer version of the \"You Will Die\" Mod has been found.");
			}
		} else {System.out.println("\"You Will\" Die Mod version checking disabled.");}

		//Item and Block loading

		blockPillar = new BlockPillar().setBlockUnbreakable().setResistance(6000000.0F);
		blockTrap = new BlockTrap().setBlockUnbreakable().setResistance(6000000.0F);
		mysteryMushroomBlock = new BlockMysteryMushroom().setHardness(0.0F).setStepSound(Block.soundTypeGrass).setLightLevel(0.125F).setBlockName("mysteryMushroomBlock");

		/*crystal = new BlockCrystal(blockID + 3);
				GameRegistry.registerBlock(crystal, "crystal");
				GameRegistry.registerTileEntity(TileEntityCrystal.class, "Crystal Tile Entity");
				LanguageRegistry.addName(crystal, "Crystal");

				crystalStand = new BlockCrystalStand(blockID + 4).setCreativeTab(CreativeTabs.tabDecorations);
				GameRegistry.registerBlock(crystalStand, "crystalStand");
				GameRegistry.registerTileEntity(TileEntityCrystalStand.class, "Crystal Stand Tile Entity");
				LanguageRegistry.addName(crystalStand, "Crystal Stand");*/

		itemPillar = new ItemPillar().setUnlocalizedName("blockPillar");
		GameRegistry.registerItem(itemPillar, "itemPillar");

		mysteryPotion = new ItemMysteryPotion().setUnlocalizedName("mysteryPotion").setCreativeTab(CreativeTabs.tabBrewing);
		GameRegistry.registerItem(mysteryPotion, "mysteryPotion");

		itemTrap = new ItemTrap().setUnlocalizedName("itemTrap").setCreativeTab(CreativeTabs.tabBlock);
		GameRegistry.registerItem(itemTrap, "itemTrap");

		mysteryMushroom = new ItemMysteryMushroom().setUnlocalizedName("mysteryMushroom").setCreativeTab(CreativeTabs.tabBrewing);
		GameRegistry.registerItem(mysteryMushroom, "mysteryMushroom");

		sceptre = new ItemObsidianSceptre().setUnlocalizedName("sceptre").setCreativeTab(CreativeTabs.tabTools).setFull3D();
		GameRegistry.registerItem(sceptre, "sceptre");

		//crystalItem = new ItemCrystal(crystal.blockID - 256);

		//scroll = new ItemScroll(itemID + 5).setUnlocalizedName("scroll").setCreativeTab(CreativeTabs.tabMisc);
		//LanguageRegistry.addName(scroll, "Scroll");

		GameRegistry.registerBlock(blockPillar, "blockPillar");
		GameRegistry.registerTileEntity(TileEntityPillar.class, "Pillar Tile Entity");

		GameRegistry.registerBlock(blockTrap, "blockTrap");
		GameRegistry.registerTileEntity(TileEntityTrap.class, "Trap Entity");

		GameRegistry.registerBlock(mysteryMushroomBlock, "mysteryMushroomBlock");

		startTheGame = getNewAchievement(achievementID, 0, 0, new ItemStack(Items.iron_sword, 1), "startTheGame", "You Will Die", "Join a world with this mod installed", null, true);
		losingIsFun = getNewAchievement(achievementID + 1, -2, 0, new ItemStack(itemTrap, 1), "losingIsFun", "Losing Is Fun", "Experience \"fun\"", startTheGame, false);
		whoops = getNewAchievement(achievementID + 2, 2, 0, new ItemStack(itemTrap, 1, 1), "whoops", "Whoops", "Fail to disarm a trap", startTheGame, false);

		theHunt = getNewAchievement(achievementID + 3, 0, -2, new ItemStack(Items.bow, 1), "theHunt", "The Hunt", "Become a competent slayer of 5 or more different creatures", startTheGame, false);
		jackOfAllTrades = getNewAchievement(achievementID + 4, 0, 2, new ItemStack(Blocks.crafting_table, 1), "jackOfAllTrades", "Jack of All Trades", "Become a novice user of at least 10 different weapons", startTheGame, false);

		page = new AchievementPage("The \"You Will Die\" Mod", startTheGame, losingIsFun, whoops, theHunt, jackOfAllTrades);

		AchievementPage.registerAchievementPage(page);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

		//Registering

		CommonTickHandler commonHandler = new CommonTickHandler();
		FMLCommonHandler.instance().bus().register(commonHandler);

		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);

		new ItemStatHelper().register();
		new EntityStatHelper().register();

		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GUIHandler());

		GameRegistry.registerWorldGenerator(new PillarGen(), 0);

		GameRegistry.registerWorldGenerator(new WorldGenTrapsTowersAndMore(), 0);

		for(int i = 0; i < 3; i++) {GameRegistry.registerWorldGenerator(new WorldGenMoreDungeons(), 0);}

		EntityRegistry.registerGlobalEntityID(EntityMysteryPotion.class, "MysteryPotion", EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(EntityMysteryPotion.class, "MysteryPotion", 0, instance, 128, 1, true);

		GameRegistry.addShapelessRecipe(new ItemStack(itemTrap, 1, 0), new Object[] {Blocks.heavy_weighted_pressure_plate, Blocks.cactus});
		GameRegistry.addShapelessRecipe(new ItemStack(itemTrap, 1, 1), new Object[] {Blocks.heavy_weighted_pressure_plate, Blocks.torch});
		GameRegistry.addShapelessRecipe(new ItemStack(itemTrap, 1, 2), new Object[] {Blocks.heavy_weighted_pressure_plate, new ItemStack(Items.dye, 1, 0)});

		for(int i = 0; i < 13; i++) {
			GameRegistry.addShapelessRecipe(new ItemStack(mysteryPotion, 1, i + 13), new Object[] {new ItemStack(mysteryPotion, 1, i), Items.gunpowder});
			GameRegistry.addShapelessRecipe(new ItemStack(mysteryPotion, 1, i), new Object[] {new ItemStack(Items.potionitem, 1, 0), Items.leather, new ItemStack(mysteryMushroom, 1, i)});
		}

		GameRegistry.addRecipe(new ItemStack(sceptre, 1, 0), "X", "Y", "X", 'X', Blocks.obsidian, 'Y', Blocks.end_stone);
		GameRegistry.addShapelessRecipe(new ItemStack(sceptre, 1, 1), new Object[] {new ItemStack(sceptre, 1, 0), Items.ender_pearl, Items.book});

		proxy.register();

		//Entity Trackers

		EntityStatTracker playerTracker = new EntityStatTracker(EntityPlayer.class, true);

		playerTracker.addStat(new EntityStat("BlessingCooldown", "" + 0));
		playerTracker.addStat(new EntityStat("BlessingCounter", "" + 0));

		EntityStatTracker mobTracker = new EntityStatTracker(EntityMob.class, true);

		mobTracker.addStat(new EntityStat("Level", "") {
			@Override
			public Object getNewValue(Random r) {
				int i = 1;
				for(; i < 5; i++) {if(ModjamMod.r.nextInt(5) < 3) {break;}}
				return i;
			}

			@Override
			public String getAlteredEntityName(EntityLiving entity) {
				int level = 1;

				try {
					level = Integer.parseInt(entity.getEntityData().getString(name));
				} catch (Exception e) {e.printStackTrace();}

				return (level == 5 ? "\u00A7c" : "") + entity.getCommandSenderName() + ", Level " + level;
			}

			@Override
			public void modifyEntity(Entity entity) {
				int level = 1;

				try {
					level = Integer.parseInt(entity.getEntityData().getString(name));
				} catch (Exception e) {e.printStackTrace();}

				int healthGain = (int) ((level - 1) * (((EntityLivingBase) entity).getMaxHealth()/4) + (level == 5 ? ((EntityLivingBase) entity).getMaxHealth()/4 : 0));

				if(healthGain != 0) {
					((EntityLivingBase) entity).getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(((EntityLivingBase) entity).getMaxHealth() + healthGain);
					((EntityLivingBase) entity).setHealth(((EntityLivingBase) entity).getMaxHealth() + healthGain);
				}

				if(level == 5) {
					switch(r.nextInt(4)) {

					case 0:
						if(entity instanceof IRangedAttackMob) {entity.getEntityData().setString("Blessing", "Hunter");}
						else {entity.getEntityData().setString("Blessing", "Warrior");}
						break;

					case 1: entity.getEntityData().setString("Blessing", "Swamp"); break;
					case 2: entity.getEntityData().setString("Blessing", "Guardian"); break;
					case 3: entity.getEntityData().setString("Blessing", "Vampire"); break;

					default: break;

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

		ItemStatTracker weaponTracker = new ItemStatTracker(new Class[] {ItemSword.class, ItemAxe.class, ItemBow.class}, true);

		weaponTracker.addStat(new ItemStat("Rank", "") {

			public String[][] prefixesByRank = {
					{"Old", "Dull", "Broken", "Worn"},
					{"Average", "Decent", "Modest", "Ordinary"},
					{"Strong", "Sharp", "Polished", "Refined"},
					{"Powerful", "Ruthless", "Elite", "Astonishing"},
					{"Godly", "Divine", "Fabled", "Legendary"}
			};

			@Override
			public Object getNewValue(ItemStack stack, Random r) {
				int i = 1;
				for(; i < 5; i++) {if(ModjamMod.r.nextInt(10) < 7) {break;}}
				return i;
			}

			@Override
			public void modifyStack(ItemStack stack, Random r) {
				int rank = Integer.parseInt(stack.getTagCompound().getString(name));
				float bonusDamage = ((float) rank - 1)/2 + (r.nextInt(rank + 1) * r.nextFloat());

				ItemStatHelper.giveStat(stack, "BonusDamage", String.format("%.2f", bonusDamage));
				ItemStatHelper.addLore(stack, !String.format("%.2f", bonusDamage).equals("0.00") ? "\u00A77\u00A7o  " + (bonusDamage > 0 ? "+" : "") + String.format("%.2f", bonusDamage) + " bonus damage" : null);

				ItemStatHelper.addLore(stack, "\u00A7eRank: "+ rank);
			}

			@Override
			public String getAlteredStackName(ItemStack stack, Random r) {
				String[] list = prefixesByRank[Integer.parseInt(stack.getTagCompound().getString(name)) - 1];
				String prefix = list[r.nextInt(list.length)];

				if(prefix.equals("Sharp") && stack.getItem() instanceof ItemBow) {prefix = "Long";}

				return "\u00A7f" + prefix + " " + stack.getDisplayName();
			}

		});

		ItemStatHelper.addStatTracker(weaponTracker);

		ItemStatTracker armorTracker = new ItemStatTracker(new Class[] {ItemArmor.class}, true);

		armorTracker.addStat(new ItemStat("Rank", "") {

			public String[][] prefixesByRank = {
					{"Old", "Broken", "Worn", "Weak"},
					{"Average", "Decent", "Modest", "Ordinary"},
					{"Polished", "Tough", "Hardened", "Durable"},
					{"Elite", "Astonishing", "Reinforced", "Resilient"},
					{"Godly", "Divine", "Fabled", "Legendary"}
			};

			@Override
			public Object getNewValue(ItemStack stack, Random r) {
				int i = 1;
				for(; i < 5; i++) {if(ModjamMod.r.nextInt(10) < 7) {break;}}
				return i;
			}

			@Override
			public void modifyStack(ItemStack stack, Random r) {
				int rank = Integer.parseInt(stack.getTagCompound().getString(name));
				float damageReduction = (rank - 1) + r.nextFloat() * 0.5F;

				ItemStatHelper.giveStat(stack, "DamageReduction", String.format("%.2f", damageReduction));
				ItemStatHelper.addLore(stack, !String.format("%.2f", damageReduction).equals("0.00") ? "\u00A77\u00A7o  " + (damageReduction > 0 ? "+" : "") + String.format("%.2f", damageReduction) + "% damage reduction" : null);

				ItemStatHelper.addLore(stack, "\u00A7eRank: "+ rank);
			}

			@Override
			public String getAlteredStackName(ItemStack stack, Random r) {
				String[] list = prefixesByRank[Integer.parseInt(stack.getTagCompound().getString(name)) - 1];
				String prefix = list[r.nextInt(list.length)];

				if(prefix.equals("Sharp") && stack.getItem() instanceof ItemBow) {prefix = "Long";}

				return "\u00A7f" + prefix + " " + stack.getDisplayName();
			}

		});

		ItemStatHelper.addStatTracker(armorTracker);

		//Other

		for(int i = 0; i < 13; i++) {
			ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(new WeightedRandomChestContent(mysteryPotion, i, 1, 3, 2));
			WorldGenTrapsTowersAndMore.chestGenInfo.addItem(new WeightedRandomChestContent(mysteryPotion, i, 1, 3, 2));
		}
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

	}

	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		
		if(!player.worldObj.isRemote) {
			NewPacketHandler.UPDATE_WORLD_DATA.sendToPlayer(player, CommonTickHandler.worldData.potionValues, CommonTickHandler.worldData.potionDurations, CommonTickHandler.worldData.getDisadvantage(), CommonTickHandler.worldData.currentTask, CommonTickHandler.worldData.currentTaskID, CommonTickHandler.worldData.currentTaskAmount, CommonTickHandler.worldData.progress, CommonTickHandler.worldData.tasksCompleted, CommonTickHandler.worldData.enderDragonKilled, ModjamMod.spawnTraps, CommonTickHandler.worldData.rewardLevels, CommonTickHandler.worldData.mushroomColors);

			String name = CommonTickHandler.worldData.currentTask.equals("Kill") ? FyresWorldData.validMobNames[CommonTickHandler.worldData.currentTaskID] : FyresWorldData.validItems[CommonTickHandler.worldData.currentTaskID].getDisplayName();

			if(CommonTickHandler.worldData.currentTaskAmount > 1) {
				if(name.contains("Block")) {name = name.replace("Block", "Blocks").replace("block", "blocks");}
				else {name += "s";}
			}

			int index = -1;
			for(int i = 0; i < FyresWorldData.validDisadvantages.length; i++) {if(FyresWorldData.validDisadvantages[i].equals(CommonTickHandler.worldData.getDisadvantage())) {index = i; break;}}

			NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "\u00A7eWorld disadvantage: " + CommonTickHandler.worldData.getDisadvantage() + (index == -1 ? "" : " (" + FyresWorldData.disadvantageDescriptions[index] + ")"));

			NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "\u00A7eWorld goal: " + CommonTickHandler.worldData.currentTask + " " + CommonTickHandler.worldData.currentTaskAmount + " " + name + ". (" + CommonTickHandler.worldData.progress + " " + CommonTickHandler.worldData.currentTask + "ed)");
			
			if(!player.getEntityData().hasKey("Blessing")) {
				player.getEntityData().setString("Blessing", TileEntityPillar.validBlessings[ModjamMod.r.nextInt(TileEntityPillar.validBlessings.length)]);
				while(player.getEntityData().getString("Blessing").equals("Inferno")) {player.getEntityData().setString("Blessing", TileEntityPillar.validBlessings[ModjamMod.r.nextInt(TileEntityPillar.validBlessings.length)]);}

				NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "\u00A72You've been granted the Blessing of the " + player.getEntityData().getString("Blessing") + ". (Use /currentBlessing to check effect)");
			}

			NewPacketHandler.UPDATE_BLESSING.sendToPlayer(player, player.getEntityData().getString("Blessing"));
			
			if(EntityStatHelper.hasStat(player, "BlessingCounter")) {NewPacketHandler.UPDATE_STAT.sendToPlayer(player, "BlessingCounter", EntityStatHelper.getStat(player, "BlessingCounter"));}

			if(!player.getEntityData().hasKey("PotionKnowledge")) {player.getEntityData().setIntArray("PotionKnowledge", new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});}

			NewPacketHandler.UPDATE_POTION_KNOWLEDGE.sendToPlayer(player, player.getEntityData().getIntArray("PotionKnowledge"));
		}

		if(versionChecking && newerVersion) {player.addChatComponentMessage(new ChatComponentTranslation("fyresmodjam.newVersion"));}

		player.triggerAchievement(startTheGame);
	}

	@SubscribeEvent
	public void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
		EntityPlayer player = event.player;

		NewPacketHandler.UPDATE_WORLD_DATA.sendToPlayer(player, CommonTickHandler.worldData.potionValues, CommonTickHandler.worldData.potionDurations, CommonTickHandler.worldData.getDisadvantage(), CommonTickHandler.worldData.currentTask, CommonTickHandler.worldData.currentTaskID, CommonTickHandler.worldData.currentTaskAmount, CommonTickHandler.worldData.progress, CommonTickHandler.worldData.tasksCompleted, CommonTickHandler.worldData.enderDragonKilled, ModjamMod.spawnTraps, CommonTickHandler.worldData.rewardLevels, CommonTickHandler.worldData.mushroomColors);

		NewPacketHandler.UPDATE_BLESSING.sendToPlayer(player, player.getEntityData().getString("Blessing"));
		
		if(EntityStatHelper.hasStat(player, "BlessingCounter")) {NewPacketHandler.UPDATE_STAT.sendToPlayer(player, "BlessingCounter", EntityStatHelper.getStat(player, "BlessingCounter"));}

		if(!player.getEntityData().hasKey("PotionKnowledge")) {player.getEntityData().setIntArray("PotionKnowledge", new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1});}

		NewPacketHandler.UPDATE_POTION_KNOWLEDGE.sendToPlayer(player, player.getEntityData().getIntArray("PotionKnowledge"));
	}

	@SubscribeEvent
	public void checkBreakSpeed(PlayerEvent.BreakSpeed event) {
		if(event.entityPlayer != null && event.entityPlayer.getEntityData().hasKey("Blessing")) {
			String blessing = event.entityPlayer.getEntityData().getString("Blessing");

			if(blessing.equals("Miner")) {
				if(event.block.getMaterial() == Material.rock || event.block.getMaterial() == Material.iron) {event.newSpeed = event.originalSpeed * 1.25F;}
			} else if(blessing.equals("Lumberjack")) {
				if(event.block.getMaterial() == Material.wood) {event.newSpeed = event.originalSpeed * 1.25F;}
			}
		}
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		initCommands(event);
	}

	public void initCommands(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandCurrentBlessing());
		event.registerServerCommand(new CommandCurrentDisadvantage());
		event.registerServerCommand(new CommandCurrentWorldTask());
		event.registerServerCommand(new CommandKillStats());
		event.registerServerCommand(new CommandWeaponStats());
		event.registerServerCommand(new CommandCraftingStats());
	}

	@SuppressWarnings("deprecation")
	public static Achievement getNewAchievement(int id, int x, int y, ItemStack stack, String name, String displayName, String desc, Achievement prereq, boolean independent) {
		Achievement achievement = new Achievement("YWD-" + id, name, x, y, stack, prereq);
		if(independent) {achievement = achievement.initIndependentStat();}
		LanguageRegistry.instance().addStringLocalization("achievement." + name, "en_US", displayName);
		LanguageRegistry.instance().addStringLocalization("achievement." + name + ".desc", "en_US", desc);
		achievement.registerStat();
		return achievement;
	}
}
