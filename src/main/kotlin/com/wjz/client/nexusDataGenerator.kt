package com.wjz.client

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

class nexusDataGenerator : DataGeneratorEntrypoint {

    override fun onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        val pack = fabricDataGenerator.createPack();
    }
}
