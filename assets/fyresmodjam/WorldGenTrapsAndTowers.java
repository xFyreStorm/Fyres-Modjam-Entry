package assets.fyresmodjam;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DungeonHooks;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenTrapsAndTowers implements IWorldGenerator {

	public static final WeightedRandomChestContent[] field_111189_a = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Item.saddle.itemID, 0, 1, 1, 10), new WeightedRandomChestContent(Item.ingotIron.itemID, 0, 1, 4, 10), new WeightedRandomChestContent(Item.bread.itemID, 0, 1, 1, 10), new WeightedRandomChestContent(Item.wheat.itemID, 0, 1, 4, 10), new WeightedRandomChestContent(Item.gunpowder.itemID, 0, 1, 4, 10), new WeightedRandomChestContent(Item.silk.itemID, 0, 1, 4, 10), new WeightedRandomChestContent(Item.bucketEmpty.itemID, 0, 1, 1, 10), new WeightedRandomChestContent(Item.appleGold.itemID, 0, 1, 1, 1), new WeightedRandomChestContent(Item.redstone.itemID, 0, 1, 4, 10), new WeightedRandomChestContent(Item.record13.itemID, 0, 1, 1, 10), new WeightedRandomChestContent(Item.recordCat.itemID, 0, 1, 1, 10), new WeightedRandomChestContent(Item.field_111212_ci.itemID, 0, 1, 1, 10), new WeightedRandomChestContent(Item.field_111216_cf.itemID, 0, 1, 1, 2), new WeightedRandomChestContent(Item.field_111215_ce.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.field_111213_cg.itemID, 0, 1, 1, 1)};
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		
		boolean addedDungeon = random.nextInt(100) != 0;
		
		for(int y = 1; y < 128; y++) {
    		for(int x = chunkX * 16; x < chunkX * 16 + 16; x++) {
    			for(int z = chunkZ * 16; z < chunkZ * 16 + 16; z++) {
    				if(random.nextInt(200) == 0 && (world.isAirBlock(x, y, z) || (Block.blocksList[world.getBlockId(x, y, z)].isBlockReplaceable(world, x, y, z) && world.getBlockId(x, y, z) != Block.waterStill.blockID && world.getBlockId(x, y, z) != Block.waterMoving.blockID && world.getBlockId(x, y, z) != Block.lavaStill.blockID && world.getBlockId(x, y, z) != Block.lavaMoving.blockID)) && (!world.isAirBlock(x, y - 1, z) && world.getBlockId(x, y - 1, z) != ModjamMod.blockTrap.blockID && !Block.blocksList[world.getBlockId(x, y - 1, z)].isBlockReplaceable(world, x, y - 1, z))) {
    					world.setBlock(x, y, z, ModjamMod.blockTrap.blockID, random.nextInt(BlockTrap.trapTypes), 0);
    				}

    				if(!addedDungeon && (world.getBlockId(x, y, z) == Block.grass.blockID || world.getBlockId(x, y, z) == Block.sand.blockID) && ModjamMod.r.nextInt(100) == 0) {
    					int floors = 3 + random.nextInt(5);

    					for(int y2 = 0; y2 <= floors * 5; y2++) {
    						for(int x2 = -4; x2 <= 4; x2++) {
    							for(int z2 = -4; z2 <= 4; z2++) {

    								if(y2 % 5 == 0 || x2 == -4 || x2 == 4 || z2 == -4 || z2 == 4) {
    									world.setBlock(x + x2, y + y2, z + z2, Block.cobblestoneMossy.blockID);
    									
    									if(x2 == 0 && z2 == -4 && y2 != 0 && y2 <= floors * 5 - 4) {
    										world.setBlock(x + x2, y + y2, z + z2 + 1, Block.ladder.blockID, Block.ladder.onBlockPlaced(world, x + x2, y + y2, z + z2 + 1, 0, 0, 0, 0, 0), 0);
    									}
    									
    								} else if(y2 % 5 == 1 && x2 == 0 && z2 == 3 && (y2/5 >= floors - 1 || random.nextInt(4) == 0)) {
    									world.setBlock(x + x2, y + y2, z + z2, Block.chest.blockID, 0, 2);
    									
    									TileEntityChest tileentitychest = (TileEntityChest) world.getBlockTileEntity(x + x2, y + y2, z + z2);

    									if(tileentitychest != null) {
    										ChestGenHooks info = ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST);
    										WeightedRandomChestContent.generateChestContents(random, info.getItems(random), tileentitychest, info.getCount(random));
    									}
    								} else if(y2 % 5 == 1 && x2 == 0 && z2 == 0) {
    									world.setBlock(x + x2, y + y2, z + z2, Block.mobSpawner.blockID, 0, 2);
    						            TileEntityMobSpawner tileentitymobspawner = (TileEntityMobSpawner)world.getBlockTileEntity(x + x2, y + y2, z + z2);

    						            if(tileentitymobspawner != null) {
    						                tileentitymobspawner.getSpawnerLogic().setMobID(DungeonHooks.getRandomDungeonMob(random));
    						            }
    								} else if(world.getBlockId(x + x2, y + y2, z + z2) != Block.mobSpawner.blockID && world.getBlockId(x + x2, y + y2, z + z2) != Block.ladder.blockID && world.getBlockId(x + x2, y + y2, z + z2) != Block.chest.blockID) {
    									world.setBlockToAir(x + x2, y + y2, z + z2);
    								}

    							}
	    					}
    					}
    					
    					addedDungeon = true;
    				}
    			}
    		}
		}
	}

}
