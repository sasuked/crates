package net.stoonegomes.crates.commands.impl;

import net.stoonegomes.crates.StrixCrates;
import net.stoonegomes.crates.commands.Command;
import net.stoonegomes.crates.entity.Crate;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.Chest;
import org.bukkit.material.EnderChest;

public class SpawnCrateCommand extends Command {

    private final StrixCrates strixCrates = StrixCrates.getInstance();

    public SpawnCrateCommand() {
        super("spawncrate", "definircrate");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (!player.hasPermission(strixCrates.getConfig().getString("admin_permission"))) {
            String message = strixCrates.getConfig().getString("messages.no_perm").replace("&", "§");
            sender.sendMessage(message);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cPara definir o local de uma crate use §f/definircrate <crate>§c no local desejado.");
            return true;
        }

        Crate crate = strixCrates.getCrateCache().getElement(args[0].toLowerCase());
        if (crate == null) {
            player.sendMessage("§cA crate digitada não foi encontrada.");
            return true;
        }

        Location location = player.getLocation();

        Block block = location.getBlock();
        block.setType(crate.getBlockType());

        BlockState blockState = block.getState();
        switch (crate.getBlockType()) {
            case ENDER_CHEST: {
                EnderChest enderChest = new EnderChest(yawToFace(location.getYaw()));
                blockState.setData(enderChest);
                break;
            }
            case CHEST: {
                Chest chest = new Chest(yawToFace(location.getYaw()));
                blockState.setData(chest);
                break;
            }
        }
        blockState.update(true);

        crate.setLocation(block.getLocation());

        strixCrates.getCrateHelper().spawnHologram(block.getLocation(), crate);
        strixCrates.getCrateHelper().setCrateLocation(crate.getName(), block.getLocation());
        strixCrates.getCrateCache().putElement(crate.getName().toLowerCase(), crate);

        player.sendMessage("§aVocê spawnou a crate §f" + crate.getName() + "§a com sucesso.");
        return false;
    }

    public BlockFace yawToFace(float yaw) {
        final BlockFace[] axis = {BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST};

        return axis[Math.round(yaw / 90f) & 0x3];
    }

}
