package net.stoonegomes.crates.builder;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemBuilder name(boolean traduce, String name) {
        itemMeta.setDisplayName(traduce ? name.replace("&", "§") : name);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder lore(boolean traduce, List<String> lore) {
        if (traduce) {
            List<String> list = new ArrayList<>();
            for (String string : lore) list.add(string.replace("&", "§"));

            itemMeta.setLore(list);
        } else {
            itemMeta.setLore(lore);
        }
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder lore(boolean traduce, String... lore) {
        lore(traduce, Arrays.asList(lore));

        return this;
    }

    public ItemBuilder durability(int durability) {
        itemStack.setDurability((short) durability);

        return this;
    }


    public ItemBuilder enchantment(Enchantment enchantment, int value) {
        itemStack.addUnsafeEnchantment(enchantment, value);

        return this;
    }

    public ItemBuilder amount(int amount) {
        itemStack.setAmount(amount);

        return this;
    }

    public ItemStack build() {
        return itemStack;
    }

}
