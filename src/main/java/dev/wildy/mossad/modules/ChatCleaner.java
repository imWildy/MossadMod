package dev.wildy.mossad.modules;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

import java.util.List;

import static dev.wildy.mossad.Mossad.CATEGORY;

public class ChatCleaner extends Module {

    public ChatCleaner() {
        super(CATEGORY, "chat-cleaner", "Automatically deletes messages based on its content.");
    }

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final Setting<List<String>> usernames = sgGeneral.add(new StringListSetting.Builder()
        .name("usernames")
        .description("Delete if username includes text.")
        .defaultValue(List.of("Ignore"))
        .build()
    );
    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder()
        .name("messages")
        .description("Delete if messages includes text.")
        .defaultValue(List.of("Ignore me."))
        .build()
    );

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event) {
        String msg = event.getMessage().getString();

        String username;
        String message;
        if (msg.contains("»")) {
            String[] parts = msg.split("»", 2);
            if (parts.length != 2) return;

            String[] raw = parts[0].trim().split("] ", 2);

            username = raw.length > 1 ? raw[1] : raw[0];
            message = parts[1].trim();
        } else {
            return;
        }
        if (username == null) return;

        if (usernames.get().stream().anyMatch(blockedUser -> username.toLowerCase().contains(blockedUser.toLowerCase()))) {
            event.cancel();
        } else if (messages.get().stream().anyMatch(blockedMsg -> message.toLowerCase().contains(blockedMsg.toLowerCase()))) {
            event.cancel();
        }
    }
}
