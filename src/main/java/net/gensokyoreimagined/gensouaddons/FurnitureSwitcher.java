package net.gensokyoreimagined.gensouaddons;

import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.api.OraxenFurniture;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import org.bukkit.Material;
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

import java.lang.reflect.Field;
import java.util.*;

public class FurnitureSwitcher {


    static class FurnitureSwitcherMechanicFactory extends MechanicFactory implements Listener {

        public FurnitureSwitcherMechanicFactory(String mechanicId) {
            super(mechanicId);
            MechanicsManager.registerListeners(OraxenPlugin.get(),getMechanicID(),this);
        }
        @Override
        public Mechanic parse(ConfigurationSection itemMechanicConfiguration) {
            Mechanic mechanic = new FurnitureSwitcherMechanic(this, itemMechanicConfiguration);
            addToImplemented(mechanic);
            return mechanic;
        }
        @EventHandler(priority = EventPriority.LOWEST)
        private void onRightClick(PlayerInteractEntityEvent event) {
            if(!event.getPlayer().getEquipment().getItemInMainHand().getType().isEmpty()) return;
            Entity entity = event.getRightClicked();
            FurnitureMechanic furnitureMechanic = OraxenFurniture.getFurnitureMechanic(entity);
            if(furnitureMechanic==null||isNotImplementedIn(furnitureMechanic.getItemID())){
                return;
            }
            ((FurnitureSwitcherMechanic) getMechanic(furnitureMechanic.getItemID())).call(entity);
            event.setCancelled(true);
        }
        @EventHandler(priority = EventPriority.LOWEST)
        private void onRightClick(PlayerInteractEvent event) {
            if(!event.getPlayer().getEquipment().getItemInMainHand().getType().isEmpty()) return;
            if(event.getHand()!= EquipmentSlot.HAND) return;
            if(event.getClickedBlock()==null||event.getClickedBlock().getType()!= Material.BARRIER){
                return;
            }
            if(!event.getAction().isRightClick()) return;
            FurnitureMechanic furnitureMechanic = OraxenFurniture.getFurnitureMechanic(event.getClickedBlock());

            if(furnitureMechanic==null||isNotImplementedIn(furnitureMechanic.getItemID())){
                return;
            }
            ((FurnitureSwitcherMechanic) getMechanic(furnitureMechanic.getItemID()))
                    .call(furnitureMechanic.getBaseEntity(event.getClickedBlock()));
            event.setCancelled(true);
        }
    }
    private static final Field itemField;
    static {
        try {
            itemField = FurnitureMechanic.class.getDeclaredField("placedItemId");
            itemField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    static class FurnitureSwitcherMechanic extends Mechanic {

        private final Set<ItemStack> furnitures = new HashSet<>();
        public FurnitureSwitcherMechanic(MechanicFactory mechanicFactory,
                                         ConfigurationSection section) {
        /* We give:
        - an instance of the Factory which created the mechanic
        - the section used to configure the mechanic
        - the item modifier(s)
         */
            super(mechanicFactory, section, item ->
                    item);
            List<String> ids = section.getStringList("furnitures");
            ids.forEach(furniture -> {
                try {
                    FurnitureMechanic mechanic = (FurnitureMechanic) FurnitureFactory.getInstance().getMechanic(furniture);
                    furnitures.add(OraxenItems.getOptionalItemById((String) itemField.get(mechanic)).map(b -> b.build().clone()).orElse(OraxenItems.getItemById(this.getItemID()).build()));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });

        }

        public void call(Entity orig){



            ItemStack origItemStack = null;
            if(orig instanceof ItemFrame itemFrame) origItemStack=itemFrame.getItem();
            else if(orig instanceof ItemDisplay itemDisplay) origItemStack=itemDisplay.getItemStack();

            Iterator<ItemStack> setIterator = furnitures.iterator();
            ItemStack temp = null;
            while(setIterator.hasNext()){
                temp = setIterator.next();
                if(temp.equals(origItemStack)){
                    break;
                }
            }
            if((!temp.equals(origItemStack))||!setIterator.hasNext()) setIterator=furnitures.iterator();
            ItemStack item = setIterator.next();
            if(orig instanceof ItemFrame itemFrame){
                itemFrame.setItem(item,false);
            }else if(orig instanceof ItemDisplay itemDisplay){
                itemDisplay.setItemStack(item);
            }


        }
    }

}
