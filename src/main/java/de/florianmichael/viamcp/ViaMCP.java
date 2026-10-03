/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.florianmichael.viamcp;

import com.viaversion.viabackwards.protocol.v1_17to1_16_4.Protocol1_17To1_16_4;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.protocols.v1_16_1to1_16_2.packet.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.v1_16_1to1_16_2.packet.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.v1_16_4to1_17.packet.ClientboundPackets1_17;
import com.viaversion.viaversion.protocols.v1_16_4to1_17.packet.ServerboundPackets1_17;
import com.viaversion.viabackwards.protocol.v1_20_3to1_20_2.Protocol1_20_3To1_20_2;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import com.viaversion.viaversion.protocols.v1_21_11to26_1.packet.ClientboundPackets26_1;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viabackwards.protocol.v26_2to26_1.Protocol26_2To26_1;
import com.mojang.authlib.GameProfile;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.v1_20to1_20_2.packet.ServerboundConfigurationPackets1_20_2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EnumPlayerModelParts;

import de.florianmichael.vialoadingbase.ViaLoadingBase;
import de.florianmichael.viamcp.fixes.MovementAttributes;
import de.florianmichael.viamcp.gui.AsyncVersionSlider;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ViaMCP {
    public final static int NATIVE_VERSION = 47;
    public static ViaMCP INSTANCE;
    public UserConnection user;
    
        
    public static void create() {
        INSTANCE = new ViaMCP();
    }

    private AsyncVersionSlider asyncVersionSlider;

    public ViaMCP() {
        ViaLoadingBase.ViaLoadingBaseBuilder.create().runDirectory(new File("ViaMCP")).nativeVersion(NATIVE_VERSION).onProtocolReload(protocolVersion -> {
            if (getAsyncVersionSlider() != null) {
                getAsyncVersionSlider().setVersion(protocolVersion.getVersion());
            }
        }).build();
        applyFix();
    }
    
    public void applyFix() {
    	fixTransactions();
    	fixHypixelLogin();
    	fix26_2Attributes();
    }

    private void fixTransactions() {
        // We handle the differences between those versions in the net code, so we can make the Via handlers pass through
        final Protocol1_17To1_16_4 protocol = Via.getManager().getProtocolManager().getProtocol(Protocol1_17To1_16_4.class);
        protocol.registerClientbound(ClientboundPackets1_17.PING, ClientboundPackets1_16_2.CONTAINER_ACK, wrapper -> {}, true);
        protocol.registerServerbound(ServerboundPackets1_16_2.CONTAINER_ACK, ServerboundPackets1_17.PONG, wrapper -> {}, true);
    }
    
    private void fixHypixelLogin() {
    	Protocol1_20_3To1_20_2 protocol1_20_3To1_20_2 = Via.getManager().getProtocolManager().getProtocol(Protocol1_20_3To1_20_2.class);
    	if (protocol1_20_3To1_20_2 == null) {
    		return;
    	}
        protocol1_20_3To1_20_2.registerServerbound(State.LOGIN, ServerboundLoginPackets.LOGIN_ACKNOWLEDGED, packetWrapper -> {
            final UserConnection connection = packetWrapper.user();
            this.user = connection;
            // Queued on the event loop so it runs after this acknowledgement has been written
            connection.getChannel().eventLoop().execute(() -> sendConfigurationInfo(connection, 0));
        });
        protocol1_20_3To1_20_2.registerServerbound(State.LOGIN, ServerboundLoginPackets.HELLO, packetWrapper -> {
        	packetWrapper.cancel();
        	PacketWrapper packet = PacketWrapper.create(ServerboundLoginPackets.HELLO, packetWrapper.user());
        	GameProfile profile = Minecraft.getMinecraft().getSession().getProfile();
        	packet.write(Types.STRING, profile.getName());
        	UUID uuid = profile.getId();
        	packet.write(Types.UUID, profile.getId());
            packet.sendToServer(Protocol1_20_3To1_20_2.class);
        });
    }
    
    /**
     * Hypixel only lets 1.21.4+ clients in that sent their brand and client information during the
     * configuration phase. That phase only lasts until the server's FINISH_CONFIGURATION, so this
     * has to be sent once per login (not per tick) and only while the connection is still in it.
     */
    private void sendConfigurationInfo(UserConnection connection, int attempt) {
        if (!ViaLoadingBase.getInstance().getTargetVersion().newerThanOrEqualTo(ProtocolVersion.v1_21_4)) {
            return;
        }

        final State state = connection.getProtocolInfo().getServerState();
        if (state == State.LOGIN && attempt < 50) {
            // The state switch to CONFIGURATION hasn't happened yet
            connection.getChannel().eventLoop().schedule(() -> sendConfigurationInfo(connection, attempt + 1), 10, TimeUnit.MILLISECONDS);
            return;
        }
        if (state != State.CONFIGURATION) {
            return;
        }

        final GameSettings settings = Minecraft.getMinecraft().gameSettings;
        try {
            PacketWrapper brand = PacketWrapper.create(ServerboundConfigurationPackets1_20_2.CUSTOM_PAYLOAD, connection);
            brand.write(Types.STRING, "minecraft:brand");
            brand.write(Types.STRING, "vanilla");
            brand.sendToServer(Protocol1_20_3To1_20_2.class);

            int modelParts = 0;
            for (EnumPlayerModelParts parts : settings.getModelParts()) {
                modelParts |= parts.getPartMask();
            }

            PacketWrapper info = PacketWrapper.create(ServerboundConfigurationPackets1_20_2.CLIENT_INFORMATION, connection);
            info.write(Types.STRING, settings.language.toLowerCase());
            info.write(Types.BYTE, (byte) settings.renderDistanceChunks);
            info.write(Types.VAR_INT, settings.chatVisibility.ordinal());
            info.write(Types.BOOLEAN, settings.chatColours);
            info.write(Types.UNSIGNED_BYTE, (short) modelParts);
            info.write(Types.VAR_INT, 1);
            info.write(Types.BOOLEAN, true);
            info.write(Types.BOOLEAN, true);
            info.sendToServer(Protocol1_20_3To1_20_2.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private void fix26_2Attributes() {
    	Protocol26_2To26_1 protocol26_2To26_1 = Via.getManager().getProtocolManager().getProtocol(Protocol26_2To26_1.class);
    	if (protocol26_2To26_1 == null) {
    		return;
    	}
    	protocol26_2To26_1.registerClientbound(ClientboundPackets26_1.UPDATE_ATTRIBUTES, ClientboundPackets26_1.UPDATE_ATTRIBUTES, handler -> {
    		int entityId = handler.passthrough(Types.VAR_INT);
    		int size = handler.passthrough(Types.VAR_INT);
    		int newSize = size;

    		MovementAttributes data = MovementAttributes.getOrCreate(entityId);

    		for (int i = 0; i < size; i++) {
    			int attributeId = handler.read(Types.VAR_INT);
    			int mappedId = protocol26_2To26_1.getMappingData().getNewAttributeId(attributeId);
    			String attributeKey = protocol26_2To26_1.getMappingData().getAttributeMappings().identifier(attributeId);
    			
    			double base = handler.read(Types.DOUBLE);
    			int modifierSize = handler.read(Types.VAR_INT);

    			double addValue = 0.0D;
    			double addMultipliedBase = 0.0D;
    			double multipliedTotal = 1.0D;

    			if (mappedId == -1) {
    				newSize--;
    			} else {
    				handler.write(Types.VAR_INT, mappedId);
    				handler.write(Types.DOUBLE, base);
    				handler.write(Types.VAR_INT, modifierSize);
    			}

    			for (int j = 0; j < modifierSize; j++) {
    				String modifierId = handler.read(Types.STRING);
    				double amount = handler.read(Types.DOUBLE);
    				byte operation = handler.read(Types.BYTE);

    				if (operation == 0) {
    					addValue += amount;
    				} else if (operation == 1) {
    					addMultipliedBase += amount;
    				} else if (operation == 2) {
    					multipliedTotal *= 1.0D + amount;
    				}

    				if (mappedId != -1) {
    					handler.write(Types.STRING, modifierId);
    					handler.write(Types.DOUBLE, amount);
    					handler.write(Types.BYTE, operation);
    				}
    			}

    			double finalValue = (base + addValue + base * addMultipliedBase) * multipliedTotal;

    			if ("minecraft:air_drag_modifier".equals(attributeKey)) {
    				data.setAirDrag(finalValue); // air drag, same as block friction but in air.
    			} else if ("minecraft:bounciness".equals(attributeKey)) {
    				data.setBounciness(finalValue); // block bounciness
    			} else if ("minecraft:friction_modifier".equals(attributeKey)) {
    				data.setFriction(finalValue); // block friction, explains how fast player can move on blocks.
    			}
    		}

    		if (size != newSize) {
    			handler.set(Types.VAR_INT, 1, newSize);
    		}
    	}, true);
    }

    public void initAsyncSlider() {
        this.initAsyncSlider(5, 5, 110, 20);
    }

    public void initAsyncSlider(int x, int y, int width, int height) {
        asyncVersionSlider = new AsyncVersionSlider(-1, x, y, Math.max(width, 110), height);
    }

    public AsyncVersionSlider getAsyncVersionSlider() {
        return asyncVersionSlider;
    }
}
