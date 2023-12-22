package net.gensokyoreimagined.gensouaddons;

import com.sk89q.worldedit.WorldEdit;
import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.config.ResourcesManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;
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
        Map.Entry<File, YamlConfiguration> mechanicsEntry = new ResourcesManager(OraxenPlugin.get()).getMechanicsEntry();
        YamlConfiguration mechanicsConfig = mechanicsEntry.getValue();
        ConfigurationSection factorySection = mechanicsConfig.getConfigurationSection("furniture");
        if (factorySection!=null&&factorySection.getBoolean("enabled")) {
            registerMechanicFactory("furniture", new CustomFurnitureFactory(factorySection),true); //WILL BREAK ON ORAXEN RELOAD
        }

        OraxenItems.loadItems();
        handler = new FaweFurnitureHandler(this);
        WorldEdit.getInstance().getEventBus().register(handler);
        getServer().getPluginManager().registerEvents(handler,this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        WorldEdit.getInstance().getEventBus().unregister(handler);
    }

}
