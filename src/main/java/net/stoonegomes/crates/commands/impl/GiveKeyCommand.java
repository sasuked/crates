package net.stoonegomes.crates.commands.impl;

import net.stoonegomes.crates.StrixCrates;
import net.stoonegomes.crates.commands.Command;
import net.stoonegomes.crates.entity.Crate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveKeyCommand extends Command {

    private final StrixCrates strixCrates = StrixCrates.getInstance();

    public GiveKeyCommand() {
        super("givekey", "darkey");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission(strixCrates.getConfig().getString("admin_permission"))) {
            String message = strixCrates.getConfig().getString("messages.no_perm").replace("&", "§");
            sender.sendMessage(message);
            return true;
        }

        if (args.length <= 2) {
            sender.sendMessage("§cPara dar uma chave a alguém use §f/givekey <player> <tipo> <quantia>§c.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (!target.isOnline()) {
            sender.sendMessage("§cO jogador digitado não está online.");
            return true;
        }

        Crate crate = strixCrates.getCrateCache().getElement(args[1].toLowerCase());
        if (crate == null) {
            sender.sendMessage("§cA crate digitada não existe.");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException exception) {
            sender.sendMessage("§cA quantia digitada precisa ser um número válido.");
            return true;
        }

        if (checkIfInventoryIsFull(target)) {
            target.getWorld().dropItem(target.getLocation(), strixCrates.getCrateHelper().getKeyToCrate(crate, amount));
        } else target.getInventory().addItem(strixCrates.getCrateHelper().getKeyToCrate(crate, amount));
        return false;
    }

    public boolean checkIfInventoryIsFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

}
