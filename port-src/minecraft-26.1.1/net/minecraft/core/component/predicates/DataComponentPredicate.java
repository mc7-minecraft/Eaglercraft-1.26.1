package net.minecraft.core.component.predicates;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface DataComponentPredicate {
   Codec<Map<DataComponentPredicate.Type<?>, DataComponentPredicate>> CODEC = Codec.dispatchedMap(
      DataComponentPredicate.Type.CODEC, DataComponentPredicate.Type::codec
   );
   StreamCodec<RegistryFriendlyByteBuf, DataComponentPredicate.Single<?>> SINGLE_STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, DataComponentPredicate.Single<?>>() {
      @Override
      public DataComponentPredicate.Single<?> decode(final RegistryFriendlyByteBuf input) {
         DataComponentPredicate.Type<?> type = DataComponentPredicate.Type.STREAM_CODEC.decode(input);
         return (DataComponentPredicate.Single<?>)((StreamCodec)type.singleStreamCodec()).decode(input);
      }

      @Override
      public void encode(final RegistryFriendlyByteBuf output, final DataComponentPredicate.Single<?> value) {
         DataComponentPredicate.Type<?> type = value.type();
         DataComponentPredicate.Type.STREAM_CODEC.encode(output, type);
         ((StreamCodec)type.singleStreamCodec()).encode(output, value);
      }
   };
   StreamCodec<RegistryFriendlyByteBuf, List<DataComponentPredicate.Single<?>>> SINGLE_LIST_STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, List<DataComponentPredicate.Single<?>>>() {
      @Override
      public List<DataComponentPredicate.Single<?>> decode(final RegistryFriendlyByteBuf input) {
         int size = ByteBufCodecs.VAR_INT.decode(input);
         List<DataComponentPredicate.Single<?>> singles = new java.util.ArrayList<>(size);

         for (int i = 0; i < size; i++) {
            singles.add(SINGLE_STREAM_CODEC.decode(input));
         }

         return singles;
      }

      @Override
      public void encode(final RegistryFriendlyByteBuf output, final List<DataComponentPredicate.Single<?>> value) {
         ByteBufCodecs.VAR_INT.encode(output, value.size());

         for (DataComponentPredicate.Single<?> single : value) {
            SINGLE_STREAM_CODEC.encode(output, single);
         }
      }
   };
   StreamCodec<RegistryFriendlyByteBuf, Map<DataComponentPredicate.Type<?>, DataComponentPredicate>> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, Map<DataComponentPredicate.Type<?>, DataComponentPredicate>>() {
      @Override
      public Map<DataComponentPredicate.Type<?>, DataComponentPredicate> decode(final RegistryFriendlyByteBuf input) {
         return DataComponentPredicate.toMap(SINGLE_LIST_STREAM_CODEC.decode(input));
      }

      @Override
      public void encode(final RegistryFriendlyByteBuf output, final Map<DataComponentPredicate.Type<?>, DataComponentPredicate> value) {
         SINGLE_LIST_STREAM_CODEC.encode(output, DataComponentPredicate.toSingles(value));
      }
   };

   private static Map<DataComponentPredicate.Type<?>, DataComponentPredicate> toMap(
      final List<DataComponentPredicate.Single<?>> singles
   ) {
      Map<DataComponentPredicate.Type<?>, DataComponentPredicate> result = new java.util.HashMap<>();

      for (DataComponentPredicate.Single<?> single : singles) {
         result.put(single.type(), single.predicate());
      }

      return result;
   }

   private static List<DataComponentPredicate.Single<?>> toSingles(
      final Map<DataComponentPredicate.Type<?>, DataComponentPredicate> map
   ) {
      List<DataComponentPredicate.Single<?>> result = new ArrayList<>(map.size());

      for (Entry<DataComponentPredicate.Type<?>, DataComponentPredicate> entry : map.entrySet()) {
         result.add(DataComponentPredicate.toSingle(entry));
      }

      return result;
   }

   @SuppressWarnings("unchecked")
   private static DataComponentPredicate.Single<?> toSingle(final Entry<DataComponentPredicate.Type<?>, DataComponentPredicate> e) {
      return new DataComponentPredicate.Single<>((DataComponentPredicate.Type<DataComponentPredicate>)e.getKey(), e.getValue());
   }

   static MapCodec<DataComponentPredicate.Single<?>> singleCodec(final String name) {
      return DataComponentPredicate.Type.CODEC.dispatchMap(name, DataComponentPredicate.Single::type, DataComponentPredicate.Type::wrappedCodec);
   }

   boolean matches(DataComponentGetter components);

   public static final class AnyValueType extends DataComponentPredicate.TypeBase<AnyValue> {
      private final AnyValue predicate;

      public AnyValueType(final AnyValue predicate) {
         super(MapCodec.unitCodec(predicate));
         this.predicate = predicate;
      }

      public AnyValue predicate() {
         return this.predicate;
      }

      public DataComponentType<?> componentType() {
         return this.predicate.type();
      }

      public static DataComponentPredicate.AnyValueType create(final DataComponentType<?> componentType) {
         return new DataComponentPredicate.AnyValueType(new AnyValue(componentType));
      }
   }

   public static final class ConcreteType<T extends DataComponentPredicate> extends DataComponentPredicate.TypeBase<T> {
      public ConcreteType(final Codec<T> codec) {
         super(codec);
      }
   }

   public static record Single<T extends DataComponentPredicate>(DataComponentPredicate.Type<T> type, T predicate) {
      @SuppressWarnings("unchecked")
      private static <T extends DataComponentPredicate> MapCodec<DataComponentPredicate.Single<T>> wrapCodec(
         final DataComponentPredicate.Type<T> type, final Codec<T> codec
      ) {
         return RecordCodecBuilder.mapCodec(
            instance -> instance.group(codec.fieldOf("value").forGetter(DataComponentPredicate.Single::predicate)).apply(instance, predicate -> new DataComponentPredicate.Single<>(type, predicate))
         );
      }
   }

   public interface Type<T extends DataComponentPredicate> {
      Codec<DataComponentPredicate.Type<?>> CODEC = Codec.either(
            BuiltInRegistries.DATA_COMPONENT_PREDICATE_TYPE.byNameCodec(), BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec()
         )
         .xmap(DataComponentPredicate.Type::copyOrCreateType, DataComponentPredicate.Type::unpackType);
      StreamCodec<RegistryFriendlyByteBuf, DataComponentPredicate.Type<?>> STREAM_CODEC = ByteBufCodecs.either(
            ByteBufCodecs.registry(Registries.DATA_COMPONENT_PREDICATE_TYPE), ByteBufCodecs.registry(Registries.DATA_COMPONENT_TYPE)
         )
         .map(DataComponentPredicate.Type::copyOrCreateType, DataComponentPredicate.Type::unpackType);

      private static <T extends DataComponentPredicate.Type<?>> Either<T, DataComponentType<?>> unpackType(final T type) {
         return type instanceof DataComponentPredicate.AnyValueType anyCheck ? Either.right(anyCheck.componentType()) : Either.left(type);
      }

      private static DataComponentPredicate.Type<?> copyOrCreateType(final Either<DataComponentPredicate.Type<?>, DataComponentType<?>> concreteTypeOrComponent) {
         return (DataComponentPredicate.Type<?>)concreteTypeOrComponent.map(concrete -> concrete, DataComponentPredicate.AnyValueType::create);
      }

      Codec<T> codec();

      MapCodec<DataComponentPredicate.Single<T>> wrappedCodec();

      StreamCodec<RegistryFriendlyByteBuf, DataComponentPredicate.Single<T>> singleStreamCodec();
   }

   @SuppressWarnings("unchecked")
   public abstract static class TypeBase<T extends DataComponentPredicate> implements DataComponentPredicate.Type<T> {
      private final Codec<T> codec;
      private final MapCodec<DataComponentPredicate.Single<T>> wrappedCodec;
      private final StreamCodec<RegistryFriendlyByteBuf, DataComponentPredicate.Single<T>> singleStreamCodec;

      public TypeBase(final Codec<T> codec) {
         this.codec = codec;
         this.wrappedCodec = DataComponentPredicate.Single.wrapCodec(this, codec);
         this.singleStreamCodec = ByteBufCodecs.<T>fromCodecWithRegistries(codec)
            .map(v -> new DataComponentPredicate.Single<>(this, (T)v), DataComponentPredicate.Single::predicate);
      }

      @Override
      public Codec<T> codec() {
         return this.codec;
      }

      @Override
      public MapCodec<DataComponentPredicate.Single<T>> wrappedCodec() {
         return this.wrappedCodec;
      }

      @Override
      public StreamCodec<RegistryFriendlyByteBuf, DataComponentPredicate.Single<T>> singleStreamCodec() {
         return this.singleStreamCodec;
      }
   }
}
