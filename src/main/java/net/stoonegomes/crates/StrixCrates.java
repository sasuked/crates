package net.stoonegomes.crates;

import lombok.Getter;
import net.stoonegomes.crates.commands.impl.CrateCommand;
import net.stoonegomes.crates.commands.impl.GiveKeyCommand;
import net.stoonegomes.crates.commands.impl.SpawnCrateCommand;
import net.stoonegomes.crates.inventory.listener.InventoryClickListener;
import net.stoonegomes.crates.listener.AsyncPlayerChatListener;
import net.stoonegomes.crates.listener.PlayerInteractListener;
import net.stoonegomes.crates.file.CustomFile;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class StrixCrates extends JavaPlugin {

    @Getter
    private static StrixCrates instance;

    private CustomFile locationsFile;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        locationsFile = new CustomFile(getDataFolder().getPath(), "locations.db");
        locationsFile.saveDefaultConfig();

        new PlayerInteractListener();
        new InventoryClickListener(this);
        new AsyncPlayerChatListener(this);

        new SpawnCrateCommand();
        new GiveKeyCommand();
        new CrateCommand();
    }

}
