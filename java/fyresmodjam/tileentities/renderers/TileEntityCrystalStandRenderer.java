package fyresmodjam.tileentities.renderers;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import fyresmodjam.ModjamMod;
import fyresmodjam.models.ModelCrystalStand;
import fyresmodjam.tileentities.TileEntityCrystalStand;

public class TileEntityCrystalStandRenderer extends TileEntitySpecialRenderer {

	private ModelCrystalStand model = new ModelCrystalStand();

	public static ResourceLocation texture = new ResourceLocation("fyresmodjam", "textures/blocks/crystal_stand.png");

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d0, double d1, double d2, float f) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d0, (float) d1, (float) d2);

		TileEntityCrystalStand crystalStand = (TileEntityCrystalStand) tileentity;
		Block block = ModjamMod.crystalStand;
		World world = crystalStand.getWorldObj();

		Tessellator tessellator = Tessellator.instance;
		float f2 = block.getMixedBrightnessForBlock(world, crystalStand.xCoord, crystalStand.yCoord, crystalStand.zCoord);
		int l = world.getLightBrightnessForSkyBlocks(crystalStand.xCoord, crystalStand.yCoord, crystalStand.zCoord, 0);
		int l1 = l % 65536;
		int l2 = l / 65536;
		tessellator.setColorOpaque_F(f2, f2, f2);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, l1, l2);

		GL11.glTranslatef(0.5F, 1.5F, 0.5F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);

		bindTexture(texture);
		model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);

		GL11.glPopMatrix();
	}

}
