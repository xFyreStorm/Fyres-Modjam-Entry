package fyresmodjam.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import java.util.List;

public class ItemScroll extends Item
{
    public static String[][] scrollText = new String[][] {{"Unkown Adventurer", "Codex #1: Adventurer's Log", "This morning I fell out of the sky. I am unsure of where I am, or for what purpose, but I've begun construction of a small shelter for the time being.", "I've now spent days scouting the nearby area with no luck. Still alone, I'm made my decision to set out further.", "I've discovered an ancient stronghold. Writtings of end times cover the walls. What could it mean? Who built this place? Why was it here? And I have an unsettling feeling that I'm being watched..."}, {"Village Elder", "Codex #2: An Elders Warning", "For thousands of years, we have lived peacefully. As stories of the distant past began to fade in time, the elders became tasked with remembering. I now join their ranks, but still I see there are those who choose to forget.", "The lines between worlds begin to blur once again. In the deepest caverns, we can see in to their world, and theirs in to ours. The end returns, yet no other will listen. It's clear I must find the crystal alone."}, {"Pigman Warrior", "Codex #3: The Warriors Spirit", "We fight through the fires of this realm, thinking as one. It is the only way to survive this wretched landscape. Hurt one and the horde will know.", "Strangers enter our realm through even stranger portals. They seek the crystal which bonds our world. We refuse, we fight, but many die. Our numbers begin dwindle, but the crystal will stay ours.", "We can see the void seeping through the cracks of our realm, reanimating the dead. Empty husks of our fallen push back the intruders. But they too are affected by the void's forces."}, {"Enderman Urchin", "Codex #4: From the Shadows", "The darkness shall consume all, below each of the worlds it boils. We now wait for our time to return, watching through the cracks of space and time. Us children of the Ender will reclaim what was once ours."}/*,
            {"Author", "Codex #5", "Words"}*/};

    // TODO get text from text file to allow for user editting

    public Icon texture;

    public ItemScroll(int par1)
    {
        super(par1);
        this.hasSubtypes = true;
        this.maxStackSize = 1;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister)
    {
        texture = iconRegister.registerIcon("fyresmodjam:scroll");
        this.itemIcon = texture;
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int par1, boolean b)
    {
        if (!world.isRemote)
        {
            if (!stack.hasTagCompound()) {stack.stackTagCompound = new NBTTagCompound();}

            if (!stack.getTagCompound().hasKey("initialized") || !stack.getTagCompound().getBoolean("initialized"))
            {
                stack.getTagCompound().setBoolean("initialized", true);

                ItemStack book = new ItemStack(Item.writtenBook, 1, 0);

                NBTTagList pages = new NBTTagList("pages");

                for (int i = 2; i < scrollText[stack.getItemDamage() % scrollText.length].length; i++)
                {
                    pages.appendTag(new NBTTagString("" + (i - 1), scrollText[stack.getItemDamage() % scrollText.length][i]));
                }

                book.setTagInfo("pages", pages);
                book.setTagInfo("author", new NBTTagString("author", scrollText[stack.getItemDamage() % scrollText.length][0]));
                book.setTagInfo("title", new NBTTagString("title", scrollText[stack.getItemDamage() % scrollText.length][1]));

                NBTTagCompound bookTag = new NBTTagCompound();
                book.writeToNBT(bookTag);
                stack.setTagInfo("book", bookTag);
            }
        }
    }

    public void getSubItems(int id, CreativeTabs creativeTab, List list)
    {
        for (int i = 0; i < scrollText.length; i++) {list.add(new ItemStack(id, 1, i));}
    }

    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
        NBTTagCompound bookTag = par1ItemStack.getTagCompound().getCompoundTag("book");
        if (bookTag != null) {par3EntityPlayer.displayGUIBook(ItemStack.loadItemStackFromNBT(bookTag));}
        return par1ItemStack;
    }

    public String getItemDisplayName(ItemStack par1ItemStack)
    {
        return scrollText[par1ItemStack.getItemDamage() % scrollText.length][1];
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4)
    {
        par3List.add(EnumChatFormatting.GRAY + scrollText[par1ItemStack.getItemDamage() % scrollText.length][0]);
    }
}
