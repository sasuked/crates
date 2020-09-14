package net.stoonegomes.crates.cache.impl;

import net.stoonegomes.crates.cache.Cache;
import net.stoonegomes.crates.entity.process.CrateItemProcess;

import java.util.UUID;

public class ProcessItemCrateCache extends Cache<UUID, CrateItemProcess> {

    private static ProcessItemCrateCache instance;

    public static ProcessItemCrateCache getInstance() {
        if (instance == null) instance = new ProcessItemCrateCache();
        return instance;
    }

}
