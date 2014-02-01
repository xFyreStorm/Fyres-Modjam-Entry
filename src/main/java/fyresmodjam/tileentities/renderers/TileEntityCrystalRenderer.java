package fyresmodjam.tileentities.renderers;

import fyresmodjam.ModjamMod;
import fyresmodjam.items.ItemCrystal;
import fyresmodjam.models.ModelCrystal;
import fyresmodjam.tileentities.TileEntityCrystal;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class TileEntityCrystalRenderer extends TileEntitySpecialRenderer
{
    private ModelCrystal modelCrystal = new ModelCrystal();

    public static ResourceLocation texture = new ResourceLocation("fyresmodjam", "textures/blocks/crystal.png");

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double d0, double d1, double d2, float f)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d0, (float) d1, (float) d2);

        TileEntityCrystal crystal = (TileEntityCrystal) tileentity;
        Block block = ModjamMod.crystal;
        World world = crystal.worldObj;

        Tessellator tessellator = Tessellator.instance;
        float f2 = block.getBlockBrightness(world, crystal.xCoord, crystal.yCoord, crystal.zCoord);
        int l = world.getLightBrightnessForSkyBlocks(crystal.xCoord, crystal.yCoord, crystal.zCoord, 0);
        int l1 = l % 65536;
        int l2 = l / 65536;
        tessellator.setColorOpaque_F(f2, f2, f2);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) l1, (float) l2);

        float f3 = (float) (world.getWorldInfo().getWorldTime() % 20) / 20.0F;
        GL11.glTranslatef(0.5F, -0.4F + (f3 > 0.5F ? 0.25F - 0.25F * f3 : 0.25F * f3), 0.5F);

        GL11.glRotatef((float) world.getWorldInfo().getWorldTime() % 360, 0.0F, 1.0F, 0.0F);

        Color color = ItemCrystal.colors[crystal.getBlockMetadata() % ItemCrystal.colors.length];
        GL11.glColor3f((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F);

        this.tileEntityRenderer.renderEngine.bindTexture(texture);
        modelCrystal.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

        GL11.glPopMatrix();
    }

}
