package com.github.gtexpert.advancedbackupspatch.mixin;

import java.io.File;

import computer.heather.advancedbackups.core.backups.ThreadedBackup;
import computer.heather.advancedbackups.core.backups.gson.BackupManifest;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ThreadedBackup.class, remap = false)
public abstract class ThreadedBackupMixin {

    /**
     * Prevent NPE when File.list() returns null in performRename.
     * This happens when the directory does not exist or an I/O error occurs.
     *
     * @see <a href="https://github.com/HeatherComputer/AdvancedBackups/issues/103">Issue #103</a>
     */
    @Redirect(method = "performRename",
            at = @At(value = "INVOKE",
                    target = "Ljava/io/File;list()[Ljava/lang/String;"))
    private String[] safeListInRename(File file) {
        String[] result = file.list();
        return result != null ? result : new String[0];
    }

    /**
     * Prevent NPE when File.list() returns null in performDelete.
     * This happens when the directory does not exist or an I/O error occurs.
     *
     * @see <a href="https://github.com/HeatherComputer/AdvancedBackups/issues/103">Issue #103</a>
     */
    @Redirect(method = "performDelete",
            at = @At(value = "INVOKE",
                    target = "Ljava/io/File;list()[Ljava/lang/String;"))
    private String[] safeListInDelete(File file) {
        String[] result = file.list();
        return result != null ? result : new String[0];
    }

    /**
     * Prevent NPE when parsing a corrupted backup manifest in makeDifferentialOrIncrementalBackup.
     * <p>
     * Same issue as in {@code BackupWrapper.checkStartupBackups()} — the JAR v3.7.1
     * only catches {@link JsonParseException}, but {@code gson.fromJson()} can return null
     * or a manifest with null fields, causing a {@link NullPointerException}.
     *
     * @see <a href="https://github.com/HeatherComputer/AdvancedBackups/issues/110">Issue #110</a>
     */
    @Redirect(method = "makeDifferentialOrIncrementalBackup",
            at = @At(value = "INVOKE",
                    target = "Lcom/google/gson/Gson;fromJson(Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;"))
    private Object safeFromJson(Gson gson, String json, Class<?> classOfT) {
        Object result = gson.fromJson(json, classOfT);
        if (result == null) {
            throw new JsonParseException("Backup manifest parsed as null");
        }
        BackupManifest manifest = (BackupManifest) result;
        if (manifest.differential == null || manifest.incremental == null) {
            throw new JsonParseException("Backup manifest fields are null");
        }
        return result;
    }
}
