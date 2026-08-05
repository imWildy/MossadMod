/**
 * Ported from Saturn Stash Addon (https://github.com/SaturnHosting/saturn-stash-addon)
 */

package dev.alex.mossad.hud;

import dev.alex.mossad.Mossad;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.*;
import meteordevelopment.meteorclient.utils.world.BlockEntityIterator;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class DubCounter extends HudElement {
    public static final HudElementInfo<DubCounter> INFO = new HudElementInfo<>(Mossad.HUD_GROUP, "DubCounter", "Counts chests.", DubCounter::new);

    public DubCounter() {
        super(INFO);
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> doubleChests = sgGeneral.add(new BoolSetting.Builder()
        .name("double-chests")
        .description("Count double chests or regular chests")
        .defaultValue(false)
        .build()
    );

    @Override
    protected double alignX(double width, Alignment alignment) {
        return box.alignX(getWidth(), width, alignment);
    }

    @Override
    public void render(HudRenderer renderer) {
        if (mc.player == null) return;

        String title = doubleChests.get() ? "Dubs: " : "Chests: ";
        int count = getChests(doubleChests.get());
        String countText = Integer.toString(count);

        double titleWidth = renderer.textWidth(title, true, 1);
        double countWidth = renderer.textWidth(countText, true, 1);
        double totalWidth = titleWidth + countWidth;
        double renderX = x + alignX(totalWidth, Alignment.Auto);

        renderer.text(title, renderX, y, Hud.get().textColors.get().getFirst(), true, 1);
        renderer.text(countText, renderX + titleWidth, y, Hud.get().textColors.get().get(1), true, 1);

        double height = renderer.textHeight(true, 1) + 2;
        setSize(totalWidth, height);
    }

    private int getChests(boolean dubs) {
        var chests = new java.util.ArrayList<ChestBlockEntity>();

        for (BlockEntityIterator it = new BlockEntityIterator(); it.hasNext(); ) {
            var blockEntity = it.next();
            if (blockEntity instanceof ChestBlockEntity chest) {
                chests.add(chest);
            }
        }

        if (dubs) {
            return (int) chests.stream()
                .filter(chest -> chest.getBlockState()
                    .getValue(ChestBlock.TYPE) != ChestType.SINGLE)
                .count() / 2;
        } else {
            return (int) chests.size();
        }
    }
}
