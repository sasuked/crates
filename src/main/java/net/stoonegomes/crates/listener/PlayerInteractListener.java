package net.stoonegomes.crates.listener;

import net.stoonegomes.crates.StrixCrates;
import net.stoonegomes.crates.entity.Crate;
import net.stoonegomes.crates.entity.CrateItem;
import net.stoonegomes.crates.inventory.ScrollerInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerInteractListener implements Listener {

    private final StrixCrates strixCrates = StrixCrates.getInstance();

    public PlayerInteractListener(StrixCrates strixCrates) {
        strixCrates.getServer().getPluginManager().registerEvents(this, strixCrates);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Block block = event.getClickedBlock();
        if (block == null) return;

        Crate crate = strixCrates.getCrateCache().getElement($ -> $.getLocation() != null && $.getLocation().equals(block.getLocation()));
        if (crate == null) return;

        event.setCancelled(true);
        switch (event.getAction()) {
            case LEFT_CLICK_AIR:
            case RIGHT_CLICK_AIR: {
                break;
            }
            case LEFT_CLICK_BLOCK: {
                if (player.hasPermission(strixCrates.getConfig().getString("admin_permission"))) {
                    if (player.isSneaking()) {
                        block.setType(Material.AIR);

                        crate.getBlockHologram().delete();
                        crate.setBlockHologram(null);

                        strixCrates.getLocationsFile().getConfig().set("locations." + crate.getName(), null);
                        crate.setLocation(null);

                        strixCrates.getCrateCache().putElement(crate.getName(), crate);
                        return;
                    }
                }

                List<ItemStack> items = new ArrayList<>();
                for (CrateItem crateItem : crate.getItems()) {
                    items.add(crateItem.getItemStack());
                }

                ScrollerInventory scrollerInventory = new ScrollerInventory(player, "Conteúdo da crate", items, crate);
                scrollerInventory.open();
                break;
            }
            case RIGHT_CLICK_BLOCK: {
                ItemStack itemStack = player.getItemInHand();
                if (itemStack == null || !strixCrates.getCrateHelper().isKeyToCrate(itemStack, crate)) {
                    player.sendMessage("§cPara abrir uma crate você precisa estar segurando uma chave válida.");
                    return;
                }


                CrateItem finalCrateItem = null;
                for (CrateItem crateItem : crate.getItems()) {
                    if (new Random().nextInt(100) <= crateItem.getPercent()) finalCrateItem = crateItem;
                }

                if (finalCrateItem == null) finalCrateItem = crate.getItems().get(0);

                if (finalCrateItem.getCommands() != null)
                    finalCrateItem.getCommands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.getName())));
                if (finalCrateItem.isGiveItem()) {
                    if (checkIfInventoryIsFull(player)) {
                        player.getWorld().dropItem(player.getLocation(), finalCrateItem.getItemStack());
                    } else player.getInventory().addItem(finalCrateItem.getItemStack());
                }

                sendMessages(player, finalCrateItem);

                if (player.getItemInHand().getAmount() == 1) player.setItemInHand(null);
                else player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                break;
            }
        }
    }

    public void sendMessages(Player player, CrateItem crateItem) {
        if (strixCrates.getConfig().getBoolean("earn_item.titles.enable")) {
            ItemStack itemStack = crateItem.getItemStack();
            ItemMeta itemMeta = itemStack.getItemMeta();

            String name = ChatColor.stripColor(crateItem.getItemStack().getItemMeta().getDisplayName());

            String title = strixCrates.getConfig().getString("earn_item.titles.title")
                .replace("&", "§")
                .replace("{player}", player.getName())
                .replace("{amount}", String.valueOf(crateItem.getItemStack().getAmount()))
                .replace("{name}", (itemMeta != null && itemMeta.hasDisplayName()) ? name : itemStack.getType().name());

            String subTitle = strixCrates.getConfig().getString("earn_item.titles.subtitle")
                .replace("&", "§")
                .replace("{player}", player.getName())
                .replace("{amount}", String.valueOf(crateItem.getItemStack().getAmount()))
                .replace("{name}", (itemMeta != null && itemMeta.hasDisplayName()) ? name : itemStack.getType().name());

            player.sendTitle(title, subTitle);
        }

        if (strixCrates.getConfig().getBoolean("earn_item.messages.enable")) {
            ItemStack itemStack = crateItem.getItemStack();
            ItemMeta itemMeta = itemStack.getItemMeta();

            String name = ChatColor.stripColor(crateItem.getItemStack().getItemMeta().getDisplayName());

            String message = strixCrates.getConfig().getString("earn_item.messages.message")
                .replace("&", "§")
                .replace("{player}", player.getName())
                .replace("{amount}", String.valueOf(crateItem.getItemStack().getAmount()))
                .replace("{name}", (itemMeta != null && itemMeta.hasDisplayName()) ? name : itemStack.getType().name());

            player.sendMessage(message);
        }
    }

    public boolean checkIfInventoryIsFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

}
