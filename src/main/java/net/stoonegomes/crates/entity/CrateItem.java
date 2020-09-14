package net.stoonegomes.crates.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

@Builder
@AllArgsConstructor
@Data
public class CrateItem {

    private ItemStack itemStack;
    private int percent;
    private Set<String> commands;
    private boolean giveItem;

}
