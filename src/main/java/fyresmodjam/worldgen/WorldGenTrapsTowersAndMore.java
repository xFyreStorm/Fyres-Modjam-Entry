package fyresmodjam.worldgen;

import cpw.mods.fml.common.IWorldGenerator;
import fyresmodjam.ModjamMod;
import fyresmodjam.blocks.BlockTrap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DungeonHooks;

import java.util.Random;

public class WorldGenTrapsTowersAndMore implements IWorldGenerator
{
    // @formatter:off
    public static final WeightedRandomChestContent[] field_111189_a = new WeightedRandomChestContent[]
        {
            new WeightedRandomChestContent(Item.ingotIron.itemID, 0, 1, 4, 10),
            new WeightedRandomChestContent(Item.bread.itemID, 0, 1, 1, 10),
            new WeightedRandomChestContent(Item.wheat.itemID, 0, 1, 4, 10),
            new WeightedRandomChestContent(Item.gunpowder.itemID, 0, 1, 4, 10),
            new WeightedRandomChestContent(Item.silk.itemID, 0, 1, 4, 10),
            new WeightedRandomChestContent(Item.bucketEmpty.itemID, 0, 1, 1, 10),
            new WeightedRandomChestContent(Item.redstone.itemID, 0, 1, 4, 10),
            new WeightedRandomChestContent(Item.nameTag.itemID, 0, 1, 1, 10)
        };
    // @formatter:on

    public static boolean genning = false;

    public static final String TOWER_CHESTS = "towerChests";

    public static ChestGenHooks chestGenInfo = new ChestGenHooks(TOWER_CHESTS, field_111189_a, 8, 8);

    // @formatter:off
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        genning = true;

        boolean addedDungeon = !ModjamMod.spawnTowers || random.nextInt(ModjamMod.towerGenChance) != 0;

