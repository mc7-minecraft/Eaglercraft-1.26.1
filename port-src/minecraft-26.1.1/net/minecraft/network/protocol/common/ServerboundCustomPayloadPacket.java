package net.minecraft.network.protocol.common;

import com.google.common.collect.Lists;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.util.Util;

public record ServerboundCustomPayloadPacket(CustomPacketPayload payload) implements Packet<ServerCommonPacketListener> {
   private static final int MAX_PAYLOAD_SIZE = 32767;
   @SuppressWarnings("unchecked")
   private static final StreamCodec<FriendlyByteBuf, CustomPacketPayload> CUSTOM_PAYLOAD_CODEC = (StreamCodec<FriendlyByteBuf, CustomPacketPayload>)CustomPacketPayload.<FriendlyByteBuf>codec(
         id -> DiscardedPayload.codec(id, 32767),
         Util.make(
            Lists.newArrayList(new CustomPacketPayload.TypeAndCodec[]{new CustomPacketPayload.TypeAndCodec<>(BrandPayload.TYPE, BrandPayload.STREAM_CODEC)}),
            types -> {
            }
         )
      );
   public static final StreamCodec<FriendlyByteBuf, ServerboundCustomPayloadPacket> STREAM_CODEC = CUSTOM_PAYLOAD_CODEC
      .map((CustomPacketPayload payload) -> new ServerboundCustomPayloadPacket(payload), ServerboundCustomPayloadPacket::payload);

   @Override
   public PacketType<ServerboundCustomPayloadPacket> type() {
      return CommonPacketTypes.SERVERBOUND_CUSTOM_PAYLOAD;
   }

   public void handle(final ServerCommonPacketListener listener) {
      listener.handleCustomPayload(this);
   }
}
