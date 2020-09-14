package net.stoonegomes.crates.listener;

import net.stoonegomes.crates.StrixCrates;
import net.stoonegomes.crates.entity.Crate;
import net.stoonegomes.crates.entity.CrateItem;
import net.stoonegomes.crates.entity.process.CrateItemProcess;
import net.stoonegomes.crates.inventory.ScrollerInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AsyncPlayerChatListener implements Listener {

    private final StrixCrates strixCrates = StrixCrates.getInstance();

    public AsyncPlayerChatListener(StrixCrates strixCrates) {
        strixCrates.getServer().getPluginManager().registerEvents(this, strixCrates);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        CrateItemProcess crateItemProcess = strixCrates.getProcessItemCrateCache().getElement(player.getUniqueId());
        if (crateItemProcess == null) return;

        event.setCancelled(true);
        switch (crateItemProcess.getTier()) {
            case 1: {
                if (message.equalsIgnoreCase("cancelar")) {
                    player.sendMessage("§cVocê cancelou o processo.");

                    strixCrates.getProcessItemCrateCache().removeElement(player.getUniqueId());
                    return;
                }

                int percent;
                try {
                    percent = Integer.parseInt(event.getMessage());
                } catch (NumberFormatException exception) {
                    player.sendMessage("§cVocê precisa digitar um número válido como porcentagem.");
                    return;
                }

                if (percent > 100) {
                    player.sendMessage("§cA porcentagem de ganho deve ser até o número §f100§c apenas.");
                    return;
                }

                player.sendMessage(new String[]{
                    "",
                    "§aVocê deseja adicionar algum comando? (Atuais: §f0§a)",
                    "§7(Digite 'não' caso você queira finalizar o processo)",
                    ""
                });

                crateItemProcess.nextTier();
                crateItemProcess.getCrateItem().setPercent(percent);

                strixCrates.getProcessItemCrateCache().putElement(player.getUniqueId(), crateItemProcess);
                break;
            }
            case 2: {
                if (message.equalsIgnoreCase("não")) {
                    crateItemProcess.nextTier();

                    player.sendMessage(new String[]{
                        "",
                        "§aEsse item precisa ser enviado ao inventário do jogador?",
                        "§7(Responda com 'sim' ou 'não' para finalizar o processo)",
                        ""
                    });
                    return;
                }

                crateItemProcess.getCrateItem().getCommands().add(message);

                player.sendMessage(new String[]{
                    "",
                    "§aVocê deseja adicionar algum comando? (Atuais: §f" + crateItemProcess.getCrateItem().getCommands().size() + "§a)",
                    "§7(Digite 'não' caso você queira finalizar o processo)",
                    ""
                });
                break;
            }
            case 3: {
                if (!message.equalsIgnoreCase("sim") && !message.equalsIgnoreCase("não")) {
                    player.sendMessage("§cResponda apenas com §f'sim'§c ou §f'não'§c.");
                    return;
                }

                boolean isGiveItem = message.equalsIgnoreCase("sim");
                crateItemProcess.getCrateItem().setGiveItem(isGiveItem);

                player.sendMessage("§aYay! O item foi adicionado a esta crate com essas informações.");

                CrateItem newItem = crateItemProcess.getCrateItem();
                Crate crate = crateItemProcess.getCrate();

                crateItemProcess.getCrate().getItems().add(newItem);

                strixCrates.getCrateHelper().saveItemStack(crate.getName(), newItem);
                strixCrates.getProcessItemCrateCache().removeElement(player.getUniqueId());

                List<ItemStack> items = new ArrayList<>();
                for (CrateItem crateItem : crate.getItems()) {
                    items.add(crateItem.getItemStack());
                }

                ScrollerInventory scrollerInventory = new ScrollerInventory(player, "Conteúdo da crate", items, crate);
                scrollerInventory.open();
                break;
            }
        }
    }

}
