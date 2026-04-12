package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Map;

public class JigsawRotationFix extends AbstractBlockPropertyFix {
   private static final Map<String, String> RENAMES;

   static {
      ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
      builder.put("down", "down_south");
      builder.put("up", "up_north");
      builder.put("north", "north_up");
      builder.put("south", "south_up");
      builder.put("west", "west_up");
      builder.put("east", "east_up");
      RENAMES = builder.build();
   }

   public JigsawRotationFix(final Schema outputSchema) {
      super(outputSchema, "jigsaw_rotation_fix");
   }

   @Override
   protected boolean shouldFix(final String blockId) {
      return blockId.equals("minecraft:jigsaw");
   }

   @Override
   protected <T> Dynamic<T> fixProperties(final String blockId, final Dynamic<T> properties) {
      String facing = properties.get("facing").asString("north");
      return properties.remove("facing").set("orientation", properties.createString(RENAMES.getOrDefault(facing, facing)));
   }
}
