package au.com.mineauz.minigames.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;

public class MenuItemDisplayWhitelist extends MenuItem{
	
	private List<Material> whitelist;
	private Callback<Boolean> whitelistMode;

	public MenuItemDisplayWhitelist(String name, Material displayItem, List<Material> whitelist, Callback<Boolean> whitelistMode) {
		super(name, displayItem);
		this.whitelist = whitelist;
		this.whitelistMode = whitelistMode;
	}

	public MenuItemDisplayWhitelist(String name, List<String> description, Material displayItem, List<Material> whitelist, Callback<Boolean> whitelistMode) {
		super(name, description, displayItem);
		this.whitelist = whitelist;
		this.whitelistMode = whitelistMode;
	}
	
	@Override
	public ItemStack onClick(MinigamePlayer player) {
		Menu menu = new Menu(6, "Block Whitelist");
		List<MenuItem> items = new ArrayList<MenuItem>();
		for(Material bl : whitelist){
			items.add(new MenuItemWhitelistBlock(bl, whitelist));
		}
		menu.setControlItem(new MenuItemAddWhitelistBlock("Add Material", whitelist), 4);
		menu.setControlItem(new MenuItemBoolean("Whitelist Mode", MinigameUtils.stringToList("If whitelist mode only;added items can be;broken."), 
				Material.ENDER_PEARL, whitelistMode), 3);
		menu.addItems(items);
		menu.displayMenu(player);
		return null;
	}
}
