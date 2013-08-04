package assets.fyresmodjam;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class ItemTrap extends ItemBlock {

	public static Icon[] icons;
	public static String[] iconLocations = new String[] {"fyresmodjam:spikes", "fyresmodjam:trap2"};

	public ItemTrap(int par1) {
		super(par1);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
		if(icons == null) {
    		icons = new Icon[BlockTrap.trapTypes];
    		for(int i = 0; i < iconLocations.length; i++) {icons[i] = par1IconRegister.registerIcon(iconLocations[i]);}
    	}
    	
        this.itemIcon = icons[0];
    }
	
	@SideOnly(Side.CLIENT)
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List) {
        for (int i = 0; i < BlockTrap.trapTypes; i++) {par3List.add(new ItemStack(par1, 1, i));}
    }
	
	public int getBlockID() {
        return ModjamMod.blockTrap.blockID;
    }

}
