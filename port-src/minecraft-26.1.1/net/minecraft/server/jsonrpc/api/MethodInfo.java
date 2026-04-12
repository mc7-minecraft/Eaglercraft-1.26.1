package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.datafixers.util.Function3;
import java.util.List;
import java.util.Optional;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public record MethodInfo<Params, Result>(String description, Optional<ParamInfo<Params>> params, Optional<ResultInfo<Result>> result) {
   public MethodInfo(final String description, @Nullable final ParamInfo<Params> paramInfo, @Nullable final ResultInfo<Result> resultInfo) {
      this(description, Optional.ofNullable(paramInfo), Optional.ofNullable(resultInfo));
   }

   private static <Params> Optional<ParamInfo<Params>> toOptional(final List<ParamInfo<Params>> list) {
      return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
   }

   private static <Params> List<ParamInfo<Params>> toList(final Optional<ParamInfo<Params>> opt) {
      return opt.isPresent() ? List.of(opt.get()) : List.of();
   }

   private static <Params> Codec<Optional<ParamInfo<Params>>> paramsTypedCodec() {
      Codec<List<ParamInfo<Params>>> codec = ParamInfo.<Params>typedCodec().codec().listOf();
      return codec.xmap(MethodInfo::toOptional, MethodInfo::toList);
   }

   private static <Params, Result> MethodInfo<Params, Result> create(final String description, final Optional<ParamInfo<Params>> params, final Optional<ResultInfo<Result>> result) {
      return new MethodInfo<>(description, params, result);
   }

   private static <Params, Result> MapCodec<MethodInfo<Params, Result>> typedCodec() {
      return RecordCodecBuilder.mapCodec(
         i -> i.group(
                  Codec.STRING.fieldOf("description").forGetter((MethodInfo<Params, Result> info) -> info.description()),
                  paramsTypedCodec().fieldOf("params").forGetter((MethodInfo<Params, Result> info) -> (Optional)info.params()),
                  ResultInfo.<Result>typedCodec().optionalFieldOf("result").forGetter((MethodInfo<Params, Result> info) -> info.result())
               )
               .apply(i, (Function3<String, Optional<ParamInfo<Params>>, Optional<ResultInfo<Result>>, MethodInfo<Params, Result>>)MethodInfo::create)
      );
   }

   public MethodInfo.Named<Params, Result> named(final Identifier name) {
      return new MethodInfo.Named<>(name, this);
   }

   public static record Named<Params, Result>(Identifier name, MethodInfo<Params, Result> contents) {
      public static final Codec<MethodInfo.Named<?, ?>> CODEC = (Codec<MethodInfo.Named<?, ?>>)(Codec<?>)typedCodec();

      public static <Params, Result> Codec<MethodInfo.Named<Params, Result>> typedCodec() {
         return RecordCodecBuilder.create(
            i -> i.group(
                     Identifier.CODEC.fieldOf("name").forGetter((MethodInfo.Named<Params, Result> named) -> named.name()),
                     MethodInfo.<Params, Result>typedCodec().forGetter((MethodInfo.Named<Params, Result> named) -> named.contents())
                  )
                  .apply(i, (name, contents) -> new MethodInfo.Named<>(name, contents))
         );
      }
   }
}
