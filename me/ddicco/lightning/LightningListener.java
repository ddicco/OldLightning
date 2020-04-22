package me.ddicco.lightning;

import me.ddicco.lightning.Lightning;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;

public class LightningListener implements Listener {
	@EventHandler
	public void onShift(PlayerToggleSneakEvent event) {
		Player player = event.getPlayer();
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);
		
		if (event.isCancelled() || bPlayer == null) {
			return;
		} else if (bPlayer.canBend(CoreAbility.getAbility(Lightning.class)) && !CoreAbility.hasAbility(event.getPlayer(), Lightning.class)) {
			new Lightning (player);
		}
	}
}
