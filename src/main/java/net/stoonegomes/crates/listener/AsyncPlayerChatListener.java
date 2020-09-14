package net.stoonegomes.crates.listener;

import net.stoonegomes.crates.StrixCrates;
import net.stoonegomes.crates.builder.ItemBuilder;
import net.stoonegomes.crates.cache.impl.ProcessItemCrateCache;
import net.stoonegomes.crates.entity.Crate;
import net.stoonegomes.crates.entity.process.CrateItemProcess;
import net.stoonegomes.crates.helper.CrateHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AsyncPlayerChatListener implements Listener {

    private final CrateHelper crateHelper = CrateHelper.getInstance();
    private final ProcessItemCrateCache processItemCrateCache = ProcessItemCrateCache.getInstance();

    public AsyncPlayerChatListener(StrixCrates strixCrates) {
        strixCrates.getServer().getPluginManager().registerEvents(this, strixCrates);
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        CrateItemProcess crateItemProcess = processItemCrateCache.getElement(player.getUniqueId());
        if (crateItemProcess == null) return;

        event.setCancelled(true);
        switch (crateItemProcess.getTier()) {
            case 1: {
                if (message.equalsIgnoreCase("cancel")) {
                    player.sendMessage("§cYou cancelled the process.");

                    processItemCrateCache.removeElement(player.getUniqueId());
                    return;
                }

                int percent;
                try {
                    percent = Integer.parseInt(event.getMessage());
                } catch (NumberFormatException exception) {
                    player.sendMessage("§cYou need to use an valid number to percent.");
                    return;
                }

                if (percent > 100) {
                    player.sendMessage("§cThe percentage should only be up to 100");
                    return;
                }

                player.sendMessage(new String[]{
                    "",
                    "§aDo you need to add a command? (Current: §f0§a)",
                    "§7(Type 'no' when you want to go to the next process)",
                    ""
                });

                crateItemProcess.nextTier();
                crateItemProcess.getCrateItem().setPercent(percent);
                break;
            }
            case 2: {
                if (message.equalsIgnoreCase("não")) {
                    crateItemProcess.nextTier();

                    player.sendMessage(new String[]{
                        "",
                        "§aThe item should go to player inventory?",
                        "§7(Reply with 'yes' or 'no' to finalize the process)",
                        ""
                    });
                    return;
                }

                crateItemProcess.getCrateItem().getCommands().add(message);

                player.sendMessage(new String[]{
                    "",
                    "§aDo you need to add a command? (Current: §f" + crateItemProcess.getCrateItem().getCommands().size() + "§a)",
                    "§7(Type 'no' when you want to go to the next process)",
                    ""
                });
                break;
            }
            case 3: {
                if (!message.equalsIgnoreCase("yes") && !message.equalsIgnoreCase("no")) {
                    player.sendMessage("§cReply only with 'yes' or 'no'§c.");
                    return;
                }

                boolean isGiveItem = message.equalsIgnoreCase("yes");
                crateItemProcess.getCrateItem().setGiveItem(isGiveItem);

                Crate crate = crateItemProcess.getCrate();
                crate.addItem(crateItemProcess.getCrateItem());

                crateHelper.saveItemStack(crate.getName(), crateItemProcess.getCrateItem());
                crateHelper.openContentsInventory(crate, player);

                processItemCrateCache.removeElement(player.getUniqueId());
                player.sendMessage("§aYay! The item was added to the crate.");
                break;
            }
        }
    }

}
