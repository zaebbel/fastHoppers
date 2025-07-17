package de.zaebbel.fastHoppers;

import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class HopperHandler implements Listener {
    private final FastHoppers plugin;
    private final String mode;
    private final double speedMultiplier;

    public HopperHandler(FastHoppers plugin) {
        this.plugin = plugin;
        this.mode = plugin.getConfig().getString("mode", "performance");

        if (mode.equals("custom")) {
            double customSpeed = plugin.getConfig().getDouble("speeds.custom.multiplier", 1.0);
            // Clamp the custom speed between 1 and 100
            this.speedMultiplier = Math.max(1.0, Math.min(100.0, customSpeed));
        } else {
            this.speedMultiplier = plugin.getConfig().getDouble("speeds." + mode + ".multiplier", 1.0);
        }
    }

    @EventHandler
    public void onHopperMove(InventoryMoveItemEvent event) {
        if (!(event.getInitiator().getHolder() instanceof Hopper)) {
            return;
        }

        // Cancel the original event
        event.setCancelled(true);

        Inventory source = event.getSource();
        Inventory destination = event.getDestination();

        // If speedMultiplier is 100 or mode is hard, skip hopper
        if (mode.equals("hard") || speedMultiplier >= 100) {
            if (destination.getType() == InventoryType.CHEST
                || destination.getType() == InventoryType.BARREL
                || destination.getType() == InventoryType.DISPENSER
                || destination.getType() == InventoryType.DROPPER) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    destination.addItem(event.getItem());
                    source.removeItem(event.getItem());
                });
            }
            return;
        }

        // Calculate delay based on multiplier (8 ticks is vanilla speed)
        long delay = Math.max(1, (long) (8 / speedMultiplier));

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (destination.firstEmpty() != -1) {
                destination.addItem(event.getItem());
                source.removeItem(event.getItem());
            }
        }, delay);
    }
}
