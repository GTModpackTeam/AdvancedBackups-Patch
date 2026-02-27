package com.github.gtexpert.advancedbackupspatch;

import java.util.Collections;
import java.util.List;

import net.minecraftforge.fml.common.Mod;

import zone.rong.mixinbooter.ILateMixinLoader;

@Mod(modid = Reference.MOD_ID,
        name = Reference.MOD_NAME,
        acceptedMinecraftVersions = "[1.12.2]",
        version = Reference.VERSION,
//        updateJSON = "https://forge.curseupdate.com/851103/gtexpert",
        dependencies = "required-after:mixinbooter" + "@[10.6,);" +
                "after:advancedbackups;")
public class AdvancedBackupsPatch implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.advancedbackupspatch.json");
    }
}
