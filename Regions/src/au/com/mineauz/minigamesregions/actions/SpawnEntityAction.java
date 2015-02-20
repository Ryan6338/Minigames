package au.com.mineauz.minigamesregions.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.MinigameUtils;
import au.com.mineauz.minigames.Minigames;
import au.com.mineauz.minigames.config.StringFlag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItem.IMenuItemClick;
import au.com.mineauz.minigames.menu.MenuItemBoolean;
import au.com.mineauz.minigames.menu.MenuItemDecimal;
import au.com.mineauz.minigames.menu.MenuItemList;
import au.com.mineauz.minigames.menu.MenuItemNewLine;
import au.com.mineauz.minigames.menu.MenuItemString;
import au.com.mineauz.minigamesregions.Main;
import au.com.mineauz.minigamesregions.Node;
import au.com.mineauz.minigamesregions.Region;

public class SpawnEntityAction extends ActionInterface {
	
	private StringFlag type = new StringFlag("ZOMBIE", "type");
	private Map<String, String> settings = new HashMap<String, String>();
	
	public SpawnEntityAction(){
		addBaseSettings();
	}
	
	private void addBaseSettings(){
		settings.put("velocityx", "0");
		settings.put("velocityy", "0");
		settings.put("velocityz", "0");
	}

	@Override
	public String getName() {
		return "SPAWN_ENTITY";
	}

	@Override
	public String getCategory() {
		return "World Actions";
	}

	@Override
	public boolean useInRegions() {
		return false;
	}

	@Override
	public boolean useInNodes() {
		return true;
	}

	@Override
	public void executeRegionAction(MinigamePlayer player,
			Region region) {
	}

	@Override
	public void executeNodeAction(MinigamePlayer player, Node node) {
		if(player == null || !player.isInMinigame()) return;
		final Entity ent = node.getLocation().getWorld().spawnEntity(node.getLocation(), EntityType.valueOf(type.getFlag()));
		
		final double vx = Double.valueOf(settings.get("velocityx"));
		final double vy = Double.valueOf(settings.get("velocityy"));
		final double vz = Double.valueOf(settings.get("velocityz"));
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
			
			@Override
			public void run() {
				ent.setVelocity(new Vector(vx, vy, vz));
			}
		});
		
		if(ent instanceof LivingEntity){
			LivingEntity lent = (LivingEntity) ent;
			if(settings.containsKey("displayname")){
				lent.setCustomName(settings.get("displayname"));
				lent.setCustomNameVisible(Boolean.getBoolean(settings.get("displaynamevisible")));
			}
		}
		
		ent.setMetadata("MinigameEntity", new FixedMetadataValue(Minigames.plugin, true));
		player.getMinigame().getBlockRecorder().addEntity(ent, player, true);
	}

	@Override
	public void saveArguments(FileConfiguration config,
			String path) {
		type.saveValue(path, config);
		
	}

	@Override
	public void loadArguments(FileConfiguration config,
			String path) {
		type.loadValue(path, config);
		
	}

	@Override
	public boolean displayMenu(MinigamePlayer player, Menu previous) {
		Menu m = new Menu(3, "Spawn Entity");
		List<String> options = new ArrayList<String>();
		for(EntityType type : EntityType.values()){
			if(type != EntityType.ITEM_FRAME && type != EntityType.LEASH_HITCH && type != EntityType.PLAYER && 
					type != EntityType.COMPLEX_PART && type != EntityType.WEATHER && type != EntityType.LIGHTNING &&
					type != EntityType.PAINTING && type != EntityType.UNKNOWN &&
					type != EntityType.DROPPED_ITEM)
				options.add(MinigameUtils.capitalize(type.toString().replace("_", " ")));
		}
		m.addItem(new MenuItemList("Entity Type", Material.SKULL_ITEM, new Callback<String>() {
			
			@Override
			public void setValue(String value) {
				type.setFlag(value.toUpperCase().replace(" ", "_"));
				settings.clear();
				addBaseSettings();
			}
			
			@Override
			public String getValue() {
				return MinigameUtils.capitalize(type.getFlag().replace("_", " "));
			}
		}, options));
		
		m.addItem(new MenuItemDecimal("X Velocity", Material.ARROW, new Callback<Double>() {

			@Override
			public void setValue(Double value) {
				settings.put("velocityx", value.toString());
			}

			@Override
			public Double getValue() {
				return Double.valueOf(settings.get("velocityx"));
			}
		}, 0.5, 1, Double.MIN_VALUE, Double.MAX_VALUE));
		m.addItem(new MenuItemDecimal("Y Velocity", Material.ARROW, new Callback<Double>() {

			@Override
			public void setValue(Double value) {
				settings.put("velocityy", value.toString());
			}

			@Override
			public Double getValue() {
				return Double.valueOf(settings.get("velocityy"));
			}
		}, 0.5, 1, Double.MIN_VALUE, Double.MAX_VALUE));
		m.addItem(new MenuItemDecimal("Z Velocity", Material.ARROW, new Callback<Double>() {

			@Override
			public void setValue(Double value) {
				settings.put("velocityz", value.toString());
			}

			@Override
			public Double getValue() {
				return Double.valueOf(settings.get("velocityz"));
			}
		}, 0.5, 1, Double.MIN_VALUE, Double.MAX_VALUE));
		
		m.addItem(new MenuItemNewLine());
		
		final MenuItem cus = new MenuItem("Entity Settings", Material.CHEST);
		cus.setClickHandler(new IMenuItemClick() {
			@Override
			public void onClick(MenuItem menuItem, MinigamePlayer player) {
				if(type.getFlag().equals("ZOMBIE")){
					Menu eSet = new Menu(3, "Settings");
					eSet.clear();
					livingEntitySettings(eSet);
					eSet.displayMenu(player);
				}
			}
		});
		m.addItem(cus);
		
		m.displayMenu(player);
		return true;
	}
	
	private void livingEntitySettings(Menu eSet){
		settings.put("displayname", "");
		settings.put("displaynamevisible", "false");
		
		eSet.addItem(new MenuItemString("Display Name", Material.NAME_TAG, new Callback<String>() {

			@Override
			public void setValue(String value) {
				settings.put("displayname", value);
			}

			@Override
			public String getValue() {
				return settings.get("displayname");
			}
		}));
		eSet.addItem(new MenuItemBoolean("Display Name Visible", Material.ENDER_PEARL, new Callback<Boolean>() {

			@Override
			public void setValue(Boolean value) {
				settings.put("displaynamevisible", value.toString());
			}

			@Override
			public Boolean getValue() {
				return Boolean.valueOf(settings.get("displaynamevisible"));
			}
		}));
	}
	
	public void zombieSettings(Menu eSet){
		
	}
}
