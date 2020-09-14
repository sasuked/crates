package net.stoonegomes.crates.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;

import java.lang.reflect.Field;
import java.util.Arrays;

public abstract class Command extends BukkitCommand {

    public Command(String name, String... aliases) {
        super(name);
        setAliases(Arrays.asList(aliases));

        try {
            Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            commandMap.register(name, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
