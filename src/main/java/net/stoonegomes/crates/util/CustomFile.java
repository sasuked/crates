package net.stoonegomes.crates.util;

import net.stoonegomes.crates.StrixCrates;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class CustomFile {

    private String fileName;

    private File file;
    private FileConfiguration configuration;

    public CustomFile(String dir, String fileName) {
        this.fileName = fileName;
        file = new File(dir, fileName);
    }

    public void saveDefaultConfig() {
        try {
            if (!file.exists()) {
                file.createNewFile();
                StrixCrates.getInstance().saveResource(fileName, false);
            }

            configuration = YamlConfiguration.loadConfiguration(file);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return configuration;
    }

    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            getConfig().save(file);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}