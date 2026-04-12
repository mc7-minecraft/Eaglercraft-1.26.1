package net.minecraft.world.entity.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;

public interface PriorityProvider<Context, Condition extends PriorityProvider.SelectorCondition<Context>> {
   List<PriorityProvider.Selector<Context, Condition>> selectors();

   static <C, T> Stream<T> select(final Stream<T> entries, final Function<T, PriorityProvider<C, ?>> extractor, final C context) {
      List<PriorityProvider.UnpackedEntry<C, T>> unpackedEntries = new ArrayList<>();
      entries.forEach(entry -> {
         PriorityProvider<C, ?> provider = extractor.apply(entry);

         for (PriorityProvider.Selector<C, ?> selector : provider.selectors()) {
            unpackedEntries.add(new PriorityProvider.UnpackedEntry<>(entry, selector.priority(), selectorOrDefault(selector)));
         }
      });
      unpackedEntries.sort(PriorityProvider.UnpackedEntry.HIGHEST_PRIORITY_FIRST);
      Iterator<PriorityProvider.UnpackedEntry<C, T>> iterator = unpackedEntries.iterator();
      int highestMatchedPriority = Integer.MIN_VALUE;

      while (iterator.hasNext()) {
         PriorityProvider.UnpackedEntry<C, T> entry = iterator.next();
         if (entry.priority < highestMatchedPriority) {
            iterator.remove();
         } else if (entry.condition.test(context)) {
            highestMatchedPriority = entry.priority;
         } else {
            iterator.remove();
         }
      }

      return unpackedEntries.stream().map(PriorityProvider.UnpackedEntry::entry);
   }

   static <C, T> Optional<T> pick(
      final Stream<T> entries, final Function<T, PriorityProvider<C, ?>> extractor, final RandomSource randomSource, final C context
   ) {
      List<T> selected = select(entries, extractor, context).toList();
      return Util.getRandomSafe(selected, randomSource);
   }

   static <Context, Condition extends PriorityProvider.SelectorCondition<Context>> List<PriorityProvider.Selector<Context, Condition>> single(
      final Condition check, final int priority
   ) {
      return List.of(new PriorityProvider.Selector<>(check, priority));
   }

   static <Context, Condition extends PriorityProvider.SelectorCondition<Context>> List<PriorityProvider.Selector<Context, Condition>> alwaysTrue(
      final int priority
   ) {
      return List.of(new PriorityProvider.Selector<>(Optional.empty(), priority));
   }

   public static record Selector<Context, Condition extends PriorityProvider.SelectorCondition<Context>>(Optional<Condition> condition, int priority) {
      public Selector(final Condition condition, final int priority) {
         this(Optional.of(condition), priority);
      }

      public Selector(final int priority) {
         this(Optional.empty(), priority);
      }

      public static <Context, Condition extends PriorityProvider.SelectorCondition<Context>> Codec<PriorityProvider.Selector<Context, Condition>> codec(
         final Codec<Condition> conditionCodec
      ) {
         return RecordCodecBuilder.<PriorityProvider.Selector<Context, Condition>>create(i -> i.group(
               conditionCodec.optionalFieldOf("condition").forGetter((PriorityProvider.Selector<Context, Condition> selector) -> selector.condition()),
               Codec.INT.fieldOf("priority").forGetter((PriorityProvider.Selector<Context, Condition> selector) -> selector.priority())
            ).apply(i, (Optional<Condition> condition, Integer priority) -> new PriorityProvider.Selector<>(condition, priority))
         );
      }
   }

   @FunctionalInterface
   public interface SelectorCondition<C> extends Predicate<C> {
      static <C> PriorityProvider.SelectorCondition<C> alwaysTrue() {
         return context -> true;
      }
   }

   private static <C> PriorityProvider.SelectorCondition<C> selectorOrDefault(final PriorityProvider.Selector<C, ?> selector) {
      Optional<? extends PriorityProvider.SelectorCondition<C>> condition = selector.condition();
      if (condition.isPresent()) {
         return condition.get();
      }

      return PriorityProvider.SelectorCondition.alwaysTrue();
   }

   private static <Context, Condition extends PriorityProvider.SelectorCondition<Context>> PriorityProvider.Selector<Context, Condition> createSelector(
      final Optional<Condition> condition, final int priority
   ) {
      return new PriorityProvider.Selector<>(condition, priority);
   }

   public static record UnpackedEntry<C, T>(T entry, int priority, PriorityProvider.SelectorCondition<C> condition) {
      public static final Comparator<PriorityProvider.UnpackedEntry<?, ?>> HIGHEST_PRIORITY_FIRST = Comparator
         .comparingInt((PriorityProvider.UnpackedEntry<?, ?> entry) -> entry.priority())
         .reversed();
   }
}
