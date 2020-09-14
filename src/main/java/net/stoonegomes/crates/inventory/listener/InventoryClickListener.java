package net.stoonegomes.crates.inventory.listener;

import net.stoonegomes.crates.StrixCrates;
import net.stoonegomes.crates.inventory.CustomInventory;
import net.stoonegomes.crates.inventory.item.ClickableItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryClickListener implements Listener {

    public InventoryClickListener(StrixCrates strixCrates) { strixCrates.getServer().getPluginManager().registerEvents(this, strixCrates); }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();

        if (inventory.getHolder() instanceof CustomInventory) {
            CustomInventory customInventory = (CustomInventory) inventory.getHolder();
            if (customInventory.isCancellable()) event.setCancelled(true);

            ClickableItem clickableItem = customInventory.getItem(event.getSlot());
            if (clickableItem == null) return;

            if (clickableItem.getEventConsumer() != null) clickableItem.getEventConsumer().accept(event);
        }
    }

}
