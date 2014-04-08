package fyresmodjam.worldgen;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DungeonHooks;
import cpw.mods.fml.common.IWorldGenerator;
import fyresmodjam.ModjamMod;
import fyresmodjam.blocks.BlockTrap;

public class WorldGenTrapsTowersAndMore implements IWorldGenerator {

	public static final WeightedRandomChestContent[] field_111189_a = new WeightedRandomChestContent[] {
		new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 4, 10),
		new WeightedRandomChestContent(Items.bread, 0, 1, 1, 10),
		new WeightedRandomChestContent(Items.wheat, 0, 1, 4, 10),
		new WeightedRandomChestContent(Items.gunpowder, 0, 1, 4, 10),
		new WeightedRandomChestContent(Items.string, 0, 1, 4, 10),
		new WeightedRandomChestContent(Items.bucket, 0, 1, 1, 10),
		new WeightedRandomChestContent(Items.redstone, 0, 1, 4, 10),
		new WeightedRandomChestContent(Items.name_tag, 0, 1, 1, 10),
	};

	public static boolean genning = false;

	public static final String TOWER_CHESTS = "towerChests";

	public static ChestGenHooks chestGenInfo = new ChestGenHooks(TOWER_CHESTS, field_111189_a, 8, 8);

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {

		genning = true;

		boolean addedDungeon = !ModjamMod.spawnTowers || random.nextInt(ModjamMod.towerGenChance) != 0;

		for(int y = 1; y < 127; y++) {
			for(int x = chunkX * 16; x < chunkX * 16 + 16; x++) {
				for(int z = chunkZ * 16; z < chunkZ * 16 + 16; z++) {
					if((world.isAirBlock(x, y, z) || (world.getBlock(x, y, z).isReplaceable(world, x, y, z) && world.getBlock(x, y, z) != Blocks.water && world.getBlock(x, y, z) != Blocks.flowing_water && world.getBlock(x, y, z) != Blocks.lava && world.getBlock(x, y, z) != Blocks.flowing_lava)) && (!world.isAirBlock(x, y - 1, z) && world.getBlock(x, y - 1, z) != ModjamMod.blockTrap && !world.getBlock(x, y - 1, z).isReplaceable(world, x, y - 1, z)) && random.nextInt(ModjamMod.trapGenChance) == 0) {
						boolean skip = ModjamMod.trapsBelowGroundOnly && (world.getBlock(x, y - 1, z) == Blocks.grass || world.getBlock(x, y - 1, z) == Blocks.sand || world.getBlock(x, y - 1, z) == Blocks.log || world.getBlock(x, y - 1, z) == Blocks.log2 || world.canBlockSeeTheSky(x, y, z));
						if(!skip && ModjamMod.blockTrap.canPlaceBlockAt(world, x, y, z)) {world.setBlock(x, y, z, ModjamMod.blockTrap, random.nextInt(BlockTrap.trapTypes), 0);}
					}

					if((world.getBlock(x, y, z) == Blocks.brown_mushroom || world.getBlock(x, y, z) == Blocks.red_mushroom) && random.nextInt(ModjamMod.mushroomReplaceChance) == 0) {
						world.setBlock(x, y, z, ModjamMod.mysteryMushroomBlock, random.nextInt(13), 0);
					}

					if(!addedDungeon && ((world.getBlock(x, y, z) == Blocks.grass || ((world.getBlock(x, y, z) == Blocks.sand || world.getBlock(x, y, z) == Blocks.netherrack || world.getBlock(x, y, z) == Blocks.soul_sand || world.getBlock(x, y, z) == Blocks.gravel) && world.isAirBlock(x, y + 1, z)))) && world.getBlock(x, y + 1, z) != Blocks.water && world.getBlock(x, y + 1, z) != Blocks.flowing_water && world.getBlock(x, y + 1, z) != Blocks.lava && world.getBlock(x, y + 1, z) != Blocks.flowing_lava && ModjamMod.r.nextInt(100) == 0) {

						boolean obsidian = !world.provider.isHellWorld && ModjamMod.r.nextInt(100) == 0;

						y--;

						int floors = 3 + random.nextInt(6);

						for(int y2 = 0; y2 <= floors * 6; y2++) {
							for(int x2 = -5; x2 <= 5; x2++) {
								for(int z2 = -5; z2 <= 5; z2++) {

									if(((y2/6) % 2 == 0 ^ y2 % 6 < 2) && x2 == -2 && z2 == -5 && y2 > 1 && y2 <= floors * 6 - 5) {
										world.setBlock(x + x2, y + y2, z + z2 + 2, Blocks.ladder, 3, 0);
									}

									if(((y2/6) % 2 == 1 ^ y2 % 6 < 2) && x2 == 2 && z2 == 5 && y2 > 1 && y2 <= floors * 6 - 5) {
										world.setBlock(x + x2, y + y2, z + z2 - 2, Blocks.ladder, 2, 0);
									}

									if((x2 * x2 + z2 * z2 <= 25) && ((y2 % 6 == 0 || y2 % 6 == 1) || z2 > 3 + (y2 < 6 ? 1 : 0) || z2 < -3 || Math.abs(x2) > 3 + (y2 < 5 ? 1 : 0))) {
										if(world.getBlock(x + x2, y + y2, z + z2) != Blocks.ladder && world.getBlock(x + x2, y + y2, z + z2) != Blocks.obsidian) {
											if(!obsidian && Math.abs(x2) <= 1 && Math.abs(z2) <= 1 && y2 != floors * 6 && y2 != 1 && y2 % 6 == 1) {
												world.setBlock(x + x2, y + y2, z + z2, Blocks.obsidian);
											} else {
												world.setBlock(x + x2, y + y2, z + z2, !world.provider.isHellWorld ? (!obsidian ? (random.nextBoolean() ? Blocks.mossy_cobblestone : Blocks.cobblestone) : Blocks.obsidian) : Blocks.nether_brick);
											}
										}
									} else if(y2 % 6 == 2 && x2 == 0 && z2 == ((y2/6) % 2 == 1 ? 3 : -3) && (y2/6 >= floors - 1 || random.nextInt(3) == 0) && y2 >= 5) {
										world.setBlock(x + x2, y + y2, z + z2, Blocks.chest, 0, 2);

										boolean b = (y2/6) % 2 == 1;

										TileEntityChest tileentitychest = (TileEntityChest) world.getTileEntity(x + x2, y + y2, z + z2);

										if(tileentitychest != null) {
											WeightedRandomChestContent.generateChestContents(random, ((y2/6 >= floors - 1 || obsidian) ? ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST) : chestGenInfo).getItems(random), tileentitychest, chestGenInfo.getCount(random));
										}

										if(!obsidian) {
											world.setBlock(x + x2, y + y2 - 1, z + z2, Blocks.obsidian);
											world.setBlock(x + x2, y + y2 + 2, z + z2, Blocks.obsidian);
											world.setBlock(x + x2, y + y2, z + z2 + (b ? 1 : -1), Blocks.obsidian);
											world.setBlock(x + x2, y + y2 + 1, z + z2 + (b ? 1 : -1), Blocks.obsidian);
											world.setBlock(x + x2 + 1, y + y2, z + z2, Blocks.obsidian);
											world.setBlock(x + x2 - 1, y + y2, z + z2, Blocks.obsidian);
											world.setBlock(x + x2 + 1, y + y2 + 1, z + z2, Blocks.obsidian);
											world.setBlock(x + x2 - 1, y + y2 + 1, z + z2, Blocks.obsidian);
										}
									} else if(y2 % 6 == 2 && x2 == 0 && z2 == 0) {
										if(y2 != 2) {
											world.setBlock(x + x2, y + y2, z + z2, Blocks.mob_spawner, 0, 2);
											TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)world.getTileEntity(x + x2, y + y2, z + z2);

											if(tileentitymobspawner != null) {
												tileentitymobspawner.func_145881_a().setEntityName(DungeonHooks.getRandomDungeonMob(ModjamMod.r));;
											}
										} else {
											world.setBlock(x + x2, y + y2, z + z2, ModjamMod.blockPillar);
											world.setBlockMetadataWithNotify(x + x2, y + y2, z + z2, 0, 0);

											world.setBlock(x + x2, y + y2 + 1, z + z2, ModjamMod.blockPillar);
											world.setBlockMetadataWithNotify(x + x2, y + y2 + 1, z + z2, 1, 0);
										}
									} else if((x2 * x2 + z2 * z2 <= 25) && world.getBlock(x + x2, y + y2, z + z2) != ModjamMod.blockPillar && world.getBlock(x + x2, y + y2, z + z2) != Blocks.mob_spawner && world.getBlock(x + x2, y + y2, z + z2) != Blocks.ladder && world.getBlock(x + x2, y + y2, z + z2) != Blocks.chest && world.getBlock(x + x2, y + y2, z + z2) != Blocks.obsidian) {
										world.setBlockToAir(x + x2, y + y2, z + z2);
									}

								}
							}
						}

						int changes = 0, y2 = -1;

						do {
							changes = 0;

							for(int x2 = -5; x2 <= 5; x2++) {
								for(int z2 = -5; z2 <= 5; z2++) {
									if(x2 * x2 + z2 * z2 <= 25 && world.isAirBlock(x + x2, y + y2, z + z2)) {world.setBlock(x + x2, y + y2, z + z2, !obsidian ? (random.nextBoolean() ? Blocks.mossy_cobblestone : Blocks.cobblestone) : Blocks.obsidian); changes++;}
								}
							}

							y2--;
						} while(changes != 0 && y + y2 >= 0);

						addedDungeon = true;

						y++;
					}
				}
			}
		}

		genning = false;
	}

}
