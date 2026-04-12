package net.minecraft.world.attribute.modifier;

import com.mojang.serialization.Codec;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.LerpFunction;

public interface IntegerModifier<Argument> extends AttributeModifier<Integer, Argument> {
   IntegerModifier<Integer> ADD = new IntegerModifier.Simple() {
      public Integer apply(final Integer integer, final Integer integer2) {
         return Integer.sum(integer, integer2);
      }
   };
   IntegerModifier<Integer> SUBTRACT = new IntegerModifier.Simple() {
      public Integer apply(final Integer integer, final Integer integer2) {
         return integer - integer2;
      }
   };
   IntegerModifier<Integer> MULTIPLY = new IntegerModifier.Simple() {
      public Integer apply(final Integer integer, final Integer integer2) {
         return integer * integer2;
      }
   };
   IntegerModifier<Integer> MINIMUM = new IntegerModifier.Simple() {
      public Integer apply(final Integer integer, final Integer integer2) {
         return Math.min(integer, integer2);
      }
   };
   IntegerModifier<Integer> MAXIMUM = new IntegerModifier.Simple() {
      public Integer apply(final Integer integer, final Integer integer2) {
         return Math.max(integer, integer2);
      }
   };

   Integer apply(Integer integer, Argument argument);

   @FunctionalInterface
   public interface Simple extends IntegerModifier<Integer> {
      @Override
      default Codec<Integer> argumentCodec(final EnvironmentAttribute<Integer> type) {
         return Codec.INT;
      }

      @Override
      default LerpFunction<Integer> argumentKeyframeLerp(final EnvironmentAttribute<Integer> type) {
         return LerpFunction.ofInteger();
      }
   }
}
