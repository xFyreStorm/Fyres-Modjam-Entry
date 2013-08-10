package assets.fyresmodjam;

import static net.minecraftforge.common.ForgeDirection.SOUTH;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTrap extends BlockContainer implements IShearable {
	
	public static int trapTypes = 3;
	
    protected BlockTrap(int par1) {
        super(par1, Material.circuits);
        this.setLightOpacity(0);
    }
    
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon("fyresmodjam:spikes2");
    }

    public int idDropped(int par1, Random par2Random, int par3) {
        return 0;
    }

    public int idPicked(World par1World, int par2, int par3, int par4) {
        return ModjamMod.blockTrap.blockID;
    }

    public boolean canHarvestBlock(EntityPlayer par1EntityPlayer, int par2) {
        return false;
    }
    
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9)  {	
    	if(par1World.isRemote && isCollidable()) {
    		PacketDispatcher.sendPacketToServer(PacketHandler.newPacket(PacketHandler.DISARM_TRAP, new Object[] {par2, par3, par4, par5EntityPlayer.getEntityData().hasKey("Blessing") && par5EntityPlayer.getEntityData().getString("Blessing").equals("Mechanic")}));
	    	return false;
    	}
    	
    	return true;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityTrap();
    }

    public int getMobilityFlag() {
        return 2;
    }
    
    public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity) {}
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {
        return null;
    }
    
    public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity) {
    	TileEntity te = par1World.getBlockTileEntity(par2, par3, par4);
    	
    	if(!par1World.isRemote && te != null && te instanceof TileEntityTrap && !par5Entity.getEntityName().equals(((TileEntityTrap) te).placedBy) && ModjamMod.spawnTraps && ((par5Entity instanceof EntityPlayer && !((EntityPlayer) par5Entity).capabilities.isCreativeMode) || par5Entity instanceof EntityMob)) {
    		
    		int type = par1World.getBlockMetadata(par2, par3, par4);
    		
    		if(type % trapTypes == 0) {
    			par5Entity.attackEntityFrom(DamageSource.cactus, 8.0F);
    			if(ModjamMod.r.nextInt(8) == 0) {((EntityLivingBase) par5Entity).addPotionEffect(new PotionEffect(Potion.poison.id, 100, 1));}
    		} else if(type % trapTypes == 1) {
    			if(!par5Entity.isBurning()) {par5Entity.setFire(10);}
    		} else if(type % trapTypes == 2) {
    			((EntityLivingBase) par5Entity).addPotionEffect(new PotionEffect(Potion.blindness.id, 200, 1));
    			((EntityLivingBase) par5Entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 200, 1));
    		}
    		
    		if(CommonTickHandler.worldData.currentDisadvantage.equals("Explosive Traps")) {par5Entity.worldObj.setBlockToAir(par2, par3, par4); par5Entity.worldObj.createExplosion(null, par2 + 0.5F, par3 + 0.5F, par4 + 0.5F, 1.33F, true);}
    		
    		par1World.setBlockToAir(par2, par3, par4);
    		
    		if(par5Entity instanceof EntityPlayer) {
    			PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7c\u00A7oYou triggered a " + ItemTrap.names[type % trapTypes].toLowerCase() + "!"}), (Player) par5Entity);
    		}
    		
    	}
    }
    
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
    	super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
    	
    	if(!par1World.isRemote && !par1World.isBlockSolidOnSide(par2, par3 - 1, par4, ForgeDirection.SOUTH, true)) {
    		par1World.setBlockToAir(par2, par3, par4);
    		if(ModjamMod.spawnTraps && CommonTickHandler.worldData != null && CommonTickHandler.worldData.currentDisadvantage.equals("Explosive Traps")) {par1World.createExplosion(null, par2 + 0.5F, par3 + 0.5F, par4 + 0.5F, 1.33F, true);}
    	}
    }
    
    @Override
    public boolean isBlockReplaceable(World world, int x, int y, int z) {
    	return super.isBlockReplaceable(world, x, y, z) || (world.isRemote ? PacketHandler.trapsDisabled : !ModjamMod.spawnTraps);
    }  
    
    @SideOnly(Side.CLIENT)
    public boolean isCollidable() {return !PacketHandler.trapsDisabled && getPlayerSneaking();}
    
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
    	return super.canPlaceBlockAt(world, x, y, z) && (y == 0 || world.getBlockId(x, y - 1, z) != ModjamMod.blockTrap.blockID);
    }
    
    @SideOnly(Side.CLIENT)
    public boolean getPlayerSneaking() {
    	//boolean b2 = false;
    	
    	EntityPlayer player = Minecraft.getMinecraft().thePlayer;
    	
    	/*if(player != null && Minecraft.getMinecraft().objectMouseOver != null) {
    		MovingObjectPosition mouse = Minecraft.getMinecraft().objectMouseOver;
    		
    		double xDiff = (double) mouse.blockX + 0.5D - TileEntityRenderer.instance.playerX;
			double yDiff = (double) mouse.blockY + 0.5D - TileEntityRenderer.instance.playerY;
			double zDiff = (double) mouse.blockZ + 0.5D - TileEntityRenderer.instance.playerZ;
			
			b2 = xDiff * xDiff + yDiff * yDiff + zDiff * zDiff < ((player.getEntityData().hasKey("Blessing") && player.getEntityData().getString("Blessing").equals("Scout")) ? 16.0F : 36.0F);
		}*/
    	
    	return /*b2 &&*/ (player == null ? false : (player.isSneaking() || (player.getEntityData().hasKey("Blessing") && player.getEntityData().getString("Blessing").equals("Scout"))));
    }
    
    public int damageDropped(int par1) {
        return par1 % trapTypes;
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List) {
        for(int i = 0; i < trapTypes; i++) {par3List.add(new ItemStack(par1, 1, i));}
    }
    
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
    	super.onBlockPlacedBy(par1World, par2, par3, par4, par5EntityLivingBase, par6ItemStack);
    	
    	par1World.setBlockMetadataWithNotify(par2, par3, par4, par6ItemStack.getItemDamage(), 0);
    	
    	if(par5EntityLivingBase != null && par5EntityLivingBase instanceof EntityPlayer) {
    		EntityPlayer player = (EntityPlayer) par5EntityLivingBase;
    		TileEntity te = par1World.getBlockTileEntity(par2, par3, par4);
    		if(te != null && te instanceof TileEntityTrap) {((TileEntityTrap) te).placedBy = player.getEntityName();}
    	}
    }
    
    @Override
    public boolean isShearable(ItemStack item, World world, int x, int y, int z) {return true;}

    @Override
    public ArrayList<ItemStack> onSheared(ItemStack item, World world, int x, int y, int z, int fortune) {
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
    					PacketDispatcher.sendPacketToPlayer(PacketHandler.newPacket(PacketHandler.SEND_MESSAGE, new Object[] {"\u00A7e\u00A7oYou disarmed the trap."}), (Player) player);
    				}
    			}
        	}
        }
        
        item.attemptDamageItem(119, ModjamMod.r);
        
        return new ArrayList<ItemStack>();
    }
    
    
    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer par1EntityPlayer, World par2World, int par3, int par4, int par5) {
        return (par1EntityPlayer.getHeldItem() != null && par1EntityPlayer.getHeldItem().getItem().itemID == Item.shears.itemID) ? 1.0F : this.getBlockHardness(par2World, par3, par4, par5);
    }
}
