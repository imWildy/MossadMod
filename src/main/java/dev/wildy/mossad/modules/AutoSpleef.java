package dev.wildy.mossad.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

import static dev.wildy.mossad.Mossad.CATEGORY;

public class AutoSpleef extends Module {
    private final Random rand = new Random();

    public AutoSpleef() {
        super(CATEGORY, "auto-spleef", "Pro spleef player");
    }

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
        .name("range")
        .description("Max distance from player")
        .defaultValue(3)
        .min(1)
        .sliderMax(7)
        .build()
    );
    private final Setting<Integer> size = sgGeneral.add(new IntSetting.Builder()
        .name("size")
        .description("Amount of blocks to break")
        .defaultValue(3)
        .min(1)
        .sliderMax(5)
        .build()
    );
    private final Setting<Integer> blocksPerTick = sgGeneral.add(new IntSetting.Builder()
        .name("blocks-per-tick")
        .description("Amount of blocks to break per tick")
        .defaultValue(1)
        .min(1)
        .sliderMax(5)
        .build()
    );

    @EventHandler
    private void onTickPre(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        PlayerEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (PlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player) continue;

            double dist = mc.player.distanceTo(p);
            if (dist > range.get()) continue;

            if (dist < nearestDistance) {
                nearestDistance = dist;
                nearest = p;
            }
        }

        if (nearest == null) return;

        BlockPos base = nearest.getBlockPos().down();
        int radius = size.get() / 2;
        int broken = 0;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (broken >= blocksPerTick.get()) return;
                BlockPos blockPos = base.add(x, 0, z);
                if (mc.world.getBlockState(blockPos).getBlock() == Blocks.SNOW_BLOCK &&
                    blockPos != mc.player.getBlockPos().down()) {

                    Vec3d eye = mc.player.getEyePos();
                    Vec3d t = new Vec3d(blockPos.getX() + .5, blockPos.getY() + .5, blockPos.getZ() + .5).subtract(eye);

                    float yaw = (float) (Math.toDegrees(Math.atan2(t.z, t.x)) - 90f);
                    float pitch = (float) -Math.toDegrees(Math.atan2(t.y, Math.sqrt(t.x * t.x + t.z * t.z)));

                    Rotations.rotate(yaw, pitch);
                    assert mc.interactionManager != null;
                    mc.interactionManager.updateBlockBreakingProgress(
                        blockPos,
                        mc.player.getHorizontalFacing()
                    );
                    mc.player.swingHand(Hand.MAIN_HAND);
                    broken++;
                }
            }
        }
    }
}
