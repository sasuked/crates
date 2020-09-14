package net.stoonegomes.crates.entity;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class Crate {

    private Location location;
    private String name;
    private List<CrateItem> items;
    private Material blockType;
    private Hologram blockHologram;

}
