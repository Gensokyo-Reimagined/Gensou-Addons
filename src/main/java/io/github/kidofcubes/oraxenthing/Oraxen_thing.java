package io.github.kidofcubes.oraxenthing;

import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.plugin.java.JavaPlugin;

import static io.th0rgal.oraxen.mechanics.MechanicsManager.registerMechanicFactory;

public final class Oraxen_thing extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        registerMechanicFactory("randomoffset", new FurnitureRandom.FurnitureRandomMechanicFactory("randomoffset"),true);
        registerMechanicFactory("furnitureswitcher", new FurnitureSwitcher.FurnitureSwitcherMechanicFactory("furnitureswitcher"),true);
//        registerMechanicFactory("furniture", CustomFurniture.CustomFurnitureFactory::new);
        OraxenItems.loadItems();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
