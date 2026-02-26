package com.github.gtexpert.advancedbackupspatch.mixin;

import java.util.function.Consumer;

import computer.heather.advancedbackups.AdvancedBackups;
import computer.heather.advancedbackups.core.ABCore;
import computer.heather.advancedbackups.network.NetworkHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AdvancedBackups.class, remap = false)
public abstract class AdvancedBackupsMixin {

    @Shadow
    public static Consumer<String> infoLogger;

    @Shadow
    public static Consumer<String> warningLogger;

    @Shadow
    public static Consumer<String> errorLogger;

    /**
     * Disable EVENT_BUS.register(this) in the constructor.
     * The mod container is not yet established at construction time, so defer to preInit.
     */
    @Redirect(method = "<init>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraftforge/fml/common/eventhandler/EventBus;register(Ljava/lang/Object;)V"))
    private void redirectEventBusRegister(EventBus bus, Object target) {
        // no-op: deferred to preInit
    }

    /**
     * Disable NetworkHandler.registerMessages() in the constructor.
     * Defer network channel registration until the mod container is established.
     */
    @Redirect(method = "<init>",
            at = @At(value = "INVOKE",
                    target = "Lcomputer/heather/advancedbackups/network/NetworkHandler;registerMessages()V"))
    private void redirectNetworkRegister() {
        // no-op: deferred to preInit
    }

    /**
     * Register the event bus and network messages at the beginning of preInit,
     * where the mod container is properly established.
     */
    @Inject(method = "preInit", at = @At("HEAD"))
    private void onPreInit(FMLPreInitializationEvent event, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.register(this);
        NetworkHandler.registerMessages();
    }

    /**
     * Set ABCore loggers after preInit sets them on AdvancedBackups.
     * Without this, ABCore.infoLogger is null on the client side (only set in onServerStarting),
     * causing NPE in ClientConfigManager when receiving PacketToastTest.
     */
    @Inject(method = "preInit", at = @At("TAIL"))
    private void onPreInitTail(FMLPreInitializationEvent event, CallbackInfo ci) {
        ABCore.infoLogger = infoLogger;
        ABCore.warningLogger = warningLogger;
        ABCore.errorLogger = errorLogger;
    }
}
