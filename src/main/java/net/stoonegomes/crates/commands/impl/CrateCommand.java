package net.stoonegomes.crates.commands.impl;

import net.stoonegomes.crates.StrixCrates;
import net.stoonegomes.crates.commands.Command;
import net.stoonegomes.crates.entity.Crate;
import org.bukkit.command.CommandSender;

public class CrateCommand extends Command {

    private final StrixCrates strixCrates = StrixCrates.getInstance();

    public CrateCommand() {
        super("crate", "crates");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!sender.hasPermission(strixCrates.getConfig().getString("admin_permission"))) {
            String message = strixCrates.getConfig().getString("messages.no_perm").replace("&", "§");
            sender.sendMessage(message);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(new String[]{
                "",
                "§6§lCRATES",
                "",
                "§7/crates reload §8- §7Reinicia a configuração.",
                "§7/givekey <jogador> <tipo> <quantia> §8- §7Dá uma key a um jogador.",
                "§7/spawncrate <tipo> §8- §7Spawna uma crate.",
                ""
            });
            return true;
        }

        if ("reload".equals(args[0].toLowerCase())) {
            strixCrates.reloadConfig();

            sender.sendMessage(new String[]{
                "",
                "§6§lCRATES",
                "",
                "§7Iniciando reinicio geral das crates...",
            });

            for (Crate crate : strixCrates.getCrateCache().getValues()) {
                if (crate.getBlockHologram() != null) crate.getBlockHologram().delete();
                sender.sendMessage("§6* §7Foi limpo o holograma da crate §f" + crate.getName() + "§7.");
            }

            sender.sendMessage(new String[]{
                "",
                "§7Iniciando carregamento das crates...",
                ""
            });

            strixCrates.getCrateCache().load();

            sender.sendMessage(new String[]{
                "§7Você reiniciou os arquivos de configuração com sucesso.",
                "§7Foram carregadas §f" + strixCrates.getCrateCache().size() + "§7 crates.",
                ""
            });
        } else {
            sender.sendMessage(new String[]{
                "",
                "§6§lCRATES",
                "",
                "§7/crates reload §8- §7Reinicia a configuração.",
                "§7/givekey <jogador> <tipo> <quantia> §8- §7Dá uma key a um jogador.",
                "§7/spawncrate <tipo> §8- §7Spawna uma crate.",
                ""
            });
        }
        return false;
    }

}
