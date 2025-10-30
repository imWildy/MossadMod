package dev.wildy.mossad;

import dev.wildy.mossad.hud.OnlineFriendsList;
import dev.wildy.mossad.modules.ChatCleaner;
import com.mojang.logging.LogUtils;
import dev.wildy.mossad.modules.FakeHB;
import dev.wildy.mossad.modules.PingSpoof;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class Mossad extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Mossad");
    public static final HudGroup HUD_GROUP = new HudGroup("Mossad");

    @Override
    public void onInitialize() {
        LOG.info("Initialising Mossad Mod");

        // Modules
        Modules.get().add(new ChatCleaner());
        Modules.get().add(new FakeHB());
        Modules.get().add(new PingSpoof());

        // Hud
        Hud.get().register(OnlineFriendsList.INFO);

    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "dev.wildy.mossad";
    }
}
