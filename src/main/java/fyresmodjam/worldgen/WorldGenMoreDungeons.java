package fyresmodjam.worldgen;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenDungeons;

import java.util.Random;

public class WorldGenMoreDungeons extends WorldGenDungeons implements IWorldGenerator
{
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {
        for (int k1 = 0; k1 < 24; ++k1)
        {
            int l1 = chunkX * 16 + random.nextInt(16) + 8;
            int i2 = random.nextInt(128);
            int j2 = chunkZ * 16 + random.nextInt(16) + 8;
            super.generate(world, random, l1, i2, j2);
        }
    }

}
