package net.gensokyoreimagined.gensouaddons;

import com.sk89q.worldedit.WorldEdit;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

import static io.th0rgal.oraxen.mechanics.MechanicsManager.registerMechanicFactory;

public final class GensouAddons extends JavaPlugin {

    public static Logger logger;

    private FaweFurnitureHandler handler;
    @Override
    public void onEnable() {
        logger=getLogger();
        // Plugin startup logic
        registerMechanicFactory("randomoffset", new FurnitureRandom.FurnitureRandomMechanicFactory("randomoffset"),true);
        registerMechanicFactory("furnitureswitcher", new FurnitureSwitcher.FurnitureSwitcherMechanicFactory("furnitureswitcher"),true);

        OraxenItems.loadItems();
        handler = new FaweFurnitureHandler();
        WorldEdit.getInstance().getEventBus().register(handler);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        WorldEdit.getInstance().getEventBus().unregister(handler);
    }

}
