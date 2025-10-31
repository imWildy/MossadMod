package dev.wildy.mossad.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import java.util.Random;

import static dev.wildy.mossad.Mossad.CATEGORY;

public class Autism extends Module {
    private final Random rand = new Random();

    public Autism() {
        super(CATEGORY, "autism", "Makes you look autistic");
    }

    @EventHandler
    public void onTickPre(TickEvent.Pre ev) {
        float randY = rand.nextFloat() * 360f - 180f;
        float randP = rand.nextFloat() * 180f - 90f;

        assert mc.player != null;
        mc.player.setYaw(randY);
        mc.player.setPitch(randP);
    }
}
