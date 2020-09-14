package net.stoonegomes.crates.inventory.listener;

import com.google.common.collect.Sets;
import net.stoonegomes.crates.StrixCrates;
import net.stoonegomes.crates.entity.Crate;
import net.stoonegomes.crates.entity.CrateItem;
import net.stoonegomes.crates.entity.process.CrateItemProcess;
import net.stoonegomes.crates.inventory.ScrollerInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    private final StrixCrates strixCrates = StrixCrates.getInstance();

    public InventoryClickListener(StrixCrates strixCrates) {
        strixCrates.getServer().getPluginManager().registerEvents(this, strixCrates);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) return;

        Player player = (Player) event.getWhoClicked();
        if (player == null) return;

        if (inventory.getName().equals("Conteúdo da crate")) {
            ScrollerInventory scrollerInventory = strixCrates.getScrollerInventoryCache().getElement(player.getUniqueId());
            if (scrollerInventory == null) return;

            event.setCancelled(true);
            switch (event.getRawSlot()) {
                case 18: {
                    scrollerInventory.goToPreviousPage();
                    break;
                }
                case 26: {
                    scrollerInventory.goToNextPage();
                    break;
                }
                case 40: {
                    player.closeInventory();
                    break;
                }
                case 44: {
                    if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
                        player.sendMessage("§cVocê precisa estar segurando algum item para adicionar na crate.");
                        return;
                    }

                    String crateName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(1).split(" ")[3]);
                    Crate crate = strixCrates.getCrateCache().getElement(crateName.toLowerCase());
                    if (crate == null) return;

                    ItemStack itemStack = player.getItemInHand();
                    CrateItemProcess crateItemProcess = CrateItemProcess.builder()
                        .tier(1)
                        .crate(crate)
                        .crateItem(CrateItem.builder()
                            .itemStack(itemStack)
                            .commands(Sets.newHashSet())
                            .percent(0)
                            .build())
                        .build();

                    strixCrates.getProcessItemCrateCache().putElement(player.getUniqueId(), crateItemProcess);

                    player.closeInventory();
                    player.sendMessage(new String[]{
                        "",
                        "§aDiga no chat a porcentagem de ganho desse item.",
                        "§7(Digite 'cancelar para cancelar para cancelar o processo)",
                        ""
                    });
                }
            }
        }
    }

}
