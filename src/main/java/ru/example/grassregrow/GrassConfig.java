package ru.example.grassregrow;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Читает config.yml и хранит значения в удобном виде.
 * Время хранится сразу в тиках (1 секунда = 20 тиков), чтобы не пересчитывать его каждый раз.
 */
public class GrassConfig {

    private final JavaPlugin plugin;

    private boolean enabled;

    private boolean shortGrassEnabled;
    private long shortGrassRespawnSeconds;
    private long shortGrassRespawnTicks;

    private boolean tallGrassEnabled;
    private long tallGrassRespawnSeconds;
    private long tallGrassRespawnTicks;

    private boolean ignoreCreativeMode;
    private boolean checkLocationBeforeRespawn;

    public GrassConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
    }

    /**
     * Перечитывает config.yml с диска и обновляет все значения.
     * Используется как при запуске плагина, так и командой /grassregrow reload.
     */
    public void load() {
        plugin.reloadConfig();
        FileConfiguration cfg = plugin.getConfig();

        enabled = cfg.getBoolean("enabled", true);

        shortGrassEnabled = cfg.getBoolean("short-grass.enabled", true);
        shortGrassRespawnSeconds = cfg.getLong("short-grass.respawn-seconds", 60);
        shortGrassRespawnTicks = shortGrassRespawnSeconds * 20L;

        tallGrassEnabled = cfg.getBoolean("tall-grass.enabled", true);
        tallGrassRespawnSeconds = cfg.getLong("tall-grass.respawn-seconds", 180);
        tallGrassRespawnTicks = tallGrassRespawnSeconds * 20L;

        ignoreCreativeMode = cfg.getBoolean("ignore-creative-mode", false);
        checkLocationBeforeRespawn = cfg.getBoolean("check-location-before-respawn", true);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isShortGrassEnabled() {
        return shortGrassEnabled;
    }

    public long getShortGrassRespawnTicks() {
        return shortGrassRespawnTicks;
    }

    public long getShortGrassRespawnSeconds() {
        return shortGrassRespawnSeconds;
    }

    public boolean isTallGrassEnabled() {
        return tallGrassEnabled;
    }

    public long getTallGrassRespawnTicks() {
        return tallGrassRespawnTicks;
    }

    public long getTallGrassRespawnSeconds() {
        return tallGrassRespawnSeconds;
    }

    public boolean isIgnoreCreativeMode() {
        return ignoreCreativeMode;
    }

    public boolean isCheckLocationBeforeRespawn() {
        return checkLocationBeforeRespawn;
    }

    // ---- Сеттеры: применяются сразу и сохраняются в config.yml ----

    public void setEnabled(boolean value) {
        this.enabled = value;
        plugin.getConfig().set("enabled", value);
        plugin.saveConfig();
    }

    public void setShortGrassEnabled(boolean value) {
        this.shortGrassEnabled = value;
        plugin.getConfig().set("short-grass.enabled", value);
        plugin.saveConfig();
    }

    public void setTallGrassEnabled(boolean value) {
        this.tallGrassEnabled = value;
        plugin.getConfig().set("tall-grass.enabled", value);
        plugin.saveConfig();
    }

    public void setShortGrassRespawnSeconds(long seconds) {
        this.shortGrassRespawnSeconds = seconds;
        this.shortGrassRespawnTicks = seconds * 20L;
        plugin.getConfig().set("short-grass.respawn-seconds", seconds);
        plugin.saveConfig();
    }

    public void setTallGrassRespawnSeconds(long seconds) {
        this.tallGrassRespawnSeconds = seconds;
        this.tallGrassRespawnTicks = seconds * 20L;
        plugin.getConfig().set("tall-grass.respawn-seconds", seconds);
        plugin.saveConfig();
    }
}
