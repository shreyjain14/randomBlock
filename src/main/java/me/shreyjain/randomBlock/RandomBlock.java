package me.shreyjain.randomBlock;

import org.bukkit.plugin.java.JavaPlugin;

public final class RandomBlock extends JavaPlugin {

    private RandomBlockListener blockListener;

    @Override
    public void onEnable() {
        // Plugin startup logic
        blockListener = new RandomBlockListener(this);
        getServer().getPluginManager().registerEvents(blockListener, this);

        // Register command
        getCommand("randomblock").setExecutor(new RandomBlockCommand());

        getLogger().info("RandomBlock plugin enabled! Place any block and it will transform into a random block!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("RandomBlock plugin disabled!");
    }
}
