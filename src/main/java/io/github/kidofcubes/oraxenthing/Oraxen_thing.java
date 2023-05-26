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
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static io.th0rgal.oraxen.mechanics.MechanicsManager.registerMechanicFactory;

public final class Oraxen_thing extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("PLEASE IM GETTING ENABLED PLEASE WORKadslkhfadkjlfahklfsakj");
        System.out.println("PLEASE IM GETTING ENABLED PLEASE WORKadslkhfadkjlfahklfsakj");
        System.out.println("PLEASE IM GETTING ENABLED PLEASE WORKadslkhfadkjlfahklfsakj");
        System.out.println("PLEASE IM GETTING ENABLED PLEASE WORKadslkhfaasdasddkjlfahklfsakj");
        System.out.println("PLEASE IM GETTING ENABLED PLEASE WORKadslkhfadkjlfahklfsakj");
        System.out.println("PLEASE IM GETTING ENABLED PLEASE WORKadslkhfadkjlfahklfsakj");
        System.out.println("PLEASE IM GETTING ENABLED PLEASE WORKadslkhfaasdadkjlfahklfsakj");
        System.out.println("PLEASE IM GETTING ENABLED PLEASE WORKadslkhfadkjlfahklfsasdasdakj");
        System.out.println("PLEASE IM GETTING ENABLED PLEASE WORKadslkhfadkjlfahklfsakj");
        System.out.println("PLEASE IM GETTING ENABLED PLEASE WORKadslkhfadkjlfahklfsakj");
//        MechanicsManager.registerMechanicFactory("switchermagic",DurabilityMechanicFactory::new);
        registerMechanicFactory("randomthing", Oraxen_thing2.RandomMechanicFactory::new);
        registerMechanicFactory("furniture", Oraxen_thing2.CustomFurnitureFactory::new);
        Bukkit.getScheduler().runTaskLater(OraxenPlugin.get(), new Runnable() {
            @Override
            public void run() {
                registerMechanicFactory("furniture", Oraxen_thing2.CustomFurnitureFactory::new);
            }
        }, 100L);
//        System.out.println("WHAT IS HAPPENING OH GOD");


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    static class DurabilityMechanicsManager implements Listener {

        private DurabilityMechanicFactory factory;

        public DurabilityMechanicsManager(DurabilityMechanicFactory factory) {
            this.factory = factory;
        }

        @EventHandler(priority = EventPriority.NORMAL)
        private void onItemDamaged(PlayerInteractEntityEvent event) {
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            Entity entity = event.getRightClicked();
            String itemID = OraxenItems.getIdByItem(item);
            if (factory.isNotImplementedIn(itemID)) {
//                System.out.println("WHAT THE HELL DO YOU MEAN ITS NOT IMPLEMENTED");
                return;
            }
//            System.out.println("START ENTITYYYYYYY CLICK========================================================");
//            System.out.println("WE CLICKED ON A ENTITY "+entity);
            DurabilityMechanic durabilityMechanic = (DurabilityMechanic) factory.getMechanic(itemID);
            FurnitureMechanic furnitureMechanic = OraxenFurniture.getFurnitureMechanic(entity);
            if(furnitureMechanic==null){
//                System.out.println("FURINITURE NULL THERE ENTITY");
                return;
            }
            Entity baseEntity = furnitureMechanic.getBaseEntity(entity);
//            System.out.println("furniture mechanic is "+furnitureMechanic.getItemID()+" base entity is "+baseEntity);
//            System.out.println("STOP ENTITYYYYYYYY CLICK========================================================");
            durabilityMechanic.eat(furnitureMechanic,baseEntity);
        }
        @EventHandler(priority = EventPriority.NORMAL)
        private void onItemDamaged(PlayerInteractEvent event) {
            if(event.getHand()!= EquipmentSlot.HAND) return;
            if(event.getClickedBlock()==null||event.getClickedBlock().getType()!= Material.BARRIER){
                return;
            }
            if(!event.getAction().isRightClick()) return;
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
            String itemID = OraxenItems.getIdByItem(item);
            if (factory.isNotImplementedIn(itemID)) {
//                System.out.println("WHAT THE HELL DO YOU MEAN ITS NOT IMPLEMENTED2");
                return;
            }
//            System.out.println("START BLOCK CLICK========================================================");
//            System.out.println("OROAHDSKFA");
            DurabilityMechanic durabilityMechanic = (DurabilityMechanic) factory.getMechanic(itemID);
            FurnitureMechanic furnitureMechanic = OraxenFurniture.getFurnitureMechanic(event.getClickedBlock());

            if(furnitureMechanic==null){
//                System.out.println("FURNITURE NULL THERE BLOCK");
                return;
            }
            Entity baseEntity = furnitureMechanic.getBaseEntity(event.getClickedBlock());
//            System.out.println("furniture mechanic is "+furnitureMechanic.getItemID()+" base entity is "+baseEntity);
//            System.out.println("END BLOCK CLICK===========================================");
            durabilityMechanic.eat(furnitureMechanic, baseEntity);
        }
    }




    static class DurabilityMechanicFactory extends MechanicFactory {

        public DurabilityMechanicFactory(ConfigurationSection section) {
            super(section);
            System.out.println("PLEASE GOD PLEASE WORK");
            MechanicsManager.registerListeners(OraxenPlugin.get(),new DurabilityMechanicsManager(this));
            System.out.println("MADE A MECHANIC FACTORY");
        }

        @Override
        public Mechanic parse(ConfigurationSection itemMechanicConfiguration) {
            Mechanic mechanic = new DurabilityMechanic(this, itemMechanicConfiguration);
            addToImplemented(mechanic);
            return mechanic;
        }
    }

    static class DurabilityMechanic extends Mechanic {

        private final List<String> furniturez;
        public DurabilityMechanic(MechanicFactory mechanicFactory,
                                  ConfigurationSection section) {
        /* We give:
        - an instance of the Factory which created the mechanic
        - the section used to configure the mechanic
        - the item modifier(s)
         */
            super(mechanicFactory, section, item ->
                    item);
//            System.out.println("MECHANIC CREATED FOR I SCREAM ETERNALLY");
            furniturez = section.getStringList("furniturez");

        }

        public void eat(FurnitureMechanic furnitureMechanic, Entity orig){
            float yaw = orig.getLocation().getYaw();
            if(orig instanceof ItemFrame itemFrame){
                yaw = FurnitureMechanic.rotationToYaw(itemFrame.getRotation());
            }
            if (furnitureMechanic.hasBarriers())
                furnitureMechanic.removeSolid(orig.getWorld(), new BlockLocation(orig.getLocation()), yaw);
            else furnitureMechanic.removeAirFurniture(orig);

            int index = furniturez.indexOf(furnitureMechanic.getItemID());
            if(index!=-1){
                index= (index+1)%furniturez.size();
            }else index = 0;
            FurnitureMechanic mechanic = (FurnitureMechanic) FurnitureFactory.getInstance().getMechanic(furniturez.get(index));
            if (mechanic == null){
                return;
            }
            if(orig instanceof ItemFrame itemFrame){

//                    mechanic.place(orig.getLocation(),yaw, itemFrame.getRotation(), itemFrame.getFacing());
            }else if(orig instanceof ItemDisplay itemDisplay){
//                mechanic.place(orig.getLocation(),yaw, FurnitureMechanic.yawToRotation(yaw), BlockFace.DOWN); //no blockface from itemdisplay
            }
        }
    }
}
