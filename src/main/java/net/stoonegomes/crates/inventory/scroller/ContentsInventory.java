package net.stoonegomes.crates.inventory.scroller;

import lombok.Getter;
import net.stoonegomes.crates.entity.Crate;
import net.stoonegomes.crates.inventory.CustomInventory;
import net.stoonegomes.crates.inventory.item.ClickableItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ContentsInventory {

    private int currentPage;
    private List<CustomInventory> pages;
    private Crate crate;

    public ContentsInventory(List<ClickableItem> items, String title, Crate crate) {
        currentPage = 0;
        pages = new ArrayList<>();
        this.crate = crate;

        CustomInventory inventory = new DefaultContentsInventory(title, this);

        Integer[] ALLOWED_SLOTS = new Integer[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
        int i = 0;

        for (ClickableItem clickableItem : items) {

            if (ALLOWED_SLOTS[i] == 43) {
                inventory.setItem(ALLOWED_SLOTS[i], clickableItem);

                currentPage++;
                i = 0;

                inventory = new DefaultContentsInventory(title, this);
                continue;
            }

            inventory.setItem(ALLOWED_SLOTS[i], clickableItem);
            if (i == 0) pages.add(inventory);

            i++;
        }
        currentPage = 0;
    }

    public void open(Player player) {
        if (pages.size() == 0) return;

        CustomInventory customInventory = pages.get(currentPage);
        customInventory.openInventory(player);
    }

    public void goToNextPage(Player player) {
        if ((currentPage + 1) >= pages.size()) return;
        currentPage++;

        CustomInventory customInventory = pages.get(currentPage);
        customInventory.openInventory(player);
    }

    public void goToPreviousPage(Player player) {
        if (currentPage == 0) return;
        currentPage--;

        CustomInventory customInventory = pages.get(currentPage);
        customInventory.openInventory(player);
    }

    public Inventory getPage(int index) {
        return pages.get(index).getInventory();
    }

}
