package com.github.gtexpert.advancedbackupspatch.mixin;

import computer.heather.advancedbackups.core.ABCore;
import computer.heather.advancedbackups.core.config.ClientConfigManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientConfigManager.class, remap = false)
public abstract class ClientConfigManagerMixin {

    @Unique
    private static final Logger advancedbackupspatch$LOGGER = LogManager.getLogger("advancedbackups");

    /**
     * Ensure ABCore loggers are non-null before ClientConfigManager uses them.
     * <p>
     * In singleplayer, PacketToastTest is handled on the Netty thread,
     * which may not see logger writes from the IntegratedServer thread
     * due to the Java Memory Model (non-volatile fields).
     * This provides a fallback to prevent NPE.
     *
     * @see <a href="https://github.com/HeatherComputer/AdvancedBackups/issues/87">Issue #87</a>
     */
    @Inject(method = "loadOrCreateConfig", at = @At("HEAD"))
    private static void ensureLoggers(CallbackInfo ci) {
        if (ABCore.infoLogger == null) {
            ABCore.infoLogger = advancedbackupspatch$LOGGER::info;
        }
        if (ABCore.warningLogger == null) {
            ABCore.warningLogger = advancedbackupspatch$LOGGER::warn;
        }
        if (ABCore.errorLogger == null) {
            ABCore.errorLogger = advancedbackupspatch$LOGGER::error;
        }
    }
}
