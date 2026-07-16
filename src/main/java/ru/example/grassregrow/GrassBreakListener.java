package ru.example.grassregrow;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GrassBreakListener implements Listener {

    private final JavaPlugin plugin;
    private final GrassConfig config;

    public GrassBreakListener(JavaPlugin plugin, GrassConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!config.isEnabled()) {
            return;
        }

        if (config.isIgnoreCreativeMode() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        Block block = event.getBlock();
        Material type = block.getType();

        if (type == Material.SHORT_GRASS) {
            if (config.isShortGrassEnabled()) {
                scheduleShortGrassRespawn(block.getLocation());
            }
            return;
        }

        if (type == Material.TALL_GRASS) {
            if (config.isTallGrassEnabled()) {
                Location baseLocation = getBaseLocation(block);
                scheduleTallGrassRespawn(baseLocation);
            }
        }
    }

    /**
     * Высокая трава состоит из двух блоков (низ/верх, свойство Bisected.Half).
     * Определяет координаты нижнего блока, независимо от того, какую половину сломал игрок.
     */
    private Location getBaseLocation(Block block) {
        BlockData data = block.getBlockData();
        if (data instanceof Bisected bisected && bisected.getHalf() == Bisected.Half.TOP) {
            return block.getLocation().add(0, -1, 0);
        }
        return block.getLocation();
    }

    private void scheduleShortGrassRespawn(Location location) {
        Location target = location.clone();
        long delayTicks = config.getShortGrassRespawnTicks();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!config.isEnabled() || !config.isShortGrassEnabled()) {
                return;
            }
            if (!isChunkReady(target)) {
                return;
            }

            Block block = target.getBlock();
            if (config.isCheckLocationBeforeRespawn() && block.getType() != Material.AIR) {
                return;
            }
            block.setType(Material.SHORT_GRASS);
        }, delayTicks);
    }

    private void scheduleTallGrassRespawn(Location baseLocation) {
        Location target = baseLocation.clone();
        long delayTicks = config.getTallGrassRespawnTicks();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!config.isEnabled() || !config.isTallGrassEnabled()) {
                return;
            }
            if (!isChunkReady(target)) {
                return;
            }

            Block bottom = target.getBlock();
            Block top = target.clone().add(0, 1, 0).getBlock();

            boolean placeBottom = !config.isCheckLocationBeforeRespawn() || bottom.getType() == Material.AIR;
            boolean placeTop = !config.isCheckLocationBeforeRespawn() || top.getType() == Material.AIR;

            if (placeBottom) {
                bottom.setType(Material.TALL_GRASS);
                setHalf(bottom, Bisected.Half.BOTTOM);
            }
            if (placeTop) {
                top.setType(Material.TALL_GRASS);
                setHalf(top, Bisected.Half.TOP);
            }
        }, delayTicks);
    }

    private void setHalf(Block block, Bisected.Half half) {
        BlockData data = block.getBlockData();
        if (data instanceof Bisected bisected) {
            bisected.setHalf(half);
            block.setBlockData(bisected);
        }
    }

    /**
     * Проверяет, что чанк с этой локацией загружен, чтобы не дёргать мир зря.
     * Если чанк выгружен, трава там просто не возродится (появится в следующий раз,
     * когда игрок сломает траву в этом месте после того как чанк снова будет загружен).
     */
    private boolean isChunkReady(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return false;
        }
        int chunkX = location.getBlockX() >> 4;
        int chunkZ = location.getBlockZ() >> 4;
        return world.isChunkLoaded(chunkX, chunkZ);
    }
}
