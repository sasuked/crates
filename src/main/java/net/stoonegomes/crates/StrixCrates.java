package net.stoonegomes.crates;

import lombok.Getter;
import net.stoonegomes.crates.cache.impl.CrateCache;
import net.stoonegomes.crates.cache.impl.ProcessItemCrateCache;
import net.stoonegomes.crates.commands.impl.CrateCommand;
import net.stoonegomes.crates.commands.impl.GiveKeyCommand;
import net.stoonegomes.crates.commands.impl.SpawnCrateCommand;
import net.stoonegomes.crates.helper.CrateHelper;
import net.stoonegomes.crates.inventory.cache.ScrollerInventoryCache;
import net.stoonegomes.crates.inventory.listener.InventoryClickListener;
import net.stoonegomes.crates.listener.AsyncPlayerChatListener;
import net.stoonegomes.crates.listener.PlayerInteractListener;
import net.stoonegomes.crates.util.CustomFile;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class StrixCrates extends JavaPlugin {

    @Getter
    private static StrixCrates instance;

    private ScrollerInventoryCache scrollerInventoryCache;

    private CustomFile locationsFile;

    private CrateHelper crateHelper;
    private CrateCache crateCache;

    private ProcessItemCrateCache processItemCrateCache;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        scrollerInventoryCache = new ScrollerInventoryCache();

        locationsFile = new CustomFile(getDataFolder().getPath(), "locations.db");
        locationsFile.saveDefaultConfig();

        crateHelper = new CrateHelper(this);

        crateCache = new CrateCache();
        crateCache.load();

        processItemCrateCache = new ProcessItemCrateCache();

        new PlayerInteractListener(this);
        new InventoryClickListener(this);
        new AsyncPlayerChatListener(this);

        new SpawnCrateCommand();
        new GiveKeyCommand();
        new CrateCommand();
    }

}
