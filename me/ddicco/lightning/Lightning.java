package me.ddicco.lightning;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;

public class Lightning extends FireAbility implements AddonAbility {
	
	private long chargetime;
	private static double blockdistance = 4;
	private double maxdamage = MAX_DAMAGE;
	private double strikeradius = 4;
	private Player player;
	private long starttime;
	private boolean charged = false;
	private LightningStrike strike = null;
	public static ConcurrentHashMap<Player, Lightning> instances = new ConcurrentHashMap<Player, Lightning>();
	private static ConcurrentHashMap<Entity, Lightning> strikes = new ConcurrentHashMap<Entity, Lightning>();
	private ArrayList<Entity> hitentities = new ArrayList<Entity>();
	
	public Lightning(Player player) {
		super(player);
		// TODO Auto-generated constructor stub
		
		if (instances.containsKey(player)) {
			return;
		}
		this.player = player;
		starttime = System.currentTimeMillis();
		start();
	}
	public static Lightning getLightning(Entity entity) {
		if (strikes.containsKey(entity))
			return strikes.get(entity);
		return null;
	}
	private void strike() {
		Location targetlocation = getTargetLocation();
		if (!GeneralMethods.isRegionProtectedFromBuild(player, "Lightning",
				targetlocation)) {
			strike = player.getWorld().strikeLightning(targetlocation);
			strikes.put(strike, this);
			bPlayer.addCooldown("Lightning", cooldown);
		}
		remove();
	}
	public static int defaultdistance = com.projectkorra.projectkorra.ProjectKorra.plugin.getConfig().getInt("ExtraAbilities.Fire.Lightning.DefaultDistance");
	private static long defaultchargetime = com.projectkorra.projectkorra.ProjectKorra.plugin.getConfig().getLong("ExtraAbilities.Fire.Lightning.ChargeTime");
	private static double MAX_DAMAGE = com.projectkorra.projectkorra.ProjectKorra.plugin.getConfig().getLong("ExtraAbilities.Fire.Lightning.Damage");
	private static long cooldown = com.projectkorra.projectkorra.ProjectKorra.plugin.getConfig().getLong("ExtraAbilities.Fire.Lightning.Cooldown");
	
	private Location getTargetLocation() {
		int distance = (int) defaultdistance;
		Location targetlocation;
		targetlocation = GeneralMethods.getTargetedLocation(player, distance);
		Entity target = GeneralMethods.getTargetedEntity(player, distance, new ArrayList<Entity>());
		if (target != null) {
			if (target instanceof LivingEntity
					&& player.getLocation().distance(targetlocation) > target
					.getLocation().distance(player.getLocation())) {
				targetlocation = target.getLocation();
			}
		}
		if (targetlocation.getBlock().getType() == Material.AIR)
			targetlocation.add(0, -1, 0);
		if (targetlocation.getBlock().getType() == Material.AIR)
			targetlocation.add(0, -1, 0);
		return targetlocation;
	}

	public void dealDamage(Entity entity) {
		if (strike == null) {
			return;
		}
		if (hitentities.contains(entity)) {
			return;
		}
		double distance = entity.getLocation().distance(strike.getLocation());
		double damage = maxdamage - (distance / strikeradius) * .5;
		if ((getTargetLocation().getBlock().getType() == Material.WATER) || (getTargetLocation().getBlock().getType() == Material.STATIONARY_WATER)) {
		} else {
			if (distance > strikeradius) {
				return;
			}
			if(entity.getUniqueId() != player.getUniqueId()) {
			DamageHandler.damageEntity(entity, (int) damage, this);
			}
		}
		hitentities.add(entity);
	}
	public static boolean isNearbyChannel(Location location) {
		boolean value = false;
		for (Player player : instances.keySet()) {
			if (!player.getWorld().equals(location.getWorld()))
				continue;
			if (player.getLocation().distance(location) <= blockdistance) {
				value = true;
				instances.get(player).starttime = 0;
			}
		}
		return value;
	}
	public String getDescription() {
		return "Hold sneak while selecting this ability to charge up a lightning strike. Once "
				+ "charged, release sneak to discharge the lightning to the targetted location.";
	}
	@Override
	public long getCooldown() {
		// TODO Auto-generated method stub
		return 0;
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
		if (player.isDead() || !player.isOnline()) {
			remove();
			return;
		}
		if (bPlayer.getBoundAbility() == null || !bPlayer.getBoundAbilityName().equalsIgnoreCase("Lightning")) {
			remove();
			return;
		}
		chargetime = (int) ((double) defaultchargetime);
		if (System.currentTimeMillis() > starttime + chargetime)
			charged = true;

		if (charged) {
			if (player.isSneaking()) {
				final Location tempLoc = this.player.getEyeLocation().add(this.player.getEyeLocation().getDirection().normalize().multiply(1.2));
				ParticleEffect.SMOKE.display(tempLoc, 0.3F, 0.1F, 0.3F, 0, 4);
			} else {
				strike();
			}
		} else {
			if (!player.isSneaking()) {
				remove();
			}
		}
	}
	@Override
	public String getAuthor() {
		// TODO Auto-generated method stub
		return "ddicco";
	}
	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return "1.1";
	}
	@Override
	public void load() {
		// TODO Auto-generated method stub
		ProjectKorra.plugin.getServer().getPluginManager().registerEvents(new LightningListener(), ProjectKorra.plugin);
		ProjectKorra.log.info("Succesfully enabled " + getName() + " by " + getAuthor());
		
		ConfigManager.getConfig().addDefault("ExtraAbilities.Fire.Lightning.DefaultDistance", 20);
        ConfigManager.getConfig().addDefault("ExtraAbilities.Fire.Lightning.ChargeTime", 2500);
        ConfigManager.getConfig().addDefault("ExtraAbilities.Fire.Lightning.MissChance", 5);
        ConfigManager.getConfig().addDefault("ExtraAbilities.Fire.Lightning.Damage", 5);
        ConfigManager.getConfig().addDefault("ExtraAbilities.Fire.Lightning.Cooldown", 3000);
        ConfigManager.defaultConfig.save();
	}
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		ProjectKorra.log.info("Successfully disabled " + getName() + " by " + getAuthor());
		super.remove();
	}

}
