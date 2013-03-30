package com.pauldavdesign.mineauz.minigames.scoring;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
//import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
//import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.pauldavdesign.mineauz.minigames.Minigame;

public class PlayerKillsType extends ScoreType{

	@Override
	public String getType() {
		return "kills";
	}
	
	@EventHandler
	private void playerAttackPlayer(PlayerDeathEvent event){
		if(event.getEntity() instanceof Player){
			Player ply = (Player) event.getEntity();
			Minigame mgm = null;
			if(pdata.getPlayersMinigame(ply) != null){
				mgm = pdata.getPlayersMinigame(ply);
			}
			if(mgm != null){
				if(mgm.getBlueTeam().isEmpty() && mgm.getRedTeam().isEmpty()){
					Player attacker = null;
					if(ply.getKiller() != null){
						attacker = (Player) ply.getKiller();
						if(attacker == ply){
							return;
						}
					}
					else{
						return;
					}
					
					if(!mgm.getName().equals(pdata.getPlayersMinigame(attacker))){
						return;
					}

					pdata.addPlayerKill(attacker);
					pdata.addPlayerScore(attacker);
					
					if(mgm.getScoreType().equals("kills")){
						mdata.sendMinigameMessage(mgm, ply.getKiller().getName() + "'s Score: " + pdata.getPlayerScore(ply.getKiller()), null, null);
					
						if(mgm.getMaxScore() != 0 && pdata.getPlayerScore(attacker) >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
							List<Player> conPlayers = new ArrayList<Player>();
							conPlayers.addAll(mgm.getPlayers());
							conPlayers.remove(attacker);
							for(Player pl : conPlayers){
								if(pl != attacker){
									pdata.quitMinigame(pl, false);
								}
							}
						}
					}
				}
				else{
					Player attacker = null;
					if(ply.getKiller() instanceof Player){
						attacker = (Player) ply.getKiller();
						if(attacker == ply){
							return;
						}
					}
					else{
						return;
					}
					
					if(!mgm.getName().equals(pdata.getPlayersMinigame(attacker))){
						return;
					}
					
					int team = 0;
					int ateam = 0;
					if(mgm.getBlueTeam().contains(ply)){
						team = 1;
					}
					
					if(mgm.getBlueTeam().contains(attacker)){
						ateam = 1;
					}
					
					if(team != ateam){
						pdata.addPlayerKill(attacker);
						pdata.addPlayerScore(attacker);
						
						if(mgm.getScoreType().equals("kills")){
							boolean end = false;
							
							if(ateam == 0){
								mgm.incrementRedTeamScore();
								
								if(mgm.getMaxScore() != 0 && mgm.getRedTeamScore() >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
									end = true;
								}
							}
							else{
								mgm.incrementBlueTeamScore();
								
								if(mgm.getMaxScore() != 0 && mgm.getBlueTeamScore() >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
									end = true;
								}
							}
							
							mdata.sendMinigameMessage(mgm, "Score: " + ChatColor.RED + mgm.getRedTeamScore() + ChatColor.WHITE + " to " + ChatColor.BLUE + mgm.getBlueTeamScore(), null, null);
							
							if(end){
								mdata.sendMinigameMessage(mgm, attacker.getName() + " took the final kill against " + ply.getName(), null, null);
								if(ateam == 1){
									if(mgm.getMaxScore() != 0 && mgm.getBlueTeamScore() >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
										pdata.endTeamMinigame(1, mgm);
									}
								}
								else{
									if(mgm.getMaxScore() != 0 && mgm.getRedTeamScore() >= mgm.getMaxScorePerPlayer(mgm.getPlayers().size())){
										pdata.endTeamMinigame(0, mgm);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	private void playerSuicide(PlayerDeathEvent event){
		Player ply = event.getEntity();
		if(pdata.playerInMinigame(ply) && (ply.getKiller() == null || ply.getKiller() == ply)){
			Minigame mgm = pdata.getPlayersMinigame(ply);
			if(mgm.getScoreType().equals("kills")){
				if(mgm.getRedTeam().isEmpty() && mgm.getBlueTeam().isEmpty()){
					pdata.takePlayerScore(ply);
					
					mdata.sendMinigameMessage(mgm, ply.getName() + "'s Score: " + pdata.getPlayerScore(ply), null, null);
				}
				else{
					pdata.takePlayerScore(ply);
					if(mgm.getRedTeam().contains(ply)){
						mgm.setRedTeamScore(mgm.getRedTeamScore() - 1);
					}
					else{
						mgm.setBlueTeamScore(mgm.getBlueTeamScore() - 1);
					}
					mdata.sendMinigameMessage(mgm, "Score: " + ChatColor.RED + mgm.getRedTeamScore() + ChatColor.WHITE + " to " + ChatColor.BLUE + mgm.getBlueTeamScore(), null, null);
				}
			}
		}
	}
}
