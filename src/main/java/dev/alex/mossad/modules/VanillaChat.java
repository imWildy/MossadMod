/**
 * Ported from Vanilla UI (https://github.com/9aze/vanilla-ui)
 */

package dev.alex.mossad.modules;

import dev.alex.mossad.Mossad;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VanillaChat extends Module {

    private static final Pattern chatRegex = Pattern.compile("^(?:\\[(.*?)\\]\\s*)?([A-Za-z0-9_]+) » (.+)$");

    private final SettingGroup sgText = this.settings.createGroup("Username");
    private final SettingGroup sgUsernameColors = this.settings.createGroup("Username Colors");
    private final SettingGroup sgChatColors = this.settings.createGroup("Chat Colors");
    private final SettingGroup sgGreenText = this.settings.createGroup("Green Text");


    public enum colorMode {
        Username,
        Chat,
        Custom
    }

    private final Setting<colorMode> textColorMode = sgText.add(new EnumSetting.Builder<colorMode>()
        .name("color-mode")
        .description("Controls how username prefix/suffix are colored.")
        .defaultValue(colorMode.Chat)
        .build()
    );

    private final Setting<SettingColor> customTextColor = sgText.add(new ColorSetting.Builder()
        .name("custom-text-color")
        .description("Color used when color-mode is set to Custom.")
        .defaultValue(new SettingColor(255, 255, 255))
        .visible(() -> textColorMode.get() == colorMode.Custom)
        .build()
    );

    // username prefix/suffix shit
    public final Setting<String> usernamePrefix = sgText.add(new StringSetting.Builder()
        .name("username-prefix")
        .description("Prefix of the username.")
        .defaultValue("<")
        .build()
    );

    public final Setting<String> usernameSuffix = sgText.add(new StringSetting.Builder()
        .name("username-suffix")
        .description("Suffix of the username.")
        .defaultValue("> ")
        .build()
    );

    // greentext
    public final Setting<Boolean> greenText = sgGreenText.add(new BoolSetting.Builder()
        .name("green-text")
        .description("Enable 4chan-style >greentext.")
        .defaultValue(true)
        .build()
    );

    public final Setting<SettingColor> greenTextColor = sgGreenText.add(new ColorSetting.Builder()
        .name("green-text-color")
        .description("The color of the greentext.")
        .visible(greenText::get)
        .defaultValue(new SettingColor(85, 255, 85))
        .build()
    );

    public final Setting<String> greenTextPrefix = sgGreenText.add(new StringSetting.Builder()
        .name("green-text-prefix")
        .description("The prefix of the greentext.")
        .visible(greenText::get)
        .defaultValue(">")
        .build()
    );

    public final Setting<Boolean> usernameColors = sgUsernameColors.add(new BoolSetting.Builder()
        .name("custom-username-colors")
        .description("Enables username rank colors.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> chatColors = sgChatColors.add(new BoolSetting.Builder()
        .name("custom-chat-colors")
        .description("Enables chat rank colors.")
        .defaultValue(true)
        .build()
    );

    //username colors
    private final Setting<SettingColor> defaultUserColor = sgUsernameColors.add(new ColorSetting.Builder()
        .name("default")
        .description("Default username color.")
        .defaultValue(new SettingColor(170, 170, 170))
        .visible(usernameColors::get)
        .build()
    );

    private final Setting<SettingColor> youtubeUserColor = sgUsernameColors.add(new ColorSetting.Builder()
        .name("youtube")
        .description("YouTube rank username color.")
        .defaultValue(new SettingColor(240, 107, 107))
        .visible(usernameColors::get)
        .build()
    );

    private final Setting<SettingColor> primeUserColor = sgUsernameColors.add(new ColorSetting.Builder()
        .name("prime")
        .description("Prime rank username color.")
        .defaultValue(new SettingColor(84, 252, 252))
        .visible(usernameColors::get)
        .build()
    );

    private final Setting<SettingColor> eliteUserColor = sgUsernameColors.add(new ColorSetting.Builder()
        .name("elite")
        .description("Elite rank username color.")
        .defaultValue(new SettingColor(239, 239, 80))
        .visible(usernameColors::get)
        .build()
    );

    private final Setting<SettingColor> apexUserColor = sgUsernameColors.add(new ColorSetting.Builder()
        .name("apex")
        .description("Apex rank username color.")
        .defaultValue(new SettingColor(252, 168, 0))
        .visible(usernameColors::get)
        .build()
    );

    private final Setting<SettingColor> legendUserColor = sgUsernameColors.add(new ColorSetting.Builder()
        .name("legend")
        .description("Legend rank username color.")
        .defaultValue(new SettingColor(255, 246, 143))
        .visible(usernameColors::get)
        .build()
    );

    private final Setting<SettingColor> ownerUserColor = sgUsernameColors.add(new ColorSetting.Builder()
        .name("owner")
        .description("Owner rank username color.")
        .defaultValue(new SettingColor(139, 0, 0))
        .visible(usernameColors::get)
        .build()
    );

    //chat colors
    private final Setting<SettingColor> defaultChatColor = sgChatColors.add(new ColorSetting.Builder()
        .name("default")
        .description("Default chat color.")
        .defaultValue(new SettingColor(170, 170, 170))
        .visible(chatColors::get)
        .build()
    );

    private final Setting<SettingColor> youtubeChatColor = sgChatColors.add(new ColorSetting.Builder()
        .name("youtube")
        .description("YouTube rank chat color.")
        .defaultValue(new SettingColor(255, 255, 255))
        .visible(chatColors::get)
        .build()
    );

    private final Setting<SettingColor> primeChatColor = sgChatColors.add(new ColorSetting.Builder()
        .name("prime")
        .description("Prime rank chat color.")
        .defaultValue(new SettingColor(255, 255, 255))
        .visible(chatColors::get)
        .build()
    );

    private final Setting<SettingColor> eliteChatColor = sgChatColors.add(new ColorSetting.Builder()
        .name("elite")
        .description("Elite rank chat color.")
        .defaultValue(new SettingColor(255, 255, 255))
        .visible(chatColors::get)
        .build()
    );

    private final Setting<SettingColor> apexChatColor = sgChatColors.add(new ColorSetting.Builder()
        .name("apex")
        .description("Apex rank chat color.")
        .defaultValue(new SettingColor(255, 255, 255))
        .visible(chatColors::get)
        .build()
    );

    private final Setting<SettingColor> legendChatColor = sgChatColors.add(new ColorSetting.Builder()
        .name("legend")
        .description("Legend rank chat color.")
        .defaultValue(new SettingColor(255, 255, 255))
        .visible(chatColors::get)
        .build()
    );

    private final Setting<SettingColor> ownerChatColor = sgChatColors.add(new ColorSetting.Builder()
        .name("owner")
        .description("Owner rank chat color.")
        .defaultValue(new SettingColor(255, 255, 255))
        .visible(chatColors::get)
        .build()
    );



    public VanillaChat() {
        super(Mossad.CATEGORY, "vanilla-chat", "A module to make the appearance of 6b6t chat look more vanilla. (9aze/vanilla-ui)");
    }


    @EventHandler
    public void onMessageReceive(ReceiveMessageEvent e) {
        String text = e.getMessage().getString();
        Matcher m = chatRegex.matcher(text);

        if (!m.matches()) return;

        String rank = m.group(1);
        String username = m.group(2);
        String message = m.group(3);

        MutableComponent user = Component.literal(username)
            .withStyle(style -> style.withColor(getRankColor(rank).toTextColor()));

        MutableComponent msg = Component.literal(message)
            .withStyle(style -> style.withColor(getChatColor(message, rank).toTextColor()));

        MutableComponent prefix = Component.literal(usernamePrefix.get())
            .withStyle(style -> style.withColor(getContextColor(message, rank).toTextColor()));

        MutableComponent suffix = Component.literal(usernameSuffix.get())
            .withStyle(style -> style.withColor(getContextColor(message, rank).toTextColor()));

        MutableComponent finalMsg = prefix
            .append(user)
            .append(suffix)
            .append(msg);

        e.setMessage(finalMsg);
    }

    private Color getContextColor(String msg ,String rank) {
        switch(textColorMode.get()) {
            case colorMode.Chat -> {
                return getChatColor(msg.replaceAll(greenTextPrefix.get(), ""), rank);
            }
            case colorMode.Username -> {
                return getRankColor(rank);
            } case colorMode.Custom -> {
                return customTextColor.get();
            }
        }
        return Color.WHITE;
    }


    private Color getRankColor(String rank) {
        if(!usernameColors.get()) return Color.WHITE;
        if(rank == null) return defaultUserColor.get();

        String r = rank.toLowerCase();

        if (r.contains("youtube") || r.contains("yt")) return youtubeUserColor.get();
        if (r.contains("apex")) return apexUserColor.get();
        if (r.contains("legend")) return legendUserColor.get();
        if (r.contains("elite")) return eliteUserColor.get();
        if (r.contains("prime")) return primeUserColor.get();
        if (r.contains("owner")) return ownerUserColor.get();

        return defaultUserColor.get();
    }

    private Color getChatColor(String msg, String rank) {

        if(msg.startsWith(greenTextPrefix.get()) && greenText.get()) {
            return greenTextColor.get();
        }

        if(!chatColors.get()) return Color.WHITE;
        if(rank == null) return defaultChatColor.get();

        String r = rank.toLowerCase();

        if (r.contains("youtube") || r.contains("yt")) return youtubeChatColor.get();
        if (r.contains("apex")) return apexChatColor.get();
        if (r.contains("legend")) return legendChatColor.get();
        if (r.contains("elite")) return eliteChatColor.get();
        if (r.contains("prime")) return primeChatColor.get();
        if (r.contains("owner")) return ownerChatColor.get();

        return defaultChatColor.get();
    }

}
