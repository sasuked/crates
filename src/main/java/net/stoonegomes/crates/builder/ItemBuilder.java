package net.stoonegomes.crates.builder;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

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

    public ItemBuilder name(String name) {
        itemMeta.setDisplayName(name);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder lore(String... lore) {
        itemMeta.setLore(Lists.newArrayList(lore));
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder addLore(String... lore) {
        List<String> actualLore = itemMeta.getLore();
        actualLore.addAll(Arrays.asList(lore));
        itemMeta.setLore(actualLore);
        itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder durability(int durability) {
        itemStack.setDurability((short) durability);

        return this;
    }

    public ItemBuilder owner(String owner) {
        SkullMeta skullMeta = (SkullMeta) itemMeta;
        skullMeta.setOwner(owner);

        itemStack.setItemMeta(skullMeta);

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