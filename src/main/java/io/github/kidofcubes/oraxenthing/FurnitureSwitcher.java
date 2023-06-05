package io.github.kidofcubes.oraxenthing;

import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.api.OraxenFurniture;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.BlockLocation;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FurnitureSwitcher {
    public static class FurnitureSwitcherMechanicManager implements Listener {

        private final FurnitureSwitcherMechanicFactory factory;

        public FurnitureSwitcherMechanicManager(FurnitureSwitcherMechanicFactory factory) {
            this.factory = factory;
        }

        @EventHandler(priority = EventPriority.LOWEST)
        private void onItemDamaged(PlayerInteractEntityEvent event) {
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            Entity entity = event.getRightClicked();
            String itemID = OraxenItems.getIdByItem(item);
            if (factory.isNotImplementedIn(itemID)) {
                return;
            }
            FurnitureSwitcherMechanic furnitureSwitcherMechanic = (FurnitureSwitcherMechanic) factory.getMechanic(itemID);
            FurnitureMechanic furnitureMechanic = OraxenFurniture.getFurnitureMechanic(entity);
            if(furnitureMechanic==null){
                return;
            }
            Entity baseEntity = furnitureMechanic.getBaseEntity(entity);
            furnitureSwitcherMechanic.call(furnitureMechanic,baseEntity);
            event.setCancelled(true);
        }
        @EventHandler(priority = EventPriority.LOWEST)
        private void onItemDamaged(PlayerInteractEvent event) {
            if(event.getHand()!= EquipmentSlot.HAND) return;
            if(event.getClickedBlock()==null||event.getClickedBlock().getType()!= Material.BARRIER){
                return;
            }
            if(!event.getAction().isRightClick()) return;
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            String itemID = OraxenItems.getIdByItem(item);
            if (factory.isNotImplementedIn(itemID)) {
                return;
            }
            FurnitureSwitcherMechanic furnitureSwitcherMechanic = (FurnitureSwitcherMechanic) factory.getMechanic(itemID);
            FurnitureMechanic furnitureMechanic = OraxenFurniture.getFurnitureMechanic(event.getClickedBlock());

            if(furnitureMechanic==null){
                return;
            }
            Entity baseEntity = furnitureMechanic.getBaseEntity(event.getClickedBlock());
            furnitureSwitcherMechanic.call(furnitureMechanic,baseEntity);
            event.setCancelled(true);
        }
    }


    static class FurnitureSwitcherMechanicFactory extends MechanicFactory {

        public FurnitureSwitcherMechanicFactory(ConfigurationSection section) {
            super(section);
            MechanicsManager.registerListeners(OraxenPlugin.get(),new FurnitureSwitcherMechanicManager(this));
        }
        @Override
        public Mechanic parse(ConfigurationSection itemMechanicConfiguration) {
            Mechanic mechanic = new FurnitureSwitcherMechanic(this, itemMechanicConfiguration);
            addToImplemented(mechanic);
            return mechanic;
        }
    }

    static class FurnitureSwitcherMechanic extends Mechanic {

        private final List<String> furnitures;
        public FurnitureSwitcherMechanic(MechanicFactory mechanicFactory,
                                         ConfigurationSection section) {
        /* We give:
        - an instance of the Factory which created the mechanic
        - the section used to configure the mechanic
        - the item modifier(s)
         */
            super(mechanicFactory, section, item ->
                    item);
            furnitures = section.getStringList("furnitures");

        }

        public void call(FurnitureMechanic furnitureMechanic, Entity orig){
            Location location = orig.getLocation();

            if(orig.getPersistentDataContainer().has(FurnitureMechanic.ROOT_KEY)){
                location = new BlockLocation(
                        Objects.requireNonNull(orig.getPersistentDataContainer().get(FurnitureMechanic.ROOT_KEY, PersistentDataType.STRING))
                ).toLocation(orig.getWorld());
            }
            furnitureMechanic.removeSolid(location.getBlock());
            if (furnitureMechanic.hasBarriers())
                for (Block barrier : furnitureMechanic.getBarriers().stream().map(blockLoc -> blockLoc.toLocation(orig.getWorld()).getBlock()).collect(Collectors.toSet()))
                    if (location.getBlock().getType() == Material.BARRIER) furnitureMechanic.removeSolid(barrier);
                    else furnitureMechanic.removeAirFurniture(orig);
            float yaw = orig.getLocation().getYaw();


            int index = furnitures.indexOf(furnitureMechanic.getItemID());
            if(index!=-1){
                index= (index+1)%furnitures.size();
            }else index = 0;
            FurnitureMechanic mechanic = (FurnitureMechanic) FurnitureFactory.getInstance().getMechanic(furnitures.get(index));
            if (mechanic == null){
                return;
            }
            if(orig instanceof ItemFrame itemFrame){
                mechanic.place(location,yaw,itemFrame.getFacing());
            }else if(orig instanceof ItemDisplay){
                mechanic.place(location,yaw, BlockFace.DOWN); //no blockface from itemdisplay
            }
        }
    }

}
