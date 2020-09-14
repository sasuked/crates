package net.stoonegomes.crates.helper;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.stoonegomes.crates.StrixCrates;
import net.stoonegomes.crates.builder.ItemBuilder;
import net.stoonegomes.crates.entity.Crate;
import net.stoonegomes.crates.entity.CrateItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CrateHelper {

    private StrixCrates strixCrates;

    public CrateHelper(StrixCrates strixCrates) {
        this.strixCrates = strixCrates;
    }

    public ItemStack getKeyToCrate(Crate crate, int amount) {
        ConfigurationSection section = strixCrates.getConfig().getConfigurationSection("crates." + crate.getName() + ".key");

        ItemStack itemStack = getItemStack(section);
        itemStack.setAmount(amount);

        return itemStack;
    }

    public boolean isKeyToCrate(ItemStack itemStack, Crate crate) {
        ConfigurationSection section = strixCrates.getConfig().getConfigurationSection("crates." + crate.getName() + ".key");

        if (itemStack.getType() != Material.getMaterial(section.getInt("type"))) return false;

        if (!itemStack.hasItemMeta()) return false;
        if (!itemStack.getItemMeta().hasDisplayName() || !itemStack.getItemMeta().hasLore()) return false;

        if (!itemStack.getItemMeta().getDisplayName().equals(section.getString("name").replace("&", "§"))) return false;

        List<String> lore = section.getStringList("lore");
        lore.replaceAll(s -> s.replace("&", "§"));

        return itemStack.getItemMeta().getLore().equals(lore);
    }

    public List<CrateItem> getCrateItems(String crateName) {
        ConfigurationSection section = strixCrates.getConfig().getConfigurationSection("crates." + crateName + ".itens");

        List<CrateItem> items = new ArrayList<>();
        for (String string : section.getKeys(false)) {
            CrateItem crateItem = getCrateItem(section.getConfigurationSection(string));

            items.add(crateItem);
        }

        return items;
    }

    public CrateItem getCrateItem(ConfigurationSection section) {
        ItemStack itemStack = getItemStack(section);
        int percent = section.getInt("percentage");
        Set<String> commands = Sets.newHashSet(section.getStringList("commands"));

        return CrateItem.builder()
            .itemStack(itemStack)
            .percent(percent)
            .giveItem(!section.contains("giveItem") || section.getBoolean("giveItem"))
            .commands(section.contains("commands") ? commands : null)
            .build();
    }

    public ItemStack getItemStack(ConfigurationSection section) {
        String type = section.getString("type");
        String[] typeSplit = section.getString("type").split(":");

        ItemStack itemStack = new ItemBuilder(type.contains(":") ? Material.getMaterial(Integer.parseInt(typeSplit[0])) : Material.getMaterial(Integer.parseInt(type)))
            .amount(!section.contains("amount") ? 1 : section.getInt("amount"))
            .durability(type.contains(":") ? Integer.parseInt(typeSplit[1]) : 0)
            .build();

        ItemMeta itemMeta = itemStack.getItemMeta();

        List<String> enchantments = section.getStringList("enchantments");
        if (section.contains("enchantments")) {
            for (String string : enchantments) {
                String[] split = string.split(":");
                itemMeta.addEnchant(Enchantment.getByName(split[0]), Integer.parseInt(split[1]), true);
            }
        }

        String name = section.getString("name");
        if (section.contains("name")) itemMeta.setDisplayName(name.replace("&", "§"));

        List<String> lore = section.getStringList("lore");
        if (section.contains("lore")) {
            lore.replaceAll(s -> s.replace("&", "§"));

            itemMeta.setLore(lore);
        }

        List<String> flags = section.getStringList("flags");
        if (section.contains("flags")) {
            for (String string : flags) {
                itemMeta.addItemFlags(ItemFlag.valueOf(string));
            }
        }

        itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public void saveItemStack(String crateName, CrateItem crateItem) {
        ConfigurationSection section = strixCrates.getConfig().createSection("crates." + crateName + ".itens." + crateItem.hashCode());

        ItemStack itemStack = crateItem.getItemStack();
        ItemMeta itemMeta = itemStack.getItemMeta();

        section.set("type", itemStack.getTypeId() + ":" + itemStack.getDurability());
        section.set("amount", itemStack.getAmount());

        if (!itemStack.getEnchantments().isEmpty()) {
            List<String> enchantments = new ArrayList<>();

            for (Map.Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet()) {
                enchantments.add(entry.getKey().getName() + ":" + entry.getValue());
            }

            section.set("enchantments", enchantments);
        }

        if (itemStack.hasItemMeta()) {
            if (itemMeta.hasDisplayName()) section.set("name", itemMeta.getDisplayName().replace("§", "&"));

            if (itemMeta.hasLore()) {
                List<String> lore = Lists.newArrayList(itemMeta.getLore());
                lore.replaceAll(s -> s.replace("§", "&"));

                section.set("lore", lore);
            }

            if (!itemMeta.getItemFlags().isEmpty()) {
                List<String> itemFlags = Lists.newArrayList();

                for (ItemFlag itemFlag : itemMeta.getItemFlags()) {
                    itemFlags.add(itemFlag.name());
                }

                section.set("flags", itemFlags);
            }
        }

        section.set("giveItem", crateItem.isGiveItem());
        section.set("percentage", crateItem.getPercent());

        if (!crateItem.getCommands().isEmpty()) section.set("commands", Lists.newArrayList(crateItem.getCommands()));

        strixCrates.saveConfig();
    }

    public Hologram spawnHologram(Location location, Crate crate) {
        List<String> lines = strixCrates.getConfig().getStringList("crates." + crate.getName() + ".block.hologram");

        double y = location.getBlockY() + (lines.size() * 0.2) + 2.1;
        Location hologramLocation = new Location(location.getWorld(), location.getX() + 0.5, y, location.getZ() + 0.5);
        if (crate.getBlockHologram() == null) {
            Hologram hologram = HologramsAPI.createHologram(strixCrates, hologramLocation);

            for (String line : lines) {
                hologram.appendTextLine(line.replace("&", "§"));
            }
            hologram.appendItemLine(new ItemStack(crate.getBlockType()));

            crate.setBlockHologram(hologram);
        } else {
            crate.getBlockHologram().teleport(hologramLocation);
        }

        return crate.getBlockHologram();
    }

    public Location getCrateLocation(String crateName) {
        ConfigurationSection section = strixCrates.getLocationsFile().getConfig().getConfigurationSection("locations");
        if (section == null) return null;
        if (!section.contains(crateName)) return null;

        return new Location(Bukkit.getWorld(section.getString(crateName + ".world")), section.getInt(crateName + ".x"), section.getInt(crateName + ".y"), section.getInt(crateName + ".z"));
    }

    public void setCrateLocation(String crateName, Location location) {
        ConfigurationSection section = strixCrates.getLocationsFile().getConfig().createSection("locations." + crateName);

        section.set("x", location.getBlockX());
        section.set("y", location.getBlockY());
        section.set("z", location.getBlockZ());
        section.set("world", location.getWorld().getName());

        strixCrates.getLocationsFile().save();
    }

}
