package net.minecraft.world.attribute.modifier;

import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.LerpFunction;

public interface FloatModifier<Argument> extends AttributeModifier<Float, Argument> {
   FloatModifier<FloatWithAlpha> ALPHA_BLEND = new FloatModifier<FloatWithAlpha>() {
      public Float apply(final Float subject, final FloatWithAlpha argument) {
         return Mth.lerp(argument.alpha(), subject, argument.value());
      }

      @Override
      public Codec<FloatWithAlpha> argumentCodec(final EnvironmentAttribute<Float> type) {
         return FloatWithAlpha.CODEC;
      }

      @Override
      public LerpFunction<FloatWithAlpha> argumentKeyframeLerp(final EnvironmentAttribute<Float> type) {
         return (alpha, from, to) -> new FloatWithAlpha(Mth.lerp(alpha, from.value(), to.value()), Mth.lerp(alpha, from.alpha(), to.alpha()));
      }
   };
   FloatModifier<Float> ADD = new FloatModifier.Simple() {
      public Float apply(final Float value, final Float value2) {
         return Float.sum(value, value2);
      }
   };
   FloatModifier<Float> SUBTRACT = new FloatModifier.Simple() {
      public Float apply(final Float value, final Float value2) {
         return value - value2;
      }
   };
   FloatModifier<Float> MULTIPLY = new FloatModifier.Simple() {
      public Float apply(final Float value, final Float value2) {
         return value * value2;
      }
   };
   FloatModifier<Float> MINIMUM = new FloatModifier.Simple() {
      public Float apply(final Float value, final Float value2) {
         return Math.min(value, value2);
      }
   };
   FloatModifier<Float> MAXIMUM = new FloatModifier.Simple() {
      public Float apply(final Float value, final Float value2) {
         return Math.max(value, value2);
      }
   };

   @FunctionalInterface
   public interface Simple extends FloatModifier<Float> {
      @Override
      default Codec<Float> argumentCodec(final EnvironmentAttribute<Float> type) {
         return Codec.FLOAT;
      }

      @Override
      default LerpFunction<Float> argumentKeyframeLerp(final EnvironmentAttribute<Float> type) {
         return LerpFunction.ofFloat();
      }
   }
}
