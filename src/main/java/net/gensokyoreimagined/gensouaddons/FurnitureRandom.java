package net.gensokyoreimagined.gensouaddons;

import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.api.OraxenFurniture;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.api.events.furniture.OraxenFurniturePlaceEvent;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import it.unimi.dsi.fastutil.floats.FloatFloatImmutablePair;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class FurnitureRandom {




    static class FurnitureRandomMechanicFactory extends MechanicFactory implements Listener {

        public FurnitureRandomMechanicFactory(String mechanicid) {
            super(mechanicid);
            MechanicsManager.registerListeners(OraxenPlugin.get(), getMechanicID(),this);
        }

        @Override
        public Mechanic parse(ConfigurationSection itemMechanicConfiguration) {
            Mechanic mechanic = new RandomMechanic(this, itemMechanicConfiguration);
            addToImplemented(mechanic);
            return mechanic;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlace(OraxenFurniturePlaceEvent event) {
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

            Entity entity = event.getBaseEntity();
            String itemID = OraxenItems.getIdByItem(item);
            if (isNotImplementedIn(itemID)) {
                return;
            }
            RandomMechanic durabilityMechanic = (RandomMechanic) getMechanic(itemID);
            FurnitureMechanic furnitureMechanic = OraxenFurniture.getFurnitureMechanic(entity);
            if(furnitureMechanic==null){
                return;
            }
            durabilityMechanic.call(event);
        }
    }

    static class RandomMechanic extends Mechanic {

        private final List<Vector> offsets;
        private final List<FloatFloatImmutablePair> rotations;
        public RandomMechanic(MechanicFactory mechanicFactory,
                              ConfigurationSection section) {
        /* We give:
        - an instance of the Factory which created the mechanic
        - the section used to configure the mechanic
        - the item modifier(s)
         */
            super(mechanicFactory, section, item ->
                    item);
            offsets = new ArrayList<>();
            rotations = new ArrayList<>();
            section.getStringList("offsets").forEach(text -> {
                offsets.add(new Vector(Double.parseDouble(text.split(",")[0]),Double.parseDouble(text.split(",")[1]),Double.parseDouble(text.split(",")[2])));
                rotations.add(FloatFloatImmutablePair.of(Float.parseFloat(text.split(",")[3]),Float.parseFloat(text.split(",")[4])));
            });

        }

        public void call(OraxenFurniturePlaceEvent event){
            if(offsets.size()==0) return;
            int index = (int)(Math.random()*offsets.size());
            Location newLocation = event.getBaseEntity().getLocation().add(offsets.get(index));
            newLocation.setPitch(newLocation.getPitch()+rotations.get(index).leftFloat());
            newLocation.setYaw(newLocation.getYaw()+rotations.get(index).rightFloat());
            event.getBaseEntity().teleport(newLocation);
        }
    }

}
