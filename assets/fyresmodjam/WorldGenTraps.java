package assets.fyresmodjam;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenTraps implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		for(int y = 1, added = 0; y < 128; y++) {
    		for(int x = chunkX * 16; x < chunkX * 16 + 16; x++) {
    			for(int z = chunkZ * 16; z < chunkZ * 16 + 16; z++) {
    				if(random.nextInt(100) == 0 && (world.isAirBlock(x, y, z) || Block.blocksList[world.getBlockId(x, y, z)].isBlockReplaceable(world, x, y, z)) && (!world.isAirBlock(x, y - 1, z) && world.getBlockId(x, y - 1, z) != ModjamMod.blockTrap.blockID && !Block.blocksList[world.getBlockId(x, y - 1, z)].isBlockReplaceable(world, x, y - 1, z))) {
    					world.setBlock(x, y, z, ModjamMod.blockTrap.blockID);
    				}
    			}
    		}
		}
	}

}
