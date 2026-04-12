package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class RenamedCoralFix {
   public static final Map<String, String> RENAMED_IDS;

   static {
      ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
      builder.put("minecraft:blue_coral", "minecraft:tube_coral_block");
      builder.put("minecraft:pink_coral", "minecraft:brain_coral_block");
      builder.put("minecraft:purple_coral", "minecraft:bubble_coral_block");
      builder.put("minecraft:red_coral", "minecraft:fire_coral_block");
      builder.put("minecraft:yellow_coral", "minecraft:horn_coral_block");
      builder.put("minecraft:blue_coral_plant", "minecraft:tube_coral");
      builder.put("minecraft:pink_coral_plant", "minecraft:brain_coral");
      builder.put("minecraft:purple_coral_plant", "minecraft:bubble_coral");
      builder.put("minecraft:red_coral_plant", "minecraft:fire_coral");
      builder.put("minecraft:yellow_coral_plant", "minecraft:horn_coral");
      builder.put("minecraft:blue_coral_fan", "minecraft:tube_coral_fan");
      builder.put("minecraft:pink_coral_fan", "minecraft:brain_coral_fan");
      builder.put("minecraft:purple_coral_fan", "minecraft:bubble_coral_fan");
      builder.put("minecraft:red_coral_fan", "minecraft:fire_coral_fan");
      builder.put("minecraft:yellow_coral_fan", "minecraft:horn_coral_fan");
      builder.put("minecraft:blue_dead_coral", "minecraft:dead_tube_coral");
      builder.put("minecraft:pink_dead_coral", "minecraft:dead_brain_coral");
      builder.put("minecraft:purple_dead_coral", "minecraft:dead_bubble_coral");
      builder.put("minecraft:red_dead_coral", "minecraft:dead_fire_coral");
      builder.put("minecraft:yellow_dead_coral", "minecraft:dead_horn_coral");
      RENAMED_IDS = builder.build();
   }
}
