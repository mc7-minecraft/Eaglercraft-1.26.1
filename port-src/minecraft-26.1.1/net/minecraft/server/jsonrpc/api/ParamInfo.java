package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ParamInfo<Param>(String name, Schema<Param> schema, boolean required) {
   public ParamInfo(final String name, final Schema<Param> schema) {
      this(name, schema, true);
   }

   public static <Param> MapCodec<ParamInfo<Param>> typedCodec() {
      return RecordCodecBuilder.mapCodec(
         i -> i.group(
                  Codec.STRING.fieldOf("name").forGetter((ParamInfo<Param> info) -> info.name()),
                  Schema.<Param>typedCodec().fieldOf("schema").forGetter((ParamInfo<Param> info) -> info.schema()),
                  Codec.BOOL.fieldOf("required").forGetter((ParamInfo<Param> info) -> info.required())
               )
               .apply(i, ParamInfo::new)
      );
   }
}
