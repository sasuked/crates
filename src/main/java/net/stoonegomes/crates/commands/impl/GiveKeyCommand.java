package net.stoonegomes.crates.commands.impl;

import net.stoonegomes.crates.StrixCrates;
import net.stoonegomes.crates.cache.impl.CrateCache;
import net.stoonegomes.crates.commands.Command;
import net.stoonegomes.crates.entity.Crate;
import net.stoonegomes.crates.helper.CrateHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveKeyCommand extends Command {

    private final CrateCache crateCache = CrateCache.getInstance();
    private final CrateHelper crateHelper = CrateHelper.getInstance();

    public GiveKeyCommand() {
        super("givekey");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission("crates.admin")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }

        if (args.length <= 2) {
            sender.sendMessage("§cTo give an crate key to someone use §f/givekey <player> <id> <amount>§c.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (!target.isOnline()) {
            sender.sendMessage("§cO jogador digitado não está online.");
            return true;
        }

        Crate crate = crateCache.getElement(args[1].toLowerCase());
        if (crate == null) {
            sender.sendMessage("§cThe typed crate wasn't found.");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException exception) {
            sender.sendMessage("§cThe amount must be a valid number.");
            return true;
        }

        if (target.getInventory().firstEmpty() == -1) {
            target.getWorld().dropItem(target.getLocation(), crateHelper.getKeyToCrate(crate, amount));
        } else target.getInventory().addItem(crateHelper.getKeyToCrate(crate, amount));
        return false;
    }

}
