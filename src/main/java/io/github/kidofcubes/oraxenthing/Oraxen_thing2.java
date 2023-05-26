package io.github.kidofcubes.oraxenthing;

import io.th0rgal.oraxen.OraxenPlugin;
import io.th0rgal.oraxen.api.OraxenFurniture;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.api.events.OraxenFurniturePlaceEvent;
import io.th0rgal.oraxen.compatibilities.provided.lightapi.WrappedLightAPI;
import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.*;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.evolution.EvolutionListener;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.jukebox.JukeboxListener;
import io.th0rgal.oraxen.shaded.morepersistentdatatypes.DataType;
import io.th0rgal.oraxen.utils.BlockHelpers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static io.th0rgal.oraxen.mechanics.MechanicsManager.registerMechanicFactory;

public class Oraxen_thing2 {


    static class CustomFurnitureFactory extends FurnitureFactory{

        public CustomFurnitureFactory(ConfigurationSection section) {
            super(section);
            instance = this;
            System.out.println("MADE A CUSTOM FURNITURE FACTORY");
            System.out.println("MADE A CUSTOM FURNITURE FACTORY");
            System.out.println("MADE A CUSTOM FURNITURE FACTORY");
            System.out.println("MADE A CUSTOM FURNITURE FACTORY");
            System.out.println("MADE A CUSTOM FURNITURE FACTORY");
            try {
                Field field = MechanicsManager.class.getDeclaredField("MECHANICS_LISTENERS");
                field.setAccessible(true);
                ((List<Listener>)field.get(null)).forEach(listener -> {
                    if(listener instanceof FurnitureListener furnitureListener){
                        HandlerList.unregisterAll(listener);
                    }
                });
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            MechanicsManager.registerListeners(OraxenPlugin.get(),
                    new FurnitureListener(this)
            );
            System.out.println("MADE A CUSTOM FURNITURE FACTORY");
            System.out.println("MADE A CUSTOM FURNITURE FACTORY");
            System.out.println("MADE A CUSTOM FURNITURE FACTORY");
            System.out.println("MADE A CUSTOM FURNITURE FACTORY");
        }

        @Override
        public Mechanic parse(ConfigurationSection itemMechanicConfiguration) {
            Mechanic mechanic = new CustomFurnitureMechanic(this, itemMechanicConfiguration);
            addToImplemented(mechanic);
            System.out.println("CUSTOM FACTORY GO ");
            return mechanic;
        }
    }

//    public static class FurnitureListener2 extends FurnitureListener{
//
//        public FurnitureListener2(MechanicFactory factory) {
//            super(factory);
//            System.out.println("IT IS ME I GOT REGISTERED OMG");
//        }
//    }

    static class CustomFurnitureMechanic extends FurnitureMechanic{

        public CustomFurnitureMechanic(MechanicFactory mechanicFactory, ConfigurationSection section) {
            super(mechanicFactory, section);
            System.out.println("CUSTOM FURNITURE GO");
            System.out.println("CUSTOM FURNITURE GO");
            System.out.println("CUSTOM FURNITURE GO");
            System.out.println("CUSTOM FURNITURE GO");
            System.out.println("CUSTOM FURNITURE GO");
            System.out.println("CUSTOM FURNITURE GO");
            System.out.println("CUSTOM FURNITURE GO");
            System.out.println("CUSTOM FURNITURE GO");
            System.out.println("CUSTOM FURNITURE GO");
            System.out.println("CUSTOM FURNITURE GO");
            System.out.println("CUSTOM FURNITURE GO");
            System.out.println("CUSTOM FURNITURE GO");
            System.out.println("CUSTOM FURNITURE GO");
            System.out.println("CUSTOM FURNITURE GO");
        }

