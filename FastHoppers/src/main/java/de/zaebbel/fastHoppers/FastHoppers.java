package de.zaebbel.fastHoppers;

import org.bukkit.plugin.java.JavaPlugin;

public final class FastHoppers extends JavaPlugin {

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();

        // Register hopper handler
        getServer().getPluginManager().registerEvents(new HopperHandler(this), this);

        // Log startup message with current mode
        String mode = getConfig().getString("mode", "performance");
        getLogger().info("FastHoppers enabled with mode: " + mode);
    }

    @Override
    public void onDisable() {
        getLogger().info("FastHoppers disabled!");
    }
}
