package net.gensokyoreimagined.gensouaddons;

import com.jeff_media.morepersistentdatatypes.DataType;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.BlockLocation;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import io.th0rgal.oraxen.mechanics.provided.gameplay.light.LightMechanic;
import io.th0rgal.oraxen.utils.BlockHelpers;
import io.th0rgal.oraxen.utils.EntityUtils;
import io.th0rgal.oraxen.utils.ItemUtils;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        } catch (IllegalAccessException | InvocationTargetException e) {
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
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Entity place(Location location, ItemStack originalItem, Float yaw, BlockFace facing,boolean checkSpace) {
        if (!location.isWorldLoaded()) {
            return null;
        } else if (checkSpace&&this.notEnoughSpace(yaw, location)) {
            return null;
        } else {
            assert location.getWorld() != null;

            assert location.getWorld() != null;

            Class<? extends Entity> entityClass = this.getFurnitureEntityType().getEntityClass();
            if (entityClass == null) {
                entityClass = ItemFrame.class;
            }

            //ItemStack item;
            ItemStack item = OraxenItems.getOptionalItemById((String)fld("placedItemId")).map(b -> b.build().clone()).orElse(originalItem);
            if(this.getEvolution()==null){
                ItemUtils.editItemMeta(item, meta -> meta.setDisplayName(""));
            }

            item.setAmount(1);
            Entity baseEntity = (Entity) EntityUtils.spawnEntity(
                    (Location)refl("correctedSpawnLocation",location,facing), entityClass, entity -> {
                        Vector offset=new Vector();

                        if((facing.isCartesian())&&((entity instanceof Display))){
                            if(location.clone().add(facing.getOppositeFace().getDirection()).getBlock().getBlockData() instanceof Slab slab){
                                if(slab.getType()== Slab.Type.BOTTOM&&facing.getModY()==1){
                                    offset=new Vector(0,-0.5,0);
                                }
                                if(slab.getType()== Slab.Type.TOP&&facing.getModY()==-1){
                                    offset=new Vector(0,0.5,0);
                                }
                            }
                        }
                        Location loc = entity.getLocation();

                        refl("setBaseFurnitureData",entity);
                        if (entity instanceof ItemFrame frame) {
                            refl("setFrameData",frame, item, yaw, facing);
                            if (this.hasBarriers()) {
                                setBarrierHitbox(entity, loc, yaw, true, offset);
                            } else {
                                float width = this.hasHitbox() ? getHitbox().width() : 1.0F;
                                float height = this.hasHitbox() ? getHitbox().height() : 1.0F;
                                Entity interaction = (Entity) refl("spawnInteractionEntity",frame, loc, width, height);
                                Block block = loc.getBlock();
                                if (this.hasSeat() && interaction != null) {
                                    interaction.teleport(interaction.getLocation().clone().add(offset));
                                    UUID seatUuid = (UUID) refl("spawnSeat",block, (boolean)fld("hasSeatYaw") ? fld("seatYaw") : getFurnitureYaw(frame));
                                    interaction.getPersistentDataContainer().set(SEAT_KEY, DataType.UUID, seatUuid);
                                    frame.getPersistentDataContainer().set(SEAT_KEY, DataType.UUID, seatUuid);
                                    loc.getWorld().getEntity(seatUuid).teleport(loc.getWorld().getEntity(seatUuid).getLocation().clone().add(offset));
                                }

                                if(getLight().hasLightLevel()){
                                    getLight().createBlockLight(block);
                                }
                            }
                        } else if (entity instanceof ItemDisplay itemDisplay) {
                            refl("setItemDisplayData",entity, item, yaw, getDisplayEntityProperties());
                            float width = this.hasHitbox() ? getHitbox().width() : getDisplayEntityProperties().getDisplayWidth();
                            float height = this.hasHitbox() ? getHitbox().height() : getDisplayEntityProperties().getDisplayHeight();
                            Interaction interaction = (Interaction) refl("spawnInteractionEntity",entity, loc, width, height);
                            if (this.hasBarriers()) {
                                setBarrierHitbox(entity, loc, yaw, false,offset);
                            } else if (this.hasSeat() && interaction != null) {
                                interaction.teleport(interaction.getLocation().clone().add(offset));
                                UUID seatUuid = (UUID) refl("spawnSeat",loc.getBlock(), (boolean)fld("hasSeatYaw") ? fld("seatYaw") : yaw);
                                interaction.getPersistentDataContainer().set(SEAT_KEY, DataType.UUID, seatUuid);
                                itemDisplay.getPersistentDataContainer().set(SEAT_KEY, DataType.UUID, seatUuid);
                                loc.getWorld().getEntity(seatUuid).teleport(loc.getWorld().getEntity(seatUuid).getLocation().clone().add(offset));
                            }

                            if(getLight().hasLightLevel()){
                                getLight().createBlockLight(loc.getBlock());
                            }
                        }
                        entity.teleport(entity.getLocation().clone().add(offset));
                    }
            );
            if (this.isModelEngine() && Bukkit.getPluginManager().isPluginEnabled("ModelEngine")) {
                refl("spawnModelEngineFurniture",baseEntity);
            }
            return baseEntity;
        }
    }

    public void setBarrierHitbox(Entity entity, Location location, float yaw, boolean handleLight, Vector offset){
        setBarrierHitbox(entity.getUniqueId(),location,yaw,handleLight,offset, true);
    }
    public void setBarrierHitbox(UUID entityUUID, Location location, float yaw, boolean handleLight, Vector offset, boolean place){
        for (Location barrierLocation : this.getLocations(yaw, BlockHelpers.toCenterBlockLocation(location), this.getBarriers())) {
            Block block = barrierLocation.getBlock();
            if(place) block.setType(Material.BARRIER);

            PersistentDataContainer data = BlockHelpers.getPDC(block);
            data.set(FURNITURE_KEY, PersistentDataType.STRING, this.getItemID());
            if ((boolean)fld("hasSeat")) {
                UUID uuid = (UUID) refl("spawnSeat",block, (boolean)fld("hasSeatYaw") ? fld("seatYaw") : yaw);
                data.set(SEAT_KEY, DataType.UUID, uuid);
                location.getWorld().getEntity(uuid).teleport(location.getWorld().getEntity(uuid).getLocation().clone().add(offset));
            }

            data.set(ROOT_KEY, PersistentDataType.STRING, new BlockLocation(location.clone()).toString());
            data.set(ORIENTATION_KEY, PersistentDataType.FLOAT, yaw);
            data.set(BASE_ENTITY_KEY, DataType.UUID, entityUUID);
            if(getLight().hasLightLevel()){
                getLight().createBlockLight(block);
            }
        }
    }
}