        @Override
        public Entity getBaseEntity(Block block) {
            PersistentDataContainer pdc = BlockHelpers.getPDC(block);
            System.out.println("WE ARE GETTING BASE ENTITY OF A BLOCK");
            if (pdc.isEmpty()) return null;
            UUID baseEntityUUID = pdc.get(BASE_ENTITY_KEY, DataType.UUID);
            System.out.println("was it null? "+(baseEntityUUID==null));
            return baseEntityUUID != null && Bukkit.getEntity(baseEntityUUID) != null ? Bukkit.getEntity(baseEntityUUID) : null;
        }

//        @Override
//        public Entity getBaseEntity(Entity entity) {
//            return super.getBaseEntity(entity);
//        }
        private static Map<String, Method> methodz = new HashMap<>();
        private static Map<String, Field> fieldz = new HashMap<>();
        private Object refl(String methodName, Object... args){
            try {
                if(methodz.containsKey(methodName)) return methodz.get(methodName).invoke(this,args);

                Method[] methods = FurnitureMechanic.class.getDeclaredMethods();
                Method method = null;
                for (Method metod : methods) {
                    if(metod.getName().equals(methodName)){
                        method=metod;
                        break;
                    }
                }
                if(method==null) throw new RuntimeException(new NoSuchMethodException("no method "+methodName));

                method.setAccessible(true);
                methodz.put(methodName,method);
                return method.invoke(this,args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        private Object fld(String fieldName){
            return fld(FurnitureMechanic.class,this,fieldName);
        }
        private Object fld(Class clazz, Object instance, String fieldName){
            try {
                if(fieldz.containsKey(fieldName)) return fieldz.get(fieldName).get(instance);
                System.out.println("DECLARED FIELDS ARE ");
                for(Field field : clazz.getDeclaredFields()){
                    System.out.println(field.getType()+" "+field.getName());
                }
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                fieldz.put(fieldName,field);
                return field.get(instance);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Entity place(Location location, ItemStack originalItem, Float yaw, BlockFace facing) {
            System.out.println("PLEASE PLACE I AM A "+this.getClass());
            if (!location.isWorldLoaded()) return null;
            if (this.notEnoughSpace(yaw, location)) return null;
            assert location.getWorld() != null;
            setPlacedItem();
            assert location.getWorld() != null;
            System.out.println("START PLACINGGGGGGGG");

            Class<? extends Entity> entityClass = getFurnitureEntityType().getEntityClass();
            if (entityClass == null) entityClass = ItemFrame.class;

            ItemStack item;
            if (fld("evolvingFurniture") == null) {
                ItemStack clone = originalItem.clone();
                ItemMeta meta = clone.getItemMeta();
                if (meta != null) meta.setDisplayName("");
                clone.setItemMeta(meta);
                item = clone;
            } else item = (ItemStack) fld("placedItem");

            System.out.println("SETTING ENTITYE DATA");
            Entity baseEntity = location.getWorld().spawn(BlockHelpers.toCenterBlockLocation(location), entityClass, (entity) ->
                    setEntityData2(entity, yaw, item, facing));

            if (this.isModelEngine() && Bukkit.getPluginManager().isPluginEnabled("ModelEngine")) {
                refl("spawnModelEngineFurniture", baseEntity, yaw);
            }

            return baseEntity;
        }

        public void setEntityData2(Entity entity, float yaw, ItemStack item, BlockFace facing){
            refl("setBaseFurnitureData",entity);
            if (entity instanceof ItemFrame frame) {
                refl("setFrameData",frame, item, yaw, facing);
                Location location = entity.getLocation();

                System.out.println("BARRIER TIME");
                if (hasBarriers()) setBarrierHitbox2(entity, location, yaw, true);
                else {
                    float width = hasHitbox() ? (float) fld(FurnitureHitbox.class,fld("hitbox"),"width") : 1f;
                    float height = hasHitbox() ? (float) fld(FurnitureHitbox.class,fld("hitbox"),"height") : 1f;
                    refl("spawnInteractionEntity",entity, location, width, height, true);

                    Block block = location.getBlock();
                    if (hasSeat()) {
                        UUID entityId = (UUID) refl("spawnSeat",this, block, (boolean)fld("hasSeatYaw") ? fld("seatYaw") : location.getYaw());
                        if (entityId != null) BlockHelpers.getPDC(block).set(SEAT_KEY, DataType.UUID, entityId);
                    }
                    if ((int)fld("light") != -1) {
                        WrappedLightAPI.createBlockLight(location, (int)fld("light"));
                    }
                }
            } else if (entity instanceof ItemDisplay itemDisplay) {
                //Correct FIXED item display yaw until 1.20 fixes this
                refl("setItemDisplayData",itemDisplay, item, yaw, fld("displayEntityProperties"));
                Location location = itemDisplay.getLocation();
                float width = hasHitbox() ? (float) fld(FurnitureHitbox.class, fld("hitbox"),"width") : ((DisplayEntityProperties)fld("displayEntityProperties")).getDisplayWidth();
                float height = hasHitbox() ? (float) fld(FurnitureHitbox.class,fld("hitbox"),"height") : ((DisplayEntityProperties)fld("displayEntityProperties")).getDisplayHeight();
                Interaction interaction = (Interaction) refl("spawnInteractionEntity", itemDisplay, location, width, height, ((DisplayEntityProperties)fld("displayEntityProperties")).isInteractable());

                System.out.println("BARRIER TIME2");
                if (hasBarriers()) setBarrierHitbox2(entity, location, yaw, false);
                else if (hasSeat()) {
                    UUID entityId = (UUID) refl("spawnSeat",this, location.getBlock(), (boolean)fld("hasSeatYaw") ? fld("seatYaw") : location.getYaw());
                    if (entityId != null && interaction != null)
                        interaction.getPersistentDataContainer().set(SEAT_KEY, DataType.UUID, entityId);
                }
            }
        }
        void setBarrierHitbox2(Entity entity, Location location, float yaw, boolean handleLight) {
            for (Location barrierLocation : getLocations(yaw, BlockHelpers.toCenterBlockLocation(location), getBarriers())) {
                Block block = barrierLocation.getBlock();
                PersistentDataContainer data = BlockHelpers.getPDC(block);
                data.set(FURNITURE_KEY, PersistentDataType.STRING, getItemID());
                if (hasSeat()) {
                    UUID entityId = (UUID)refl("spawnSeat",this, block, (boolean)fld("hasSeatYaw") ? (float) fld("seatYaw") : yaw);
                    if (entityId != null) data.set(SEAT_KEY, DataType.UUID, entityId);
                }
                data.set(ROOT_KEY, PersistentDataType.STRING, new BlockLocation(location.clone()).toString());
                data.set(BASE_ENTITY_KEY, DataType.UUID, entity.getUniqueId());
                System.out.println("SET THE BASE ENTITY KEY ");
                System.out.println("SET THE BASE ENTITY KEY ");
                System.out.println("SET THE BASE ENTITY KEY ");
                System.out.println("SET THE BASE ENTITY KEY ");
                System.out.println("SET THE BASE ENTITY KEY ");
                System.out.println("SET THE BASE ENTITY KEY ");
                System.out.println("SET THE BASE ENTITY KEY ");
                System.out.println("SET THE BASE ENTITY KEY ");
                System.out.println("SET THE BASE ENTITY KEY ");
                System.out.println("SET THE BASE ENTITY KEY ");
                System.out.println("SET THE BASE ENTITY KEY ");
                System.out.println("SET THE BASE ENTITY KEY ");
                data.set(ORIENTATION_KEY, PersistentDataType.FLOAT, yaw);
                block.setType(Material.BARRIER);
                if (handleLight && (int)fld("light") != -1)
                    WrappedLightAPI.createBlockLight(barrierLocation, (int)fld("light"));
            }
        }
    }


    static class RandomMechanicsManager implements Listener {

        private RandomMechanicFactory factory;

        public RandomMechanicsManager(RandomMechanicFactory factory) {
            this.factory = factory;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPlace(OraxenFurniturePlaceEvent event) {
            ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

            Entity entity = event.getBaseEntity();
            String itemID = OraxenItems.getIdByItem(item);
            if (factory.isNotImplementedIn(itemID)) {
                System.out.println("WHAT THE HELL DO YOU MEAN ITS NOT IMPLEMENTED");
                return;
            }
            System.out.println("START ENTITYYYYYYY CLICK========================================================");
            System.out.println("WE CLICKED ON A ENTITY "+entity);
            RandomMechanic durabilityMechanic = (RandomMechanic) factory.getMechanic(itemID);
            FurnitureMechanic furnitureMechanic = OraxenFurniture.getFurnitureMechanic(entity);
            if(furnitureMechanic==null){
                System.out.println("FURINITURE NULL THERE ENTITY");
                return;
            }
            Entity baseEntity = furnitureMechanic.getBaseEntity(entity);
            System.out.println("furniture mechanic is "+furnitureMechanic.getClass()+" base entity is "+baseEntity);
            System.out.println("STOP ENTITYYYYYYYY CLICK========================================================");
            durabilityMechanic.eat(event,furnitureMechanic,entity);
        }

        @EventHandler
        public void onMorb(PlayerDropItemEvent chatEvent){
            System.out.println("WOOP WOOP");
            chatEvent.getPlayer().getLocation().getNearbyEntities(5,5,5).forEach(entity -> {
                System.out.println("MOVED A "+entity);
                entity.teleport(entity.getLocation().add(0,0.5,0));
            });
            registerMechanicFactory("furniture", Oraxen_thing2.CustomFurnitureFactory::new);
        }
    }




    static class RandomMechanicFactory extends MechanicFactory {

        public RandomMechanicFactory(ConfigurationSection section) {
            super(section);
            System.out.println("PLEASE GOD PLEASE WORK PLEASEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            System.out.println("PLEASE GOD PLEASE WORK PLEASEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            System.out.println("PLEASE GOD PLEASE WORK PLEASEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            System.out.println("PLEASE GOD PLEASE WORK PLEASEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            System.out.println("PLEASE GOD PLEASE WORK PLEASEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            System.out.println("PLEASE GOD PLEASE WORK PLEASEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            System.out.println("PLEASE GOD PLEASE WORK PLEASEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            System.out.println("PLEASE GOD PLEASE WORK PLEASEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            System.out.println("PLEASE GOD PLEASE WORK PLEASEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            System.out.println("PLEASE GOD PLEASE WORK PLEASEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
            MechanicsManager.registerListeners(OraxenPlugin.get(),new RandomMechanicsManager(this));
            System.out.println("MADE A JLAIKSDFHJAOIDFA;LKSD;FJAD FACTORY");
        }

        @Override
        public Mechanic parse(ConfigurationSection itemMechanicConfiguration) {
            Mechanic mechanic = new RandomMechanic(this, itemMechanicConfiguration);
            addToImplemented(mechanic);
            return mechanic;
        }
    }

    static class RandomMechanic extends Mechanic {

        private final List<Vector> furniturez;
        public RandomMechanic(MechanicFactory mechanicFactory,
                                  ConfigurationSection section) {
        /* We give:
        - an instance of the Factory which created the mechanic
        - the section used to configure the mechanic
        - the item modifier(s)
         */
            super(mechanicFactory, section, item ->
                    item);
//            System.out.println("MECHANIC CREATED FOR I SCREAM ETERNALLY");
//            furniturez = section.getStringList("furniturez");
            furniturez = new ArrayList<>();
            section.getStringList("offsets").forEach(text -> {
                furniturez.add(new Vector(Double.parseDouble(text.split(",")[0]),Double.parseDouble(text.split(",")[1]),Double.parseDouble(text.split(",")[2])));
            });

        }

        public void eat(OraxenFurniturePlaceEvent event, FurnitureMechanic furnitureMechanic, Entity orig){

            if(furniturez.size()==0) return;
            Vector move = furniturez.get((int)(Math.random()*furniturez.size()));
            System.out.println("ADDED  "+move+" TO "+orig.getLocation()+" ");
            System.out.println("ORIG IS A "+orig);
            Location result = orig.getLocation().add(move);
            System.out.println("RESULTING IN "+result);
            System.out.println("RESULT WAS "+orig.teleport(result));
            System.out.println("RESULT2 WAS "+event.getBaseEntity().teleport(result));
            System.out.println(" NEW LOCATION IS NOW "+orig.getLocation());
//            Bukkit.getScheduler().runTaskLater(OraxenPlugin.get(), new Runnable() {
//                @Override
//                public void run() {
//                }
//            }, 3L);
        }
    }

}
