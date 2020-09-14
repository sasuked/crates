package net.stoonegomes.crates.commands.impl;

import net.stoonegomes.crates.StrixCrates;
import net.stoonegomes.crates.cache.impl.CrateCache;
import net.stoonegomes.crates.commands.Command;
import net.stoonegomes.crates.entity.Crate;
import org.bukkit.command.CommandSender;

public class CrateCommand extends Command {

    private final StrixCrates strixCrates = StrixCrates.getInstance();
    private final CrateCache crateCache = CrateCache.getInstance();

    public CrateCommand() {
        super("crate", "crates");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission("crates.admin")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(new String[]{
                "",
                "§6§lCRATES",
                "",
                "§7/crates reload §8- §7Reload the crates.",
                "§7/givekey <player> <id> <amount> §8- §7Give a key to someone.",
                "§7/spawncrate <id> §8- §7Spawn a crate.",
                ""
            });
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            strixCrates.reloadConfig();

            sender.sendMessage(new String[]{
                "",
                "§6§lCRATES",
                "",
                "§7Starting general crate reloading...",
            });

            for (Crate crate : crateCache.getValues()) {
                if (crate.getBlockHologram() != null) crate.getBlockHologram().delete();
                sender.sendMessage("§6* §7Was cleared the §f" + crate.getName() + "§7 hologram.");
            }

            sender.sendMessage(new String[]{
                "",
                "§7Starting crates load...",
                ""
            });

            crateCache.load();

            sender.sendMessage(new String[]{
                "§7You reloaded the crates successfully.",
                "§7Was load §f" + crateCache.size() + "§7 crates.",
                ""
            });
        }
        return false;
    }

}
