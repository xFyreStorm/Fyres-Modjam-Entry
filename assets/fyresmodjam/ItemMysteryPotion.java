package assets.fyresmodjam;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;

public class ItemMysteryPotion extends Item {

	public static Icon[] icons = null;
	
	public ItemMysteryPotion(int par1) {
		super(par1);
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
		if(icons == null) {
			icons = new Icon[12];
			for(int i = 0; i < 12; i++) {icons[i] = par1IconRegister.registerIcon("fyresmodjam:mysteryPotion_" + (i + 1));}
		}
		
        this.itemIcon = icons[0];
    }

}
