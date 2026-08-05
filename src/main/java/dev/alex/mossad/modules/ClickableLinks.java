package dev.alex.mossad.modules;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ServerboundKeepAlivePacket;


import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.alex.mossad.Mossad.CATEGORY;

public class ClickableLinks extends Module {

    public ClickableLinks() {
        super(CATEGORY, "clickable-links", "You can click links in chat.");
    }
    private static final Pattern linkRegex = Pattern.compile("(?:https?://)?(?:www\\.)?[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)+\\b(?:[-a-zA-Z0-9@:%_+.~#?&/=]*)");

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final Setting<Boolean> underline = sgGeneral.add(new BoolSetting.Builder()
        .name("underline-domains")
        .defaultValue(false)
        .build()
    );

    @EventHandler
    public void onMessageReceive(ReceiveMessageEvent e) {
        MutableComponent msg = e.getMessage().copy();
        String text = msg.getString();

        Matcher matcher = linkRegex.matcher(text);
        if (!matcher.find()) return;

        MutableComponent finalMsg = Component.empty();
        int lastEnd = 0;

        do {
            finalMsg.append(Component.literal(text.substring(lastEnd, matcher.start())));

            String link = matcher.group();

            finalMsg.append(
                Component.literal(link)
                    .withStyle(style -> style
                        .withUnderlined(underline.get())
                        .withClickEvent(new ClickEvent.OpenUrl(
                            URI.create(link.startsWith("http") ? link : "https://" + link)
                        ))
                    )
            );

            lastEnd = matcher.end();

        } while (matcher.find());

        finalMsg.append(Component.literal(text.substring(lastEnd)));
        e.setMessage(finalMsg);
    }
}
