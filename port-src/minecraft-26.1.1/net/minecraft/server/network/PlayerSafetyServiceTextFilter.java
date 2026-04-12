package net.minecraft.server.network;

public abstract class PlayerSafetyServiceTextFilter extends ServerTextFilter {
   public static ServerTextFilter createTextFilterFromConfig(final String textFilteringConfig) {
      LOGGER.warn("Player safety text filtering is not supported in the browser port");
      return null;
   }
}