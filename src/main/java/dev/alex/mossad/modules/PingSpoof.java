package dev.alex.mossad.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;


import static dev.alex.mossad.Mossad.CATEGORY;

public class PingSpoof extends Module {

    public PingSpoof() {
        super(CATEGORY, "ping-spoof", "Adds a delay to packets");
    }

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The delay to the packets (in ms).")
        .defaultValue(100)
        .min(0)
        .sliderMin(0)
        .sliderMax(1000)
        .build()
    );

    @EventHandler
    public void onReceivePacket(PacketEvent.Receive event) {
        if (event.packet instanceof ClientboundKeepAlivePacket) {
            event.cancel();

            new Thread(() -> {
                try {
                    Thread.sleep(delay.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mc.execute(() -> {
                    Connection connection = mc.getConnection().getConnection();
                    connection.send(new ServerboundKeepAlivePacket(((ClientboundKeepAlivePacket) event.packet).getId()));
                });
            }).start();
        }
    }
}
