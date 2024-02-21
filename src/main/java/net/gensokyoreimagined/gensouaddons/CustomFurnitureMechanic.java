package net.gensokyoreimagined.gensouaddons;

//import com.jeff_media.morepersistentdatatypes.DataType;
import io.th0rgal.oraxen.compatibilities.provided.lightapi.WrappedLightAPI;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.BlockLocation;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.DisplayEntityProperties;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
//import io.th0rgal.oraxen.mechanics.provided.gameplay.light.LightMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.limitedplacing.LimitedPlacing;
import io.th0rgal.oraxen.shaded.jeff_media.morepersistentdatatypes.DataType;
import io.th0rgal.oraxen.utils.BlockHelpers;
import io.th0rgal.oraxen.utils.EntityUtils;
import io.th0rgal.oraxen.utils.ItemUtils;
import io.th0rgal.oraxen.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Slab;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CustomFurnitureMechanic extends FurnitureMechanic {
    public CustomFurnitureMechanic(MechanicFactory mechanicFactory, ConfigurationSection section) {
        super(mechanicFactory, section);
    }
    private static final Map<String, Method> methodz = new HashMap<>();
    private static final Map<String, Field> fieldz = new HashMap<>();
    private Object refl(String methodName, Object... args){
        try {
            if(methodz.containsKey(methodName)){
                return methodz.get(methodName).invoke(this,args);
            }

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
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Object fld(String fieldName){
        return fld(FurnitureMechanic.class,this,fieldName);
    }
    private Object fld(Class<?> clazz, Object instance, String fieldName){
        try {
            if(fieldz.containsKey(fieldName)) return fieldz.get(fieldName).get(instance);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            fieldz.put(fieldName,field);
            return field.get(instance);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Entity place(Location location, ItemStack originalItem, Float yaw, BlockFace facing, boolean checkSpace) {
        if (!location.isWorldLoaded()) {
            return null;
        } else if (checkSpace && this.notEnoughSpace(yaw, location)) {
            return null;
        } else {
            assert location.getWorld() != null;

            this.setPlacedItem();

            assert location.getWorld() != null;

            Class<? extends Entity> entityClass = this.getFurnitureEntityType().getEntityClass();
            if (entityClass == null) {
                entityClass = ItemFrame.class;
            }

            ItemStack item;
            if (fld("evolvingFurniture") == null) {
                item = ItemUtils.editItemMeta(originalItem.clone(), (meta) -> {
                    meta.setDisplayName("");
                });
            } else {
                item = (ItemStack) fld("placedItem");
            }

            item.setAmount(1);
            Entity baseEntity = EntityUtils.spawnEntity((Location) refl("correctedSpawnLocation", location, facing), entityClass, (e) -> {
                this.setEntityData(e, yaw, item, facing);
            });
            if (this.isModelEngine() && PluginUtils.isEnabled("ModelEngine")) {
                refl("spawnModelEngineFurniture", baseEntity);
            }

            return baseEntity;
        }
    }

    @Override
    public void setEntityData(Entity entity, float yaw, ItemStack item, BlockFace facing) {
        if (entity instanceof ItemDisplay itemDisplay) {
            refl("setBaseFurnitureData",entity);
            Location location = entity.getLocation();
            float width;
            float height;
            Vector offset = new Vector();

            if ((facing.isCartesian()) && ((entity instanceof Display))) {
                if (location.clone().add(facing.getOppositeFace().getDirection()).getBlock().getBlockData() instanceof Slab slab) {
                    if (slab.getType() == Slab.Type.BOTTOM && facing.getModY() == 1) {
                        offset = new Vector(0, -0.5, 0);
                    }
                    if (slab.getType() == Slab.Type.TOP && facing.getModY() == -1) {
                        offset = new Vector(0, 0.5, 0);
                    }
                }
            }
            entity.teleport(entity.getLocation().clone().add(offset));
            refl("setItemDisplayData",itemDisplay, item, yaw, getDisplayEntityProperties());

            width = this.hasHitbox() ? getHitbox().width() : getDisplayEntityProperties().getDisplayWidth();
            height = this.hasHitbox() ? getHitbox().height() : getDisplayEntityProperties().getDisplayHeight();
            boolean isFixed = getDisplayEntityProperties().getDisplayTransform() == ItemDisplay.ItemDisplayTransform.FIXED;
            Location interactionLoc = location.clone().subtract(0.0, this.hasLimitedPlacing() && getLimitedPlacing().isRoof() && isFixed ? 1.5 * (double) (height - 1.0F) : 0.0, 0.0);
            Interaction interaction = (Interaction) refl("spawnInteractionEntity",itemDisplay, interactionLoc.clone().add(offset), width, height);
//            Interaction interaction = (Interaction) refl("spawnInteractionEntity",itemDisplay, interactionLoc, width, height);
            Location barrierLoc = EntityUtils.isNone(itemDisplay) && getDisplayEntityProperties().hasScale() ? location.clone().subtract(0.0, 0.5 * getDisplayEntityProperties().getScale().y(), 0.0) : location;
            if (this.hasBarriers()) {
                this.setBarrierHitbox(entity, barrierLoc, yaw, offset, true);
                //refl("setBarrierHitbox",entity, barrierLoc, yaw, true);
            } else if (this.hasSeat() && interaction != null) {
                UUID seatUuid = (UUID) refl("spawnSeat",location.getBlock(), (boolean)fld("hasSeatYaw") ? fld("seatYaw") : yaw);
                interaction.getPersistentDataContainer().set(SEAT_KEY, DataType.UUID, seatUuid);
                itemDisplay.getPersistentDataContainer().set(SEAT_KEY, DataType.UUID, seatUuid);
                //location.getWorld().getEntity(seatUuid).teleport(location.getWorld().getEntity(seatUuid).getLocation().clone().add(offset));
            }

            if ((int)fld("light") != -1) {
                WrappedLightAPI.createBlockLight(location, (int)fld("light"));
            }
//            if (getLight().hasLightLevel()) {
//                getLight().createBlockLight(location.getBlock());
//            }
        }else{
            super.setEntityData(entity,yaw,item,facing);
        }
    }
    public void setBarrierHitbox(Entity entity, Location location, float yaw, Vector offset, boolean handleLight){
        List<Location> barrierLocations = this.getLocations(yaw, BlockHelpers.toCenterBlockLocation(location), (List<BlockLocation>) fld("barriers"));

        for(Location barrierLocation: barrierLocations) {
            Block block = barrierLocation.getBlock();
            block.setType(Material.BARRIER);
            PersistentDataContainer data = BlockHelpers.getPDC(block);
            data.set(FURNITURE_KEY, PersistentDataType.STRING, this.getItemID());
            if ((boolean)fld("hasSeat")) {
                UUID uuid = (UUID) refl("spawnSeat",block, (boolean)fld("hasSeatYaw") ? fld("seatYaw") : yaw);
                location.getWorld().getEntity(uuid).teleport(location.getWorld().getEntity(uuid).getLocation().clone().add(offset));
                data.set(SEAT_KEY, DataType.UUID, uuid);
            }

            data.set(ROOT_KEY, PersistentDataType.STRING, (new BlockLocation(location.clone())).toString());
            data.set(ORIENTATION_KEY, PersistentDataType.FLOAT, yaw);
            data.set(BASE_ENTITY_KEY, DataType.UUID, entity.getUniqueId());
            if (handleLight && ((int)fld("light")) != -1) {
                WrappedLightAPI.createBlockLight(barrierLocation, ((int)fld("light")));
            }
//            if (((LightMechanic)fld("light")).hasLightLevel()) {
//                ((LightMechanic)fld("light")).createBlockLight(block);
//            }
        }
    }
}
