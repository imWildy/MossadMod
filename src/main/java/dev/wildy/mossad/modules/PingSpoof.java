package dev.wildy.mossad.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket;
import net.minecraft.network.packet.s2c.common.KeepAliveS2CPacket;


import static dev.wildy.mossad.Mossad.CATEGORY;

public class PingSpoof extends Module {

    public PingSpoof() {
        super(CATEGORY, "ping-spoof", "Adds a delay to packets");
    }

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The delay to the packets")
        .defaultValue(100)
        .min(0)
        .sliderMin(0)
        .sliderMax(1000)
        .build()
    );

    @EventHandler
    public void onReceivePacket(PacketEvent.Receive event) {
        if (event.packet instanceof KeepAliveS2CPacket) {
            event.cancel();

            new Thread(() -> {
                try {
                    Thread.sleep(delay.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mc.execute(() -> {
                    ClientConnection connection = mc.getNetworkHandler().getConnection();
                    connection.send(new KeepAliveC2SPacket(((KeepAliveS2CPacket) event.packet).getId()));
                });
            }).start();
        }
    }
}
