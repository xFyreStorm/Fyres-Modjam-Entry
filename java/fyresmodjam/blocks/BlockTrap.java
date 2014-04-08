package fyresmodjam.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import fyresmodjam.ModjamMod;
import fyresmodjam.handlers.CommonTickHandler;
import fyresmodjam.handlers.NewPacketHandler;
import fyresmodjam.items.ItemTrap;
import fyresmodjam.tileentities.TileEntityTrap;

public class BlockTrap extends BlockContainer implements IShearable {

	public static int trapTypes = 3;

	public BlockTrap() {
		super(Material.circuits);
		setLightOpacity(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		blockIcon = par1IconRegister.registerIcon("fyresmodjam:spikes2");
	}

	public int idDropped(int par1, Random par2Random, int par3) {
		return 0;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition object, World par1World, int par2, int par3, int par4) {
		return new ItemStack(ModjamMod.blockTrap);
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer par1EntityPlayer, int par2) {
		return false;
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)  {
		if(par5EntityPlayer.getHeldItem() == null) {
			TileEntity te = par1World.getTileEntity(par2, par3, par4);

			if(!par5EntityPlayer.isSneaking() && te != null && te instanceof TileEntityTrap && ((TileEntityTrap) te).placedBy != null && ((TileEntityTrap) te).placedBy.equals(par5EntityPlayer.getCommandSenderName())) {
				if(!par1World.isRemote) {
					((TileEntityTrap) te).setting++; if(((TileEntityTrap) te).setting >= TileEntityTrap.settings.length) {((TileEntityTrap) te).setting = 0;}

					te.markDirty();

					NewPacketHandler.SEND_MESSAGE.sendToPlayer(par5EntityPlayer, "\u00A7e\u00A7oSet to: " + TileEntityTrap.settings[((TileEntityTrap) te).setting] + ".");
				}
			} else if(par1World.isRemote) {
				NewPacketHandler.DISARM_TRAP.sendToServer(par2, par3, par4, par5EntityPlayer.getEntityData().hasKey("Blessing") && par5EntityPlayer.getEntityData().getString("Blessing").equals("Mechanic"));
			}
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityTrap();
	}

	@Override
	public int getMobilityFlag() {
		return 2;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity) {}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
		return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public MovingObjectPosition collisionRayTrace(World par1World, int par2, int par3, int par4, Vec3 par5Vec3, Vec3 par6Vec3) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		TileEntity te = par1World.getTileEntity(par2, par3, par4);
		return (player != null && te instanceof TileEntityTrap && (((TileEntityTrap) te).placedBy != null || !NewPacketHandler.trapsDisabled) && (player.getCommandSenderName().equals(((TileEntityTrap) te).placedBy) || player.isSneaking() || (player.getEntityData().hasKey("Blessing") && player.getEntityData().getString("Blessing").equals("Scout")))) ? super.collisionRayTrace(par1World, par2, par3, par4, par5Vec3, par6Vec3) : null;
	}

	@Override
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
		TileEntity te = par1World.getTileEntity(par2, par3, par4);

		boolean b1 = (((TileEntityTrap) te).placedBy == null || !par5Entity.getCommandSenderName().equals(((TileEntityTrap) te).placedBy));
		boolean b2 = ((TileEntityTrap) te).setting != 3 && (!(par5Entity instanceof EntityPlayer) || ((TileEntityTrap) te).setting < 2);
		boolean b3 = (ModjamMod.spawnTraps || ((TileEntityTrap) te).placedBy != null);
		boolean b4 = ((par5Entity instanceof EntityPlayer && !((EntityPlayer) par5Entity).capabilities.isCreativeMode) || par5Entity instanceof EntityMob);

		if(!par1World.isRemote && par5Entity.ridingEntity == null && te != null && te instanceof TileEntityTrap && b1 && b2 && b3 && b4) {

			int type = par1World.getBlockMetadata(par2, par3, par4);

			String blessing = null;
			if(par5Entity.getEntityData().hasKey("Blessing")) {blessing = par5Entity.getEntityData().getString("Blessing");}
			boolean scout = blessing != null && blessing.equals("Scout");

			if(par5Entity instanceof EntityPlayer) {
				if(type % trapTypes == 0) {
					par5Entity.attackEntityFrom(DamageSource.cactus, 8.0F + (scout ? 2.0F : 0.0F));
					if(ModjamMod.r.nextInt(8 - (scout ? 2 : 0)) == 0) {((EntityLivingBase) par5Entity).addPotionEffect(new PotionEffect(Potion.poison.id, 100 + (scout ? 25 : 0), 1));}
				} else if(type % trapTypes == 1) {
					if(!par5Entity.isBurning()) {par5Entity.setFire(10 + (scout ? 2 : 0));}
				} else if(type % trapTypes == 2) {
					((EntityLivingBase) par5Entity).addPotionEffect(new PotionEffect(Potion.blindness.id, 200 + (scout ? 50 : 0), 1));
					((EntityLivingBase) par5Entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200 + (scout ? 50 : 0), 1));
				}

				if(CommonTickHandler.worldData.getDisadvantage().equals("Explosive Traps")) {par5Entity.worldObj.setBlockToAir(par2, par3, par4); par5Entity.worldObj.createExplosion(null, par2 + 0.5F, par3 + 0.5F, par4 + 0.5F, 1.33F, true);}

				par1World.setBlockToAir(par2, par3, par4);

				NewPacketHandler.SEND_MESSAGE.sendToPlayer((EntityPlayer) par5Entity, "\u00A7c\u00A7oYou triggered a " + ItemTrap.names[type % trapTypes].toLowerCase() + "!");
			} else {
				if(type % trapTypes == 0) {
					par5Entity.attackEntityFrom(DamageSource.cactus, 2.0F);
					if(ModjamMod.r.nextInt(32) == 0) {((EntityLivingBase) par5Entity).addPotionEffect(new PotionEffect(Potion.poison.id, 100, 1));}
				} else if(type % trapTypes == 1) {
					if(!par5Entity.isBurning()) {par5Entity.setFire(5);}
				} else if(type % trapTypes == 2) {
					((EntityLivingBase) par5Entity).addPotionEffect(new PotionEffect(Potion.blindness.id, 100, 1));
					((EntityLivingBase) par5Entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 100, 1));
				}
			}

		}
	}

	@Override
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5) {
		super.onNeighborBlockChange(par1World, par2, par3, par4, par5);

		if(!par1World.isRemote && !par1World.isSideSolid(par2, par3 - 1, par4, ForgeDirection.SOUTH, true)) {
			par1World.setBlockToAir(par2, par3, par4);
			TileEntity te = par1World.getTileEntity(par2, par3, par4);
			if((ModjamMod.spawnTraps || (te != null && te instanceof TileEntityTrap && ((TileEntityTrap) te).placedBy != null)) && CommonTickHandler.worldData != null && CommonTickHandler.worldData.getDisadvantage().equals("Explosive Traps")) {par1World.createExplosion(null, par2 + 0.5F, par3 + 0.5F, par4 + 0.5F, 1.33F, true);}
		}
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		return super.isReplaceable(world, x, y, z) || (((world instanceof World && ((World) world).isRemote) ? NewPacketHandler.trapsDisabled : !ModjamMod.spawnTraps) && (te == null || !(te instanceof TileEntityTrap) || ((TileEntityTrap) te).placedBy == null));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isCollidable() {return super.isCollidable();}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return super.canPlaceBlockAt(world, x, y, z) && (y == 0 || world.getBlock(x, y - 1, z) != ModjamMod.blockTrap);
	}

	@Override
	public int damageDropped(int par1) {
		return par1 % trapTypes;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for(int i = 0; i < trapTypes; i++) {par3List.add(new ItemStack(par1, 1, i));}
	}

	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
		super.onBlockPlacedBy(par1World, par2, par3, par4, par5EntityLivingBase, par6ItemStack);

		par1World.setBlockMetadataWithNotify(par2, par3, par4, par6ItemStack.getItemDamage(), 0);

		if(par5EntityLivingBase != null && par5EntityLivingBase instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) par5EntityLivingBase;

			if(!player.capabilities.isCreativeMode || NewPacketHandler.trapsDisabled) {
				TileEntity te = par1World.getTileEntity(par2, par3, par4);
				if(te != null && te instanceof TileEntityTrap) {((TileEntityTrap) te).placedBy = player.getCommandSenderName();}
			}
		}
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess blockAccess, int x, int y, int z) {return true;}

	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess blockAccess, int x, int y, int z, int fortune) {
		if(blockAccess instanceof World) {
			World world = (World) blockAccess;

			world.setBlockToAir(x, y, z);

			if(!world.isRemote) {
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

				for(int i = 0; i < server.worldServers.length; i++) {
					WorldServer s = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers[i];

					if(s == null) {continue;}

					for(Object o : s.playerEntities) {
						if(o == null || !(o instanceof EntityPlayer)) {continue;}

						EntityPlayer player = (EntityPlayer) o;

						if(item.equals(player.getHeldItem())) {
							NewPacketHandler.SEND_MESSAGE.sendToPlayer(player, "\u00A7e\u00A7oYou disarmed the trap.");
						}
					}
				}
			}

			item.attemptDamageItem(119, ModjamMod.r);
		}

		return new ArrayList<ItemStack>();
	}


	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer par1EntityPlayer, World par2World, int par3, int par4, int par5) {
		return (par1EntityPlayer.getHeldItem() != null && par1EntityPlayer.getHeldItem().getItem() == Items.shears) ? 1.0F : getBlockHardness(par2World, par3, par4, par5);
	}
}
