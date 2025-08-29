package com.example.glow;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class GlowPlugin extends JavaPlugin implements Listener {

    private final Set<UUID> glowingPlayers = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig(); // Create config.yml if not present
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("glow").setExecutor(new GlowCommand(this));

        // Load glowing players from config
        for (String uuidStr : getConfig().getStringList("glowingPlayers")) {
            try {
                UUID uuid = UUID.fromString(uuidStr);
                glowingPlayers.add(uuid);

                // If player is online at startup, apply glow
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.setGlowing(true);
                }
            } catch (IllegalArgumentException e) {
                getLogger().warning("Invalid UUID in config: " + uuidStr);
            }
        }

        getLogger().info("GlowPlugin enabled! Loaded " + glowingPlayers.size() + " glowing players.");
    }

    @Override
    public void onDisable() {
        // Save glowing players into config
        getConfig().set("glowingPlayers", glowingPlayers.stream()
                .map(UUID::toString)
                .collect(Collectors.toList()));
        saveConfig();

        getLogger().info("GlowPlugin disabled! Saved " + glowingPlayers.size() + " glowing players.");
    }

    public boolean isGlowing(Player player) {
        return glowingPlayers.contains(player.getUniqueId());
    }

    public void setGlowing(Player player, boolean glowing) {
        if (glowing) {
            glowingPlayers.add(player.getUniqueId());
            player.setGlowing(true);
        } else {
            glowingPlayers.remove(player.getUniqueId());
            player.setGlowing(false);
        }

        // Immediately update config
        getConfig().set("glowingPlayers", glowingPlayers.stream()
                .map(UUID::toString)
                .collect(Collectors.toList()));
        saveConfig();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (isGlowing(player)) {
            // Delay by 1 tick so glow re-applies after respawn
            Bukkit.getScheduler().runTaskLater(this, () -> player.setGlowing(true), 1L);
        }
    }
}
