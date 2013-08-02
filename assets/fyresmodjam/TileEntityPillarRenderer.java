package assets.fyresmodjam;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class TileEntityPillarRenderer extends TileEntitySpecialRenderer
{
    private ModelPillar modelPillar = new ModelPillar();

    public static ResourceLocation[] textures = {
    	new ResourceLocation("fyresmodjam", "textures/blocks/pillar.png"),
    	new ResourceLocation("fyresmodjam", "textures/blocks/pillarActive.png")
    };
    
    public void renderTileEntityAt(TileEntity tileEntity, double d, double d1, double d2, float f)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d, (float)d1, (float)d2);
        TileEntityPillar tileEntityYour = (TileEntityPillar) tileEntity;
        renderBlockYour(tileEntityYour, tileEntity.worldObj, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, ModjamMod.blockPillar);
        GL11.glPopMatrix();
    }

    public void renderBlockYour(TileEntityPillar tl, World world, int i, int j, int k, Block block)
    {
        Tessellator tessellator = Tessellator.instance;
        float f = block.getBlockBrightness(world, i, j, k);
        int l = world.getLightBrightnessForSkyBlocks(i, j, k, 0);
        int l1 = l % 65536;
        int l2 = l / 65536;
        tessellator.setColorOpaque_F(f, f, f);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)l1, (float)l2);
        int dir = world.getBlockMetadata(i, j, k) % 4;
       
        GL11.glPushMatrix();
        this.tileEntityRenderer.renderEngine.func_110577_a(tl.getBlockMetadata() % 2 == 0 ? textures[0] : textures[2]);
        this.modelPillar.render((Entity) null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }
}