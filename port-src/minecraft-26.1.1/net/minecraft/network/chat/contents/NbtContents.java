package net.minecraft.network.chat.contents;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TextComponentTagVisitor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ResolutionContext;
import net.minecraft.network.chat.contents.data.DataSource;
import net.minecraft.network.chat.contents.data.DataSources;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.CompilableString;
import org.slf4j.Logger;

public record NbtContents(
   CompilableString<NbtPathArgument.NbtPath> nbtPath, boolean interpreting, boolean plain, Optional<Component> separator, DataSource dataSource
) implements ComponentContents {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<CompilableString<NbtPathArgument.NbtPath>> NBT_PATH_CODEC = CompilableString.codec(
      new CompilableString.CommandParserHelper<NbtPathArgument.NbtPath>() {
         protected NbtPathArgument.NbtPath parse(final StringReader reader) throws CommandSyntaxException {
            return new NbtPathArgument().parse(reader);
         }

         @Override
         protected String errorMessage(final String original, final CommandSyntaxException exception) {
            return "Invalid NBT path: " + original + ": " + exception.getMessage();
         }
      }
   );
   public static final MapCodec<NbtContents> MAP_CODEC = RecordCodecBuilder.<NbtContents>mapCodec(
         (RecordCodecBuilder.Instance<NbtContents> instance) -> instance
            .group(
               NBT_PATH_CODEC.fieldOf("nbt").forGetter(contents -> contents.nbtPath),
               Codec.BOOL.lenientOptionalFieldOf("interpret", false).forGetter(contents -> contents.interpreting),
               Codec.BOOL.lenientOptionalFieldOf("plain", false).forGetter(contents -> contents.plain),
               ComponentSerialization.CODEC.lenientOptionalFieldOf("separator").forGetter(contents -> contents.separator),
               DataSources.CODEC.forGetter(contents -> contents.dataSource)
            )
            .apply(instance, NbtContents::new)
      )
      .validate((NbtContents contents) -> contents.interpreting && contents.plain ? DataResult.error(() -> "'interpret' and 'plain' flags can't be both on") : DataResult.success(contents));

   @Override
   public MutableComponent resolve(final ResolutionContext context, final int recursionDepth) throws CommandSyntaxException {
      CommandSourceStack source = context.source();
      if (source == null) {
         return Component.empty();
      } else {
         Stream<Tag> elements = this.dataSource.getData(source).flatMap(t -> {
            try {
               return this.nbtPath.compiled().get(t).stream();
            } catch (CommandSyntaxException var3x) {
               return Stream.empty();
            }
         });
         Component resolvedSeparator = (Component)DataFixUtils.orElse(
            ComponentUtils.resolve(context, this.separator, recursionDepth), ComponentUtils.DEFAULT_NO_STYLE_SEPARATOR
         );
         if (this.interpreting) {
            RegistryOps<Tag> registryOps = source.registryAccess().createSerializationContext(NbtOps.INSTANCE);
            return elements.flatMap(tag -> {
               try {
                  Component component = (Component)ComponentSerialization.CODEC.parse(registryOps, tag).getOrThrow();
                  return Stream.of(ComponentUtils.resolve(context, component, recursionDepth));
               } catch (Exception var5x) {
                  LOGGER.warn("Failed to parse component: {}", tag, var5x);
                  return Stream.of();
               }
            }).reduce((left, right) -> left.append(resolvedSeparator).append(right)).orElseGet(Component::empty);
         } else {
            return elements.map(
                  tag -> {
                     TextComponentTagVisitor visitor = new TextComponentTagVisitor(
                        "", this.plain ? TextComponentTagVisitor.PlainStyling.INSTANCE : TextComponentTagVisitor.RichStyling.INSTANCE
                     );
                     return (MutableComponent)visitor.visit(tag);
                  }
               )
               .reduce((left, right) -> left.append(resolvedSeparator).append(right))
               .orElseGet(Component::empty);
         }
      }
   }

   @Override
   public MapCodec<NbtContents> codec() {
      return MAP_CODEC;
   }
}
