package com.github.gtexpert.advancedbackupspatch.mixin;

import java.io.File;

import computer.heather.advancedbackups.core.backups.BackupWrapper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BackupWrapper.class, remap = false)
public abstract class BackupWrapperMixin {

    /**
     * Prevent NPE when File.listFiles() returns null in deleteDirectoryContents.
     * This happens when the path is not a directory or an I/O error occurs.
     */
    @Redirect(method = "deleteDirectoryContents",
            at = @At(value = "INVOKE",
                    target = "Ljava/io/File;listFiles()[Ljava/io/File;"))
    private static File[] safeListFiles(File directory) {
        File[] result = directory.listFiles();
        return result != null ? result : new File[0];
    }
}
