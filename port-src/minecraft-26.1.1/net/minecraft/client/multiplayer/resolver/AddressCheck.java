package net.minecraft.client.multiplayer.resolver;

public interface AddressCheck {
   boolean isAllowed(ResolvedServerAddress address);

   boolean isAllowed(ServerAddress address);

   static AddressCheck createFromService() {
      return new AddressCheck() {
         @Override
         public boolean isAllowed(final ResolvedServerAddress address) {
            return true;
         }

         @Override
         public boolean isAllowed(final ServerAddress address) {
            return true;
         }
      };
   }
}
