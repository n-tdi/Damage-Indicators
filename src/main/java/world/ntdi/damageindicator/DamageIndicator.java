package world.ntdi.damageindicator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class DamageIndicator extends JavaPlugin implements Listener {
    public static FileConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        config = getConfig();

        // Config stuff
        config.addDefault("critical-color", "&c");
        config.addDefault("regular-color", "&e");
        config.addDefault("timer", 3);
        config.addDefault("player-only", false);

        config.options().copyDefaults(true);
        saveConfig();

        // Register events
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void entityDamageEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof ArmorStand) return;
        Location loc = e.getEntity().getLocation().subtract(0, -0.5, 0);
        int dmg = (int) Math.round(e.getFinalDamage());
        if (config.getBoolean("player-only") && e.getDamager() instanceof Player) {
            createHologram(loc, dmg, e.getDamager().isOnGround());
        } else if (!config.getBoolean("player-only")) {
            createHologram(loc, dmg, e.getDamager().isOnGround());
        }
    }

    private void createHologram(Location loc, int dmg, boolean isCritical) {
        ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        as.setVisible(false);
        as.setGravity(false);
        as.setCustomName(ChatColor.translateAlternateColorCodes('&', isCritical
                ? config.getString("critical-color") + dmg
                : config.getString("regular-color") + dmg
        ));
        as.setCustomNameVisible(true);
        as.setInvulnerable(true);
        as.setSilent(true);
        as.setSmall(true);
        as.setCollidable(false);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, as::remove, config.getInt("timer") * 20L);
    }
}
