package net.stoonegomes.crates.listener;

import net.stoonegomes.crates.StrixCrates;
import net.stoonegomes.crates.cache.impl.CrateCache;
import net.stoonegomes.crates.entity.Crate;
import net.stoonegomes.crates.entity.CrateItem;
import net.stoonegomes.crates.helper.CrateHelper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class PlayerInteractListener implements Listener {

    private final CrateHelper crateHelper = CrateHelper.getInstance();
    private final CrateCache crateCache = CrateCache.getInstance();

    private final StrixCrates strixCrates = StrixCrates.getInstance();

    public PlayerInteractListener() {
        strixCrates.getServer().getPluginManager().registerEvents(this, strixCrates);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Block block = event.getClickedBlock();
        if (block == null) return;

        Crate crate = crateCache.getElement($ -> $.getLocation() != null && $.getLocation().equals(block.getLocation()));
        if (crate == null) return;

        event.setCancelled(true);
        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
            case RIGHT_CLICK_AIR: {
                break;
            }
            case LEFT_CLICK_BLOCK: {
                if (player.hasPermission("crates.admin")) {
                    if (player.isSneaking()) {
                        block.setType(Material.AIR);

                        crate.getBlockHologram().delete();
                        crate.setBlockHologram(null);

                        strixCrates.getLocationsFile().getConfig().set("locations." + crate.getName(), null);
                        crate.setLocation(null);

                        crateCache.putElement(crate.getName(), crate);
                        return;
                    }
                }

                crateHelper.openContentsInventory(crate, player);
                break;
            }
            case RIGHT_CLICK_BLOCK: {
                ItemStack itemStack = player.getItemInHand();
                if (itemStack == null || !crateHelper.isKeyToCrate(itemStack, crate)) {
                    player.sendMessage("§cTo open that crate do you need to hold an valid key.");
                    return;
                }

                CrateItem finalCrateItem = null;
                for (CrateItem crateItem : crate.getItems()) {
                    if (new Random().nextInt(100) <= crateItem.getPercent()) finalCrateItem = crateItem;
                }

                if (finalCrateItem == null) finalCrateItem = crate.getItems().get(0);

                if (finalCrateItem.getCommands() != null)
                    finalCrateItem.getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName())));
                else if (finalCrateItem.isGiveItem()) {
                    if (player.getInventory().firstEmpty() == -1) {
                        player.getWorld().dropItem(player.getLocation(), finalCrateItem.getItemStack());
                    } else player.getInventory().addItem(finalCrateItem.getItemStack());
                }

                player.sendMessage("§aYou earned §f" + finalCrateItem.getItemStack().getAmount() + "x items on that crate.");

                if (player.getItemInHand().getAmount() == 1) player.setItemInHand(null);
                else player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                break;
            }
        }
    }

}
