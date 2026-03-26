package com.github.gtexpert.advancedbackupspatch.mixin;

import java.io.File;

import computer.heather.advancedbackups.core.backups.ThreadedBackup;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ThreadedBackup.class, remap = false)
public abstract class ThreadedBackupMixin {

    /**
     * Prevent NPE when File.list() returns null in performRename.
     * This happens when the directory does not exist or an I/O error occurs.
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
     */
    @Redirect(method = "performDelete",
            at = @At(value = "INVOKE",
                    target = "Ljava/io/File;list()[Ljava/lang/String;"))
    private String[] safeListInDelete(File file) {
        String[] result = file.list();
        return result != null ? result : new String[0];
    }
}
