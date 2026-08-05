package dev.alex.mossad;

import dev.alex.mossad.hud.DubCounter;
import dev.alex.mossad.hud.OnlineFriendsList;
import dev.alex.mossad.modules.PingSpoof;
import dev.alex.mossad.modules.*;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;

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
        Modules.get().add(new PingSpoof());

        // Hud
        Hud.get().register(OnlineFriendsList.INFO);
        Hud.get().register(DubCounter.INFO);

    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "dev.alex.mossad";
    }
}
