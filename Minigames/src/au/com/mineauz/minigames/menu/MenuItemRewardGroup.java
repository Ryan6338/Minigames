package au.com.mineauz.minigames.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardRarity;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.Rewards;

public class MenuItemRewardGroup extends MenuItem{
	
	private RewardGroup group;
	private Rewards rewards;

	public MenuItemRewardGroup(String name, Material displayItem, RewardGroup group, Rewards rewards) {
		super(name, displayItem);
		this.group = group;
		this.rewards = rewards;
		updateDescription();
	}

	public MenuItemRewardGroup(String name, List<String> description, Material displayItem, RewardGroup group, Rewards rewards) {
		super(name, description, displayItem);
		this.group = group;
		this.rewards = rewards;
		updateDescription();
	}
	
	public List<String> getOptions(){
		List<String> options = new ArrayList<String>();
		for(RewardRarity r : RewardRarity.values()){
			options.add(r.toString());
		}
		return options;
	}
	
	public void updateDescription(){
		List<String> description = null;
		List<String> options = getOptions();
		
		int pos = options.indexOf(group.getRarity().toString());
		int before = pos - 1;
		int after = pos + 1;
		if(before == -1)
			before = options.size() - 1;
		if(after == options.size())
			after = 0;
		
		if(getDescription() != null){
			description = getDescription();
			if(getDescription().size() >= 3){
				String desc = ChatColor.stripColor(getDescription().get(1));
				
				if(options.contains(desc)){
					description.set(0, ChatColor.GRAY.toString() + options.get(before));
					description.set(1, ChatColor.GREEN.toString() + group.getRarity().toString());
					description.set(2, ChatColor.GRAY.toString() + options.get(after));
				}
				else{
					description.add(0, ChatColor.GRAY.toString() + options.get(before));
					description.add(1, ChatColor.GREEN.toString() + group.getRarity().toString());
					description.add(2, ChatColor.GRAY.toString() + options.get(after));
				}
			}
			else{
				description.add(0, ChatColor.GRAY.toString() + options.get(before));
				description.add(1, ChatColor.GREEN.toString() + group.getRarity().toString());
				description.add(2, ChatColor.GRAY.toString() + options.get(after));
			}
		}
		else{
			description = new ArrayList<String>();
			description.add(ChatColor.GRAY.toString() + options.get(before));
			description.add(ChatColor.GREEN.toString() + group.getRarity().toString());
			description.add(ChatColor.GRAY.toString() + options.get(after));
		}
		
		setDescription(description);
	}

	
	@Override
	public ItemStack onClick(MinigamePlayer player){
		List<String> options = getOptions();
		int ind = options.lastIndexOf(group.getRarity().toString());
		ind++;
		if(ind == options.size())
			ind = 0;
		
		group.setRarity(RewardRarity.valueOf(options.get(ind)));
		updateDescription();
		
		return getItem();
	}
	
	@Override
	public ItemStack onRightClick(MinigamePlayer player){
		List<String> options = getOptions();
		int ind = options.lastIndexOf(group.getRarity().toString());
		ind--;
		if(ind == -1)
			ind = options.size() - 1;
		
		group.setRarity(RewardRarity.valueOf(options.get(ind)));
		updateDescription();
		
		return getItem();
	}
	
	@Override
	public void checkValidEntry(MinigamePlayer player, String entry){
		if(entry.equalsIgnoreCase("yes")){
			rewards.removeGroup(group);
			remove();
			return;
		}
		player.sendMessage("The selected group will not be removed from the rewards.", "error");
	}
	
	@Override
	public ItemStack onShiftRightClick(MinigamePlayer player){
		beginManualEntry(player, "Delete the reward group \"" + group.getName() + "\"? Type \"Yes\" to confirm.", 10);
		player.sendMessage("The menu will automatically reopen in 10s if nothing is entered.");
		return null;
	}
	
	@Override
	public ItemStack onDoubleClick(MinigamePlayer player){
		Menu rewardMenu = new Menu(5, getName());

		List<String> des = new ArrayList<String>();
		des.add("Click this with an item");
		des.add("to add it to rewards.");
		des.add("Click without an item");
		des.add("to add a money reward.");
		
		rewardMenu.setControlItem(new MenuItemRewardAdd("Add Item", des, Material.ITEM_FRAME, group), 4);
		List<String> list = new ArrayList<String>();
		for(RewardRarity r : RewardRarity.values()){
			list.add(r.toString());
		}
		
		List<MenuItem> mi = new ArrayList<MenuItem>();
		for(RewardType item : group.getItems()){
			mi.add(item.getMenuItem());
		}
		
		rewardMenu.addItems(mi);
		rewardMenu.displayMenu(player);
		return null;
	}
}
