package dev.wildy.mossad.modules;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.mixininterface.IChatHud;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.List;

import static dev.wildy.mossad.Mossad.CATEGORY;

public class FakeHB extends Module {

    public FakeHB() {
        super(CATEGORY, "fake-highway-builder", "Fakes highway building stats.");
    }

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final Setting<String> name = sgGeneral.add(new StringSetting.Builder()
        .name("Module Name")
        .description("Fake module name.")
        .defaultValue("Highway Builder")
        .build()
    );
    private final Setting<Integer> distance = sgGeneral.add(new IntSetting.Builder()
        .name("Distance")
        .description("Distance to fake.")
        .defaultValue(0)
        .sliderMin(0)
        .sliderMax(100)
        .min(0)
        .build()
    );
    private final Setting<Integer> broken = sgGeneral.add(new IntSetting.Builder()
        .name("Blocks Broken")
        .description("Blocks Broken to fake.")
        .defaultValue(0)
        .sliderMin(0)
        .sliderMax(100)
        .min(0)
        .build()
    );
    private final Setting<Integer> placed = sgGeneral.add(new IntSetting.Builder()
        .name("Blocks Placed")
        .description("Blocks Placed to fake.")
        .defaultValue(0)
        .sliderMin(0)
        .sliderMax(100)
        .min(0)
        .build()
    );

    @EventHandler
    public void onActivate() {
        sendMsg(String.format("§7[§d%s§7] Distance: §f%d", name.get(), distance.get()));
        sendMsg(String.format("§7[§d%s§7] Blocks broken: §f%d", name.get(), broken.get()));
        sendMsg(String.format("§7[§d%s§7] Blocks placed: §f%d", name.get(), placed.get()));
        toggle();
    }

    public void sendMsg(String message, Object... args) {
        if (MinecraftClient.getInstance().world == null) return;

        Text prefix = Text.empty()
            .append(Text.literal("[").formatted(Formatting.GRAY))
            .append(Text.literal("Meteor").styled(style -> style.withColor(TextColor.fromRgb(MeteorClient.ADDON.color.getPacked()))))
            .append(Text.literal("] ").formatted(Formatting.GRAY));

        String formattedMessage = String.format(message, args);

        Text msg = Text.literal(formattedMessage).formatted(Formatting.GRAY);

        Text finalMessage = Text.empty().append(prefix).append(msg);

        ((IChatHud) MinecraftClient.getInstance().inGameHud.getChatHud()).meteor$add(finalMessage, 0);
    }



}
