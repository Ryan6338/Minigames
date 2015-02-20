package au.com.mineauz.minigames.signs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.block.SignChangeEvent;
import au.com.mineauz.minigames.MinigameData;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItem.IMenuItemClick;
import au.com.mineauz.minigames.menu.MenuItemRewardAdd;
import au.com.mineauz.minigames.menu.MenuItemRewardGroup;
import au.com.mineauz.minigames.menu.MenuItemRewardGroupAdd;
import au.com.mineauz.minigames.minigame.reward.RewardGroup;
import au.com.mineauz.minigames.minigame.reward.RewardRarity;
import au.com.mineauz.minigames.minigame.reward.RewardType;
import au.com.mineauz.minigames.minigame.reward.Rewards;

public class RewardSign implements MinigameSign {
	
	private static Minigames plugin = Minigames.plugin;
	private MinigameData mdata = plugin.mdata;

	@Override
	public String getName() {
		return "Reward";
	}

	@Override
	public String getCreatePermission() {
		return "minigame.sign.create.reward";
	}

	@Override
	public String getCreatePermissionMessage() {
		return MinigameUtils.getLang("sign.reward.createPermission");
	}

	@Override
	public String getUsePermission() {
		return "minigame.sign.use.reward";
	}

	@Override
	public String getUsePermissionMessage() {
		return MinigameUtils.getLang("sign.reward.usePermission");
	}

	@Override
	public boolean signCreate(SignChangeEvent event) {
		if(!event.getLine(2).equals("")){
			event.setLine(1, ChatColor.GREEN + getName());
			return true;
		}
		plugin.pdata.getMinigamePlayer(event.getPlayer()).sendMessage(MinigameUtils.getLang("sign.reward.noName"), "error");
		return false;
	}
	
	@Override
	public boolean signUse(Sign sign, MinigamePlayer player) {
		Location loc = sign.getLocation();
		if(!MinigameUtils.isMinigameTool(player.getPlayer().getItemInHand())){
			String label = sign.getLine(2).toLowerCase();
			if(player.isInMinigame()){
				if(!player.hasTempClaimedReward(label)){
					if(mdata.hasRewardSign(loc)){
						Rewards rew = mdata.getRewardSign(loc);
						for(RewardType r : rew.getReward()){
							r.giveReward(player);
						}
					}
					player.addTempClaimedReward(label);
				}
			}
			else{
				if(!player.hasClaimedReward(label)){
					if(mdata.hasRewardSign(loc)){
						Rewards rew = mdata.getRewardSign(loc);
						for(RewardType r : rew.getReward()){
							r.giveReward(player);
						}
						
						player.updateInventory();
					}
					player.addClaimedReward(label);
				}
			}
		}
		else if(player.getPlayer().hasPermission("minigame.tool")){
			Rewards rew = null;
			if(!mdata.hasRewardSign(loc)){
				mdata.addRewardSign(loc);
			}
			rew = mdata.getRewardSign(loc);
			
			Menu rewardMenu = new Menu(5, getName());
			
			rewardMenu.setControlItem(new MenuItemRewardGroupAdd("Add Group", Material.ITEM_FRAME, rew), 3);
			rewardMenu.setControlItem(new MenuItemRewardAdd("Add Item", Material.ITEM_FRAME, rew), 2);
			final MenuItem mic = new MenuItem("Save Rewards", Material.REDSTONE_TORCH_ON);
			final Location floc = loc;
			mic.setClickHandler(new IMenuItemClick() {
				@Override
				public void onClick(MenuItem menuItem, MinigamePlayer player) {
					mdata.saveRewardSign(MinigameUtils.createLocationID(floc), true);
					player.sendMessage("Saved rewards for this sign.", null);
					player.getPlayer().closeInventory();
				}
			});
			rewardMenu.setControlItem(mic, 4);
			List<String> list = new ArrayList<String>();
			for(RewardRarity r : RewardRarity.values()){
				list.add(r.toString());
			}
			
			List<MenuItem> mi = new ArrayList<MenuItem>();
			for(RewardType item : rew.getRewards()){
				mi.add(item.getMenuItem());
			}
			for(RewardGroup group : rew.getGroups()){
				MenuItemRewardGroup rwg = new MenuItemRewardGroup(group.getName() + " Group", Material.CHEST, group, rew);
				mi.add(rwg);
			}
			rewardMenu.addItems(mi);
			rewardMenu.displayMenu(player);
		}
		return true;
	}

	@Override
	public void signBreak(Sign sign, MinigamePlayer player) {
		if(plugin.mdata.hasRewardSign(sign.getLocation())){
			plugin.mdata.removeRewardSign(sign.getLocation());
		}
	}

}
