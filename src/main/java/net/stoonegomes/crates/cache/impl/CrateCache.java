package net.stoonegomes.crates.cache.impl;

import net.stoonegomes.crates.cache.Cache;
import net.stoonegomes.crates.entity.Crate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class CrateCache extends Cache<String, Crate> {

    public void load() {
        ConfigurationSection section = strixCrates.getConfig().getConfigurationSection("crates");

        for (String string : section.getKeys(false)) {
            String configMaterial = section.getString(string + ".block.type");
            String[] configMaterialSplit = configMaterial.split(":");

            Material blockMaterial = !configMaterial.contains(":")
                ? new ItemStack(Material.getMaterial(Integer.parseInt(configMaterialSplit[0]))).getType()
                : new ItemStack(Material.getMaterial(Integer.parseInt(configMaterialSplit[0])), (short) Integer.parseInt(configMaterialSplit[1])).getType();

            Crate crate = Crate.builder()
                .name(string)
                .location(strixCrates.getCrateHelper().getCrateLocation(string))
                .items(strixCrates.getCrateHelper().getCrateItems(string))
                .blockType(blockMaterial)
                .build();

            Location location = strixCrates.getCrateHelper().getCrateLocation(crate.getName());

            if (location != null) strixCrates.getCrateHelper().spawnHologram(location, crate);
            else crate.setBlockHologram(null);

            putElement(string, crate);
        }
    }

}
