package assets.fyresmodjam;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenTrapsAndTowers implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		
		boolean addedDungeon = random.nextInt(50) != 0;
		
		for(int y = 1; y < 128; y++) {
    		for(int x = chunkX * 16; x < chunkX * 16 + 16; x++) {
    			for(int z = chunkZ * 16; z < chunkZ * 16 + 16; z++) {
    				if(random.nextInt(200) == 0 && (world.isAirBlock(x, y, z) || (Block.blocksList[world.getBlockId(x, y, z)].isBlockReplaceable(world, x, y, z) && world.getBlockId(x, y, z) != Block.waterStill.blockID && world.getBlockId(x, y, z) != Block.waterMoving.blockID && world.getBlockId(x, y, z) != Block.lavaStill.blockID && world.getBlockId(x, y, z) != Block.lavaMoving.blockID)) && (!world.isAirBlock(x, y - 1, z) && world.getBlockId(x, y - 1, z) != ModjamMod.blockTrap.blockID && !Block.blocksList[world.getBlockId(x, y - 1, z)].isBlockReplaceable(world, x, y - 1, z))) {
    					world.setBlock(x, y, z, ModjamMod.blockTrap.blockID, random.nextInt(BlockTrap.trapTypes), 0);
    				}
    				
    				if(addedDungeon && world.getBlockId(x, y, z) == Block.grass.blockID && ModjamMod.r.nextInt(100) == 0) {
    					int floors = 1 + ModjamMod.r.nextInt(6);
    					
    					for(int y2 = 0; y2 <= floors * 5; y2++) {
    						for(int x2 = -5; x2 <= 5; x2++) {
    							for(int z2 = -5; z2 <= 5; z2++) {
    	    						if(y2 % 5 == 0 || x2 == -5 || x2 == 5 || z2 == -5 || z2 == 5) {
    	    							world.setBlock(x + x2, y + y2, z + z2, Block.cobblestoneMossy.blockID);
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
