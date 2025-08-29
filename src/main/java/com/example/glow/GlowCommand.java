package com.example.glow;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlowCommand implements CommandExecutor {

    private final GlowPlugin plugin;

    public GlowCommand(GlowPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        boolean currentlyGlowing = plugin.isGlowing(player);
        plugin.setGlowing(player, !currentlyGlowing);

        if (currentlyGlowing) {
            player.sendMessage("§eGlow disabled.");
        } else {
            player.sendMessage("§aGlow enabled!");
        }

        return true;
    }
}
