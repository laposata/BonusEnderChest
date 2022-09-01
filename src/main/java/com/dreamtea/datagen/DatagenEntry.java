package com.dreamtea.datagen;

import com.dreamtea.datagen.loottable.EnderBonusChestLootTable;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class DatagenEntry implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(new EnderBonusChestLootTable(fabricDataGenerator));
    }
}
