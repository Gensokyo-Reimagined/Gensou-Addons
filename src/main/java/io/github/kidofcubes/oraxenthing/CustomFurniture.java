//package io.github.kidofcubes.oraxenthing;
//
//import io.th0rgal.oraxen.OraxenPlugin;
//import io.th0rgal.oraxen.compatibilities.provided.lightapi.WrappedLightAPI;
//import io.th0rgal.oraxen.mechanics.Mechanic;
//import io.th0rgal.oraxen.mechanics.MechanicFactory;
//import io.th0rgal.oraxen.mechanics.MechanicsManager;
//import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.*;
//import io.th0rgal.oraxen.shaded.morepersistentdatatypes.DataType;
//import io.th0rgal.oraxen.utils.BlockHelpers;
//import net.kyori.adventure.text.Component;
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.block.Block;
//import org.bukkit.block.BlockFace;
//import org.bukkit.configuration.ConfigurationSection;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.Interaction;
//import org.bukkit.entity.ItemDisplay;
//import org.bukkit.entity.ItemFrame;
//import org.bukkit.event.HandlerList;
//import org.bukkit.event.Listener;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.ItemMeta;
//import org.bukkit.persistence.PersistentDataContainer;
//import org.bukkit.persistence.PersistentDataType;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//public class CustomFurniture {
//
//    public static class CustomFurnitureFactory extends FurnitureFactory {
//
//        public CustomFurnitureFactory(ConfigurationSection section) {
//            super(section);
//            try {
//                Field field = MechanicsManager.class.getDeclaredField("MECHANICS_LISTENERS");
//                field.setAccessible(true);
//                ((List<Listener>)field.get(null)).forEach(listener -> {
//                    if(listener instanceof FurnitureListener){
//                        HandlerList.unregisterAll(listener);
//                    }
//                });
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//            MechanicsManager.registerListeners(OraxenPlugin.get(),
//                    new FurnitureListener(this)
//            );
//        }
//
//        @Override
//        public Mechanic parse(ConfigurationSection itemMechanicConfiguration) {
//            Mechanic mechanic = new CustomFurnitureMechanic(this, itemMechanicConfiguration);
//            addToImplemented(mechanic);
//            return mechanic;
//        }
//    }
//
//
//
//    public static class CustomFurnitureMechanic extends FurnitureMechanic{
//        public CustomFurnitureMechanic(MechanicFactory mechanicFactory, ConfigurationSection section) {
//            super(mechanicFactory, section);
//        }
//
//        @Override
//        public Entity getBaseEntity(Block block) {
//            PersistentDataContainer pdc = BlockHelpers.getPDC(block);
//            if (pdc.isEmpty()) return null;
//            UUID baseEntityUUID = pdc.get(BASE_ENTITY_KEY, DataType.UUID);
//            return baseEntityUUID != null && Bukkit.getEntity(baseEntityUUID) != null ? Bukkit.getEntity(baseEntityUUID) : null;
//        }
//        private static final Map<String, Method> methodz = new HashMap<>();
//        private static final Map<String, Field> fieldz = new HashMap<>();
//        private Object refl(String methodName, Object... args){
//            try {
//                if(methodz.containsKey(methodName)) return methodz.get(methodName).invoke(this,args);
//
//                Method[] methods = FurnitureMechanic.class.getDeclaredMethods();
//                Method method = null;
//                for (Method metod : methods) {
//                    if(metod.getName().equals(methodName)){
//                        method=metod;
//                        break;
//                    }
//                }
//                if(method==null) throw new RuntimeException(new NoSuchMethodException("no method "+methodName));
//
//                method.setAccessible(true);
//                methodz.put(methodName,method);
//                return method.invoke(this,args);
//            } catch (IllegalAccessException | InvocationTargetException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        private Object fld(String fieldName){
//            return fld(FurnitureMechanic.class,this,fieldName);
//        }
//        private Object fld(Class<?> clazz, Object instance, String fieldName){
//            try {
//                if(fieldz.containsKey(fieldName)) return fieldz.get(fieldName).get(instance);
//                Field field = clazz.getDeclaredField(fieldName);
//                field.setAccessible(true);
//                fieldz.put(fieldName,field);
//                return field.get(instance);
//            } catch (IllegalAccessException | NoSuchFieldException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        @Override
//        public Entity place(Location location, ItemStack originalItem, Float yaw, BlockFace facing) {
//            if (!location.isWorldLoaded()) return null;
//            if (this.notEnoughSpace(yaw, location)) return null;
//            assert location.getWorld() != null;
//            setPlacedItem();
//            assert location.getWorld() != null;
//
//            Class<? extends Entity> entityClass = getFurnitureEntityType().getEntityClass();
//            if (entityClass == null) entityClass = ItemFrame.class;
//
//            ItemStack item;
//            if (fld("evolvingFurniture") == null) {
//                ItemStack clone = originalItem.clone();
//                ItemMeta meta = clone.getItemMeta();
//                if (meta != null) meta.displayName(Component.empty());
//                clone.setItemMeta(meta);
//                item = clone;
//            } else item = (ItemStack) fld("placedItem");
//
//            Entity baseEntity = location.getWorld().spawn(BlockHelpers.toCenterBlockLocation(location), entityClass, (entity) ->
//                    setEntityData2(entity, yaw, item, facing));
//
//            if (this.isModelEngine() && Bukkit.getPluginManager().isPluginEnabled("ModelEngine")) {
//                refl("spawnModelEngineFurniture", baseEntity, yaw);
//            }
//
//            return baseEntity;
//        }
//
//        public void setEntityData2(Entity entity, float yaw, ItemStack item, BlockFace facing){
//            refl("setBaseFurnitureData",entity);
//            PersistentDataContainer pdc = entity.getPersistentDataContainer();
//            pdc.set(ROOT_KEY, PersistentDataType.STRING, new BlockLocation(entity.getLocation().clone()).toString());
//            if (entity instanceof ItemFrame frame) {
//                refl("setFrameData",frame, item, yaw, facing);
//                Location location = entity.getLocation();
//
//                if (hasBarriers()) setBarrierHitbox2(entity, location, yaw, true);
//                else {
//                    float width = hasHitbox() ? (float) fld(FurnitureHitbox.class,fld("hitbox"),"width") : 1f;
//                    float height = hasHitbox() ? (float) fld(FurnitureHitbox.class,fld("hitbox"),"height") : 1f;
//                    refl("spawnInteractionEntity",entity, location, width, height, true);
//
//                    Block block = location.getBlock();
//                    if (hasSeat()) {
//                        UUID entityId = (UUID) refl("spawnSeat",this, block, (boolean)fld("hasSeatYaw") ? fld("seatYaw") : location.getYaw());
//                        if (entityId != null) BlockHelpers.getPDC(block).set(SEAT_KEY, DataType.UUID, entityId);
//                    }
//                    if ((int)fld("light") != -1) {
//                        WrappedLightAPI.createBlockLight(location, (int)fld("light"));
//                    }
//                }
//            } else if (entity instanceof ItemDisplay itemDisplay) {
//                //Correct FIXED item display yaw until 1.20 fixes this
//                refl("setItemDisplayData",itemDisplay, item, yaw, fld("displayEntityProperties"));
//                Location location = itemDisplay.getLocation();
//                float width = hasHitbox() ? (float) fld(FurnitureHitbox.class, fld("hitbox"),"width") : ((DisplayEntityProperties)fld("displayEntityProperties")).getDisplayWidth();
//                float height = hasHitbox() ? (float) fld(FurnitureHitbox.class,fld("hitbox"),"height") : ((DisplayEntityProperties)fld("displayEntityProperties")).getDisplayHeight();
//                Interaction interaction = (Interaction) refl("spawnInteractionEntity", itemDisplay, location, width, height, ((DisplayEntityProperties)fld("displayEntityProperties")).isInteractable());
//
//                if (hasBarriers()) setBarrierHitbox2(entity, location, yaw, false);
//                else if (hasSeat()) {
//                    UUID entityId = (UUID) refl("spawnSeat",this, location.getBlock(), (boolean)fld("hasSeatYaw") ? fld("seatYaw") : location.getYaw());
//                    if (entityId != null && interaction != null)
//                        interaction.getPersistentDataContainer().set(SEAT_KEY, DataType.UUID, entityId);
//                }
//            }
//        }
//        void setBarrierHitbox2(Entity entity, Location location, float yaw, boolean handleLight) {
//            for (Location barrierLocation : getLocations(yaw, BlockHelpers.toCenterBlockLocation(location), getBarriers())) {
//                Block block = barrierLocation.getBlock();
//                PersistentDataContainer data = BlockHelpers.getPDC(block);
//                data.set(FURNITURE_KEY, PersistentDataType.STRING, getItemID());
//                if (hasSeat()) {
//                    UUID entityId = (UUID)refl("spawnSeat",this, block, (boolean)fld("hasSeatYaw") ? fld("seatYaw") : yaw);
//                    if (entityId != null) data.set(SEAT_KEY, DataType.UUID, entityId);
//                }
//                data.set(ROOT_KEY, PersistentDataType.STRING, new BlockLocation(location.clone()).toString());
//                data.set(BASE_ENTITY_KEY, DataType.UUID, entity.getUniqueId());
//                data.set(ORIENTATION_KEY, PersistentDataType.FLOAT, yaw);
//                block.setType(Material.BARRIER);
//                if (handleLight && (int)fld("light") != -1)
//                    WrappedLightAPI.createBlockLight(barrierLocation, (int)fld("light"));
//            }
//        }
//    }
//}
