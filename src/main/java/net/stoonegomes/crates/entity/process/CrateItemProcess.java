package net.stoonegomes.crates.entity.process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.stoonegomes.crates.entity.Crate;
import net.stoonegomes.crates.entity.CrateItem;

@Getter
@AllArgsConstructor
@Builder
public class CrateItemProcess {

    private int tier;

    private CrateItem crateItem;
    private Crate crate;

    public void nextTier() {
        tier++;
    }

}
