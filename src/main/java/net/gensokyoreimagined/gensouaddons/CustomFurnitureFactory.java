package net.gensokyoreimagined.gensouaddons;

import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureFactory;
import org.bukkit.configuration.ConfigurationSection;

public class CustomFurnitureFactory extends FurnitureFactory {
    public CustomFurnitureFactory(ConfigurationSection section) {
        super(section);
    }
    @Override
    public Mechanic parse(ConfigurationSection itemMechanicConfiguration) {
        Mechanic mechanic = new CustomFurnitureMechanic(this, itemMechanicConfiguration);
        this.addToImplemented(mechanic);
        return mechanic;
    }
}
