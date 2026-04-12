package com.mojang.realmsclient.client.worldupload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;

public class RealmsUploadWorldPacker {
   private RealmsUploadWorldPacker(final Path directoryToPack, final BooleanSupplier isCanceled) {
   }

   public static File pack(final Path directoryToPack, final BooleanSupplier isCanceled) throws IOException {
      throw new IOException("Realms world uploads are not supported in the browser port");
   }
}