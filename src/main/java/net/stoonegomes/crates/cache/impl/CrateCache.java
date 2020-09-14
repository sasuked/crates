package net.stoonegomes.crates.cache.impl;

import net.stoonegomes.crates.cache.Cache;
import net.stoonegomes.crates.entity.Crate;
import net.stoonegomes.crates.helper.CrateHelper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class CrateCache extends Cache<String, Crate> {

    private static CrateCache instance;

    public static CrateCache getInstance() {
        if (instance == null) instance = new CrateCache();
        return instance;
    }

    private final CrateHelper crateHelper = CrateHelper.getInstance();

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
                .location(crateHelper.getCrateLocation(string))
                .items(crateHelper.getCrateItems(string))
                .blockType(blockMaterial)
                .build();

            Location location = crateHelper.getCrateLocation(crate.getName());

            if (location != null) crateHelper.spawnHologram(location, crate);
            else crate.setBlockHologram(null);

            putElement(string, crate);
        }
    }

}
