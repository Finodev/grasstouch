package ru.example.grassregrow;

import org.bukkit.plugin.java.JavaPlugin;

public final class GrassRegrowPlugin extends JavaPlugin {

    private GrassConfig grassConfig;

    @Override
    public void onEnable() {
        // Создаёт config.yml при первом запуске, если его ещё нет
        saveDefaultConfig();

        this.grassConfig = new GrassConfig(this);

        getServer().getPluginManager().registerEvents(new GrassBreakListener(this, grassConfig), this);

        if (getCommand("grassregrow") != null) {
            getCommand("grassregrow").setExecutor(new GrassCommand(grassConfig));
        }

        getLogger().info("GrassRegrow включен.");
    }

    @Override
    public void onDisable() {
        getLogger().info("GrassRegrow выключен.");
    }

    public GrassConfig getGrassConfig() {
        return grassConfig;
    }
}
