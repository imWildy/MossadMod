package dev.alex.mossad.modules;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.net.URI;
import java.util.Optional;
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

    @EventHandler(priority = -200)
    public void onMessageReceive(ReceiveMessageEvent e) {
        MutableComponent finalMsg = Component.empty();

        e.getMessage().visit((style, text) -> {
            Matcher matcher = linkRegex.matcher(text);
            int lastEnd = 0;

            while (matcher.find()) {
                if (matcher.start() > lastEnd) {
                    finalMsg.append(Component.literal(text.substring(lastEnd, matcher.start())).withStyle(style));
                }

                String link = matcher.group();

                finalMsg.append(
                    Component.literal(link)
                        .withStyle(style
                            .withUnderlined(underline.get())
                            .withClickEvent(new ClickEvent.OpenUrl(
                                URI.create(link.startsWith("http") ? link : "https://" + link)
                            ))
                        )
                );
                lastEnd = matcher.end();
            }

            if (lastEnd < text.length()) {
                finalMsg.append(Component.literal(text.substring(lastEnd)).withStyle(style));
            }
            return Optional.<Void>empty();
        }, Style.EMPTY);

        e.setMessage(finalMsg);
    }
}
