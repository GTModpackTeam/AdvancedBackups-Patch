package com.github.gtexpert.advancedbackupspatch;

import java.util.Collections;
import java.util.List;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import zone.rong.mixinbooter.ILateMixinLoader;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION)
public class AdvancedBackupsPatch implements ILateMixinLoader {

    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME);

    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.advancedbackupspatch.json");
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Hello From {}!", Reference.MOD_NAME);
    }
}
