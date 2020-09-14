package net.stoonegomes.crates.inventory;

import net.stoonegomes.crates.StrixCrates;
import net.stoonegomes.crates.builder.ItemBuilder;
import net.stoonegomes.crates.entity.Crate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ScrollerInventory {

    private Player player;

    private int currentPage;
    private List<Inventory> pages;

    public ScrollerInventory(Player player, String name, List<ItemStack> contents, Crate crate) {
        this.player = player;
        currentPage = 0;
        pages = new ArrayList<>();

        StrixCrates.getInstance().getScrollerInventoryCache().putElement(player.getUniqueId(), this);
        /*
        Organizing the inventory
         */

        Inventory inventory = getDefaultPage(name, player, crate);
        int i = 0;

        Integer[] ALLOWED_SLOTS = new Integer[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};

        for (ItemStack itemStack : contents) {
            if (ALLOWED_SLOTS[i] == 43) {
                inventory.setItem(ALLOWED_SLOTS[i], itemStack);

                currentPage++;
                i = 0;

                inventory = getDefaultPage(name, player, crate);
                continue;
            }

            inventory.setItem(ALLOWED_SLOTS[i], itemStack);
            if (i == 0) pages.add(inventory);

            i++;
        }

        currentPage = 0;
    }

    public void open() {
        player.openInventory(pages.get(currentPage));
    }

    public void goToNextPage() {
        if (currentPage >= pages.size()) return;

        player.openInventory(pages.get(currentPage + 1));
        currentPage++;
    }

    public void goToPreviousPage() {
        if (currentPage == 0) return;

        player.openInventory(pages.get(currentPage - 1));
        currentPage--;
    }

    public Inventory getPage(int index) {
        return pages.get(index);
    }

    private Inventory getDefaultPage(String name, Player player, Crate crate) {
        Inventory inventory = Bukkit.createInventory(null, 5 * 9, name);

        ItemStack nextPage = new ItemBuilder(Material.ARROW).name(false, "§aPróxima página").build();
        ItemStack previousPage = new ItemBuilder(Material.ARROW).name(false, "§aPágina anterior").build();
        ItemStack back = new ItemBuilder(Material.ARROW).name(false, "§aSair").build();

        ItemStack addNewItem = new ItemBuilder(Material.NAME_TAG)
            .name(false, "§aAdicionar novo item")
            .lore(false, "§7Clique aqui para adicionar um novo item a essa crate.", "§7Tipo da crate: §f" + crate.getName())
            .build();

        inventory.setItem(40, back);

        if (currentPage != pages.size()) inventory.setItem(26, nextPage);
        if (player.hasPermission(StrixCrates.getInstance().getConfig().getString("admin_permission")))
            inventory.setItem(44, addNewItem);
        if (currentPage != 0) inventory.setItem(18, previousPage);

        return inventory;
    }

}
