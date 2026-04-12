package net.minecraft.network.protocol.game;

import java.util.HashMap;
import java.util.Map;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.gamerules.GameRule;

public record ClientboundGameRuleValuesPacket(Map<ResourceKey<GameRule<?>>, String> values) implements Packet<ClientGamePacketListener> {
   private static final StreamCodec<ByteBuf, HashMap<ResourceKey<GameRule<?>>, String>> VALUES_STREAM_CODEC = ByteBufCodecs.map(
      ClientboundGameRuleValuesPacket::newValuesMap, ResourceKey.<GameRule<?>>streamCodec(Registries.GAME_RULE), ByteBufCodecs.STRING_UTF8
   );
   public static final StreamCodec<ByteBuf, ClientboundGameRuleValuesPacket> STREAM_CODEC = new StreamCodec<ByteBuf, ClientboundGameRuleValuesPacket>() {
      @Override
      public ClientboundGameRuleValuesPacket decode(final ByteBuf input) {
         return new ClientboundGameRuleValuesPacket(VALUES_STREAM_CODEC.decode(input));
      }

      @Override
      public void encode(final ByteBuf output, final ClientboundGameRuleValuesPacket value) {
         VALUES_STREAM_CODEC.encode(output, new HashMap<>(value.values()));
      }
   };

   @Override
   public PacketType<ClientboundGameRuleValuesPacket> type() {
      return GamePacketTypes.CLIENTBOUND_GAME_RULE_VALUES;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleGameRuleValues(this);
   }

   private static HashMap<ResourceKey<GameRule<?>>, String> newValuesMap(final int size) {
      return new HashMap<>(size);
   }
}
