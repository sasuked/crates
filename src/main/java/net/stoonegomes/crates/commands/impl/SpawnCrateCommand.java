package net.stoonegomes.crates.commands.impl;

import net.stoonegomes.crates.StrixCrates;
import net.stoonegomes.crates.cache.impl.CrateCache;
import net.stoonegomes.crates.commands.Command;
import net.stoonegomes.crates.entity.Crate;
import net.stoonegomes.crates.helper.CrateHelper;
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

    private final CrateCache crateCache = CrateCache.getInstance();
    private final CrateHelper crateHelper = CrateHelper.getInstance();

    public SpawnCrateCommand() {
        super("spawncrate", "definircrate");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (!player.hasPermission("crates.admin")) {
            sender.sendMessage("§cNo permission.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cTo spawn a crate use §f/spawncrate <id>§c.");
            return true;
        }

        Crate crate = crateCache.getElement(args[0].toLowerCase());
        if (crate == null) {
            player.sendMessage("§cThe typed crate wasn't found.");
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

        crateHelper.spawnHologram(block.getLocation(), crate);
        crateHelper.setCrateLocation(crate.getName(), block.getLocation());
        crateCache.putElement(crate.getName().toLowerCase(), crate);

        player.sendMessage("§aYou spawned the crate §f" + crate.getName().toLowerCase() + "§a successfuly.");
        return false;
    }

    public BlockFace yawToFace(float yaw) {
        final BlockFace[] axis = {BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST};

        return axis[Math.round(yaw / 90f) & 0x3];
    }

}
