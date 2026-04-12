package net.minecraft.server.network;

public final class PlayerSafetyServiceTextFilter {
   private PlayerSafetyServiceTextFilter() {
   }

   public static ServerTextFilter createTextFilterFromConfig(final String textFilteringConfig) {
      ServerTextFilter.LOGGER.warn("Player safety text filtering is not supported in the browser port");
      return null;
   }
}