        for (int y = 1; y < 127; y++)
        {
            for (int x = chunkX * 16; x < chunkX * 16 + 16; x++)
            {
                for (int z = chunkZ * 16; z < chunkZ * 16 + 16; z++)
                {
                    if ((world.isAirBlock(x, y, z) || (Block.blocksList[world.getBlockId(x, y, z)].isBlockReplaceable(world, x, y, z) && world.getBlockId(x, y, z) != Block.waterStill.blockID && world.getBlockId(x, y, z) != Block.waterMoving.blockID && world.getBlockId(x, y, z) != Block.lavaStill.blockID && world.getBlockId(x, y, z) != Block.lavaMoving.blockID)) && (!world.isAirBlock(x, y - 1, z) && world.getBlockId(x, y - 1, z) != ModjamMod.blockTrap.blockID && !Block.blocksList[world.getBlockId(x, y - 1, z)].isBlockReplaceable(world, x, y - 1, z)) && random.nextInt(ModjamMod.trapGenChance) == 0)
                    {
                        boolean skip = ModjamMod.trapsBelowGroundOnly && (world.getBlockId(x, y - 1, z) == Block.grass.blockID || world.getBlockId(x, y - 1, z) == Block.sand.blockID || world.getBlockId(x, y - 1, z) == Block.wood.blockID || world.canBlockSeeTheSky(x, y, z));
                        if (!skip && ModjamMod.blockTrap.canPlaceBlockAt(world, x, y, z)) {world.setBlock(x, y, z, ModjamMod.blockTrap.blockID, random.nextInt(BlockTrap.trapTypes), 0);}
                    }

                    if ((world.getBlockId(x, y, z) == Block.mushroomBrown.blockID || world.getBlockId(x, y, z) == Block.mushroomRed.blockID) && random.nextInt(ModjamMod.mushroomReplaceChance) == 0)
                    {
                        world.setBlock(x, y, z, ModjamMod.mysteryMushroomBlock.blockID, random.nextInt(13), 0);
                    }

                    if (!addedDungeon && ((world.getBlockId(x, y, z) == Block.grass.blockID || ((world.getBlockId(x, y, z) == Block.sand.blockID || world.getBlockId(x, y, z) == Block.netherrack.blockID || world.getBlockId(x, y, z) == Block.slowSand.blockID || world.getBlockId(x, y, z) == Block.gravel.blockID) && world.isAirBlock(x, y + 1, z)))) && world.getBlockId(x, y + 1, z) != Block.waterStill.blockID && world.getBlockId(x, y + 1, z) != Block.waterMoving.blockID && world.getBlockId(x, y + 1, z) != Block.lavaStill.blockID && world.getBlockId(x, y + 1, z) != Block.lavaMoving.blockID && ModjamMod.r.nextInt(100) == 0)
                    {
                        boolean obsidian = !world.provider.isHellWorld && ModjamMod.r.nextInt(100) == 0;

                        y--;

                        int floors = 3 + random.nextInt(6);

                        for (int y2 = 0; y2 <= floors * 6; y2++)
                        {
                            for (int x2 = -5; x2 <= 5; x2++)
                            {
                                for (int z2 = -5; z2 <= 5; z2++)
                                {
                                    if (((y2 / 6) % 2 == 0 ^ y2 % 6 < 2) && x2 == -2 && z2 == -5 && y2 > 1 && y2 <= floors * 6 - 5)
                                    {
                                        world.setBlock(x + x2, y + y2, z + z2 + 2, Block.ladder.blockID, 3, 0);
                                    }

                                    if (((y2 / 6) % 2 == 1 ^ y2 % 6 < 2) && x2 == 2 && z2 == 5 && y2 > 1 && y2 <= floors * 6 - 5)
                                    {
                                        world.setBlock(x + x2, y + y2, z + z2 - 2, Block.ladder.blockID, 2, 0);
                                    }

                                    if ((x2 * x2 + z2 * z2 <= 25) && ((y2 % 6 == 0 || y2 % 6 == 1) || z2 > 3 + (y2 < 6 ? 1 : 0) || z2 < -3 || Math.abs(x2) > 3 + (y2 < 5 ? 1 : 0)))
                                    {
                                        if (world.getBlockId(x + x2, y + y2, z + z2) != Block.ladder.blockID && world.getBlockId(x + x2, y + y2, z + z2) != Block.obsidian.blockID)
                                        {
                                            //if(y2 >= 5 && (y2 % 5 == 2 || y2 % 5 == 3) && (Math.abs(x2) == 2 || Math.abs(z2) == 2)) {
                                            //	world.setBlock(x + x2, y + y2, z + z2, Block.fenceIron.blockID);
                                            //} else {
                                            if (!obsidian && Math.abs(x2) <= 1 && Math.abs(z2) <= 1 && y2 != floors * 6 && y2 != 1 && y2 % 6 == 1)
                                            {
                                                world.setBlock(x + x2, y + y2, z + z2, Block.obsidian.blockID);
                                            }
                                            else
                                            {
                                                world.setBlock(x + x2, y + y2, z + z2, !world.provider.isHellWorld ? (!obsidian ? (random.nextBoolean() ? Block.cobblestoneMossy.blockID : Block.cobblestone.blockID) : Block.obsidian.blockID) : Block.netherBrick.blockID);
                                            }
                                            //}
                                        }
                                    }
                                    else if (y2 % 6 == 2 && x2 == 0 && z2 == ((y2 / 6) % 2 == 1 ? 3 : -3) && (y2 / 6 >= floors - 1 || random.nextInt(3) == 0) && y2 >= 5)
                                    {
                                        world.setBlock(x + x2, y + y2, z + z2, Block.chest.blockID, 0, 2);

                                        boolean b = (y2 / 6) % 2 == 1;

                                        TileEntityChest tileentitychest = (TileEntityChest) world.getBlockTileEntity(x + x2, y + y2, z + z2);

                                        if (tileentitychest != null)
                                        {
                                            //ChestGenHooks info = ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST);
                                            WeightedRandomChestContent.generateChestContents(random, ((y2 / 6 >= floors - 1 || obsidian) ? ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST) : chestGenInfo).getItems(random), tileentitychest, chestGenInfo.getCount(random));
                                        }

                                        if (!obsidian)
                                        {
                                            world.setBlock(x + x2, y + y2 - 1, z + z2, Block.obsidian.blockID);
                                            world.setBlock(x + x2, y + y2 + 2, z + z2, Block.obsidian.blockID);
                                            world.setBlock(x + x2, y + y2, z + z2 + (b ? 1 : -1), Block.obsidian.blockID);
                                            world.setBlock(x + x2, y + y2 + 1, z + z2 + (b ? 1 : -1), Block.obsidian.blockID);
                                            world.setBlock(x + x2 + 1, y + y2, z + z2, Block.obsidian.blockID);
                                            world.setBlock(x + x2 - 1, y + y2, z + z2, Block.obsidian.blockID);
                                            world.setBlock(x + x2 + 1, y + y2 + 1, z + z2, Block.obsidian.blockID);
                                            world.setBlock(x + x2 - 1, y + y2 + 1, z + z2, Block.obsidian.blockID);
                                        }
                                    }
                                    else if (y2 % 6 == 2 && x2 == 0 && z2 == 0)
                                    {
                                        if (y2 != 2)
                                        {
                                            world.setBlock(x + x2, y + y2, z + z2, Block.mobSpawner.blockID, 0, 2);
                                            TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner) world.getBlockTileEntity(x + x2, y + y2, z + z2);

                                            if (tileentitymobspawner != null)
                                            {
                                                tileentitymobspawner.getSpawnerLogic().setMobID(DungeonHooks.getRandomDungeonMob(random));
                                            }
                                        }
                                        else
                                        {
                                            world.setBlock(x + x2, y + y2, z + z2, ModjamMod.blockPillar.blockID);
                                            world.setBlockMetadataWithNotify(x + x2, y + y2, z + z2, 0, 0);

                                            world.setBlock(x + x2, y + y2 + 1, z + z2, ModjamMod.blockPillar.blockID);
                                            world.setBlockMetadataWithNotify(x + x2, y + y2 + 1, z + z2, 1, 0);
                                        }
                                    }
                                    else if ((x2 * x2 + z2 * z2 <= 25) && world.getBlockId(x + x2, y + y2, z + z2) != ModjamMod.blockPillar.blockID && world.getBlockId(x + x2, y + y2, z + z2) != Block.mobSpawner.blockID && world.getBlockId(x + x2, y + y2, z + z2) != Block.ladder.blockID && world.getBlockId(x + x2, y + y2, z + z2) != Block.chest.blockID && world.getBlockId(x + x2, y + y2, z + z2) != Block.obsidian.blockID)
                                    {
                                        world.setBlockToAir(x + x2, y + y2, z + z2);
                                    }

                                }
                            }
                        }

                        int changes = 0, y2 = -1;

                        do
                        {
                            changes = 0;

                            for (int x2 = -5; x2 <= 5; x2++)
                            {
                                for (int z2 = -5; z2 <= 5; z2++)
                                {
                                    if (x2 * x2 + z2 * z2 <= 25 && world.isAirBlock(x + x2, y + y2, z + z2))
                                    {
                                        world.setBlock(x + x2, y + y2, z + z2, !obsidian ? (random.nextBoolean() ? Block.cobblestoneMossy.blockID : Block.cobblestone.blockID) : Block.obsidian.blockID);
                                        changes++;
                                    }
                                }
                            }

                            y2--;
                        } while (changes != 0 && y + y2 >= 0);

                        addedDungeon = true;

                        y++;
                    }
                }
            }
        }

        genning = false;
    }
    // @formatter:on
}
