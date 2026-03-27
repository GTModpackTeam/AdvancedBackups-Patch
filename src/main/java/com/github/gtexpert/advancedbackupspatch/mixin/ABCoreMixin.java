package com.github.gtexpert.advancedbackupspatch.mixin;

import computer.heather.advancedbackups.core.ABCore;
import computer.heather.advancedbackups.core.backups.gson.BackupManifest;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ABCore.class, remap = false)
public abstract class ABCoreMixin {

    /**
     * Prevent NPE when parsing a corrupted backup manifest in setActivity.
     * <p>
     * The JAR v3.7.1 only catches {@link JsonParseException}, but {@code gson.fromJson()}
     * can return null or a manifest with null fields for certain malformed JSON,
     * causing a {@link NullPointerException} when accessing {@code manifest.general.activity}.
     * <p>
     * This redirect converts the null case into a {@link JsonParseException},
     * which is already caught and handled by the existing recovery logic.
     *
     * @see <a href="https://github.com/HeatherComputer/AdvancedBackups/issues/110">Issue #110</a>
     */
    @Redirect(method = "setActivity",
            at = @At(value = "INVOKE",
                    target = "Lcom/google/gson/Gson;fromJson(Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;"))
    private static Object safeFromJson(Gson gson, String json, Class<?> classOfT) {
        Object result = gson.fromJson(json, classOfT);
        if (result == null) {
            throw new JsonParseException("Backup manifest parsed as null");
        }
        BackupManifest manifest = (BackupManifest) result;
        if (manifest.general == null) {
            throw new JsonParseException("Backup manifest 'general' field is null");
        }
        return result;
    }
}