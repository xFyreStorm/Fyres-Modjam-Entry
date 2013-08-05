package assets.fyresmodjam;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenTowers implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if(random.nextInt(50) == 0) {
			boolean added = false;
			for(int y = 48; y < 128 && !added; y++) {
	    		for(int x = chunkX * 16; x < chunkX * 16 + 16 && !added; x++) {
	    			for(int z = chunkZ * 16; z < chunkZ * 16 + 16 && !added; z++) {
	    				if(world.getBlockId(x, y, z) == Block.grass.blockID && ModjamMod.r.nextInt(100) == 0) {
	    					int floors = 1 + ModjamMod.r.nextInt(6);
	    					
	    					for(int y2 = 0; y2 <= floors * 5; y++) {
	    						for(int x2 = -5; x2 <= 5; x++) {
	    							for(int z2 = -5; z2 <= 5; z++) {
	    	    						if(y2 % 5 == 0 || x2 == -5 || x2 == 5 || z2 == -5 || z2 == 5) {
	    	    							world.setBlock(Block.cobblestoneMossy.blockID, x, y, z);
	    	    						}
	    	    					}
		    					}
	    					}
	    					
	    					added = true;
	    				}
	    			}
	    		}
			}
		}
	}

}
