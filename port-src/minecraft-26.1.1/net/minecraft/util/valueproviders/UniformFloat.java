package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public record UniformFloat(float min, float max) implements FloatProvider {
   public static final MapCodec<UniformFloat> MAP_CODEC = RecordCodecBuilder.<UniformFloat>mapCodec(
      i -> i.group(
         Codec.FLOAT.fieldOf("min_inclusive").forGetter((UniformFloat value) -> value.min()),
         Codec.FLOAT.fieldOf("max_exclusive").forGetter((UniformFloat value) -> value.max())
         )
         .apply(i, UniformFloat::new)
   )
   .validate(
      (UniformFloat value) -> value.max <= value.min
         ? DataResult.<UniformFloat>error(() -> "Max must be larger than min, min: " + value.min + ", max: " + value.max)
         : DataResult.success(value)
   );

   public static UniformFloat of(final float min, final float max) {
      if (max <= min) {
         throw new IllegalArgumentException("Max must exceed min");
      } else {
         return new UniformFloat(min, max);
      }
   }

   @Override
   public float sample(final RandomSource random) {
      return Mth.randomBetween(random, this.min, this.max);
   }

   @Override
   public MapCodec<UniformFloat> codec() {
      return MAP_CODEC;
   }

   @Override
   public String toString() {
      return "[" + this.min + "-" + this.max + "]";
   }
}
