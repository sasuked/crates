package net.stoonegomes.crates.inventory.scroller;

import com.google.common.collect.Sets;
import net.stoonegomes.crates.builder.ItemBuilder;
import net.stoonegomes.crates.cache.impl.ProcessItemCrateCache;
import net.stoonegomes.crates.entity.CrateItem;
import net.stoonegomes.crates.entity.process.CrateItemProcess;
import net.stoonegomes.crates.inventory.CustomInventory;
import net.stoonegomes.crates.inventory.item.ClickableItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DefaultContentsInventory extends CustomInventory {

    private final ProcessItemCrateCache processItemCrateCache = ProcessItemCrateCache.getInstance();

    private ContentsInventory contentsInventory;

    public DefaultContentsInventory(String title, ContentsInventory contentsInventory) {
        super(title, 6, true);

        this.contentsInventory = contentsInventory;
    }

    @Override
    public void init(Player player) {
        ClickableItem nextPage = ClickableItem.builder()
            .itemStack(new ItemBuilder(Material.ARROW).name("§aNext page").build())
            .eventConsumer(event -> contentsInventory.goToNextPage(player))
            .build();

        ClickableItem previousPage = ClickableItem.builder()
            .itemStack(new ItemBuilder(Material.ARROW).name("§aPrevious page").build())
            .eventConsumer(event -> contentsInventory.goToPreviousPage(player))
            .build();

        ClickableItem addContent = ClickableItem.builder()
            .itemStack(new ItemBuilder(Material.GOLD_INGOT)
                .name("§aAdd new item")
                .lore("§7Click here to add a new item to this crate.")
                .build())
            .eventConsumer(event -> {
                if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
                    player.sendMessage("§cYou need to hold an item to add to the crate.");
                    return;
                }

                ItemStack itemStack = player.getItemInHand();
                CrateItemProcess crateItemProcess = CrateItemProcess.builder()
                    .tier(1)
                    .crate(contentsInventory.getCrate())
                    .crateItem(CrateItem.builder()
                        .itemStack(itemStack)
                        .commands(Sets.newHashSet())
                        .percent(0)
                        .build())
                    .build();

                processItemCrateCache.putElement(player.getUniqueId(), crateItemProcess);

                player.closeInventory();
                player.sendMessage(new String[]{
                    "",
                    "§aType on chat the percentage of the item.",
                    "§7(Type 'cancel' to cancel this process)",
                    ""
                });
            })
            .build();

        setItem(44, addContent);

        if ((contentsInventory.getCurrentPage() + 1) < contentsInventory.getPages().size()) setItem(26, nextPage);
        if (contentsInventory.getCurrentPage() > 0) setItem(18, previousPage);
    }

}
