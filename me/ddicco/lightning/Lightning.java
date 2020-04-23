package me.ddicco.lightning;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.util.ParticleEffect;

public class Lightning extends FireAbility implements AddonAbility{

	private boolean charged;
	private long starttime;
	private long chargetime;
	private Location targetlocation;
	private double range;

	public Lightning(Player player) {
		super(player);
		// TODO Auto-generated constructor stub
		setFields();
		start();
	}

	private void setFields() {
		// TODO Auto-generated method stub
		starttime = System.currentTimeMillis();
		range = 20;
		chargetime = 3000;
		charged = false;
	}

	@Override
	public long getCooldown() {
		// TODO Auto-generated method stub
		return 10000;
	}

	@Override
	public Location getLocation() {
		// TODO Auto-generated method stub
		return player.getLocation();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Lightning";
	}

	@Override
	public boolean isHarmlessAbility() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void progress() {
		// TODO Auto-generated method stub
		if(charged == false && !player.isSneaking()) {
			remove();
			return;
		}
		
		if(player.isDead() || !player.isOnline()) {
			remove();
			return;
		}
		
		if(!bPlayer.canBend(this)) {
			remove();
			return;
		}
		
		if(chargetime + starttime < System.currentTimeMillis()) {
			charged = true;
			if(player.isSneaking()) {
				final Location tempLoc = this.player.getEyeLocation().add(this.player.getEyeLocation().getDirection().normalize().multiply(1.2));
				ParticleEffect.SMOKE.display(tempLoc, 0.3F, 0.1F, 0.3F, 0, 4);
			}
			
		}
		if(charged == true && !player.isSneaking()) {
			getSource(player, range);
			strikeLightning();
		}
	}
	
	public Location getSource(Player player, double range) {
	    Vector direction = player.getLocation().getDirection().clone().multiply(0.01);
	    Location loc = player.getEyeLocation().clone();
	    Location startLoc = loc.clone();
	    
	    do {
	        loc.add(direction);
	    } while (startLoc.distance(loc) < range && !GeneralMethods.isSolid(loc.getBlock()));
	        
	    return targetlocation;
	}

	private void strikeLightning() {
		// TODO Auto-generated method stub
			player.getWorld().strikeLightningEffect(targetlocation);
	}

	@Override
	public String getAuthor() {
		// TODO Auto-generated method stub
		return "ddicco";
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return "0.1";
	}

	@Override
	public void load() {
		// TODO Auto-generated method stub
		ProjectKorra.log.info("Successfully enabled " + getName() + " by " + getAuthor() + " Version " + getVersion());
		ProjectKorra.plugin.getServer().getPluginManager().registerEvents(new LightningListener(), ProjectKorra.plugin);
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

}

