package dev.wildy.mossad.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;

import java.util.Random;

import static dev.wildy.mossad.Mossad.CATEGORY;

public class Autism extends Module {
    private final Random rand = new Random();

    public Autism() {
        super(CATEGORY, "autism", "Makes you look autistic");
    }

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final Setting<Boolean> silent = sgGeneral.add(new BoolSetting.Builder()
        .name("silent-rotations")
        .description("Silent rotations")
        .defaultValue(true)
        .build()
    );

    @EventHandler
    public void onTickPre(TickEvent.Pre ev) {
        float randY = rand.nextFloat() * 360f - 180f;
        float randP = rand.nextFloat() * 180f - 90f;

        assert mc.player != null;

        if (silent.get()) {
            Rotations.rotate(randY, randP);
        } else {
            mc.player.setYaw(randY);
            mc.player.setPitch(randP);
        }
    }
}
