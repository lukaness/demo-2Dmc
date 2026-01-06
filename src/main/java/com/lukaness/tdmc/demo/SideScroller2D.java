package com.lukaness.tdmc.demo;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class SideScroller2D extends JavaPlugin implements Listener {
    private static final float SIDE_VIEW_YAW = -180f;
    private Map<Player, ArmorStand> playerModels = new HashMap<>();

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("SideScroller2D enabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.setInvisible(true);
        Location loc = player.getLocation();
        ArmorStand model = player.getWorld().spawn(loc, ArmorStand.class, stand -> {
            stand.setVisible(false);
            stand.setGravity(false);
            stand.setMarker(true);
            stand.setHelmet(new ItemStack(Material.DIAMOND_BLOCK)); // 2D sprite placeholder
        });
        playerModels.put(player, model);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    model.remove();
                    cancel();
                    return;
                }
                Location mloc = player.getLocation().clone();
                mloc.setYaw(SIDE_VIEW_YAW);
                mloc.setPitch(0);
                model.teleport(mloc);
            }
        }.runTaskTimer(this, 0L, 1L);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();
        if (to == null)
            return;
        to.setYaw(SIDE_VIEW_YAW);
        to.setPitch(0);
        to.setZ(from.getZ()); // restrict to 2D plane
        e.setTo(to);
    }
}
