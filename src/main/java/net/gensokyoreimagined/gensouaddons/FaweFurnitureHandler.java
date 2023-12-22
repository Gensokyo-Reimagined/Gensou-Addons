package net.gensokyoreimagined.gensouaddons;

import com.fastasyncworldedit.core.queue.IChunkExtent;
import com.fastasyncworldedit.core.wrappers.WorldWrapper;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.util.nbt.CompoundBinaryTag;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import io.th0rgal.oraxen.shaded.morepersistentdatatypes.DataType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class FaweFurnitureHandler implements Listener {
    private GensouAddons gensouAddons;
    public FaweFurnitureHandler(GensouAddons gensouAddons){
        this.gensouAddons=gensouAddons;
    }

    private static final HashMap<UUID,UUID> translated = new HashMap<>();

    private UUID arrayToUUID(int[] a){
        return new UUID((((long)a[0]) << 32) | (a[1] & 0xffffffffL),(((long)a[2]) << 32) | (a[3] & 0xffffffffL));
    }
    @Subscribe
    public void onEditSession(EditSessionEvent event) {
        if (event.getWorld() != null) {
            if(event.getStage()==EditSession.Stage.BEFORE_CHANGE) {
                translated.clear();
                event.setExtent(new AbstractDelegateExtent(event.getExtent()) {

                    @Nullable
                    @Override
                    public Entity createEntity(Location location, BaseEntity baseEntity) {
                        Entity createdEntity = super.createEntity(location, baseEntity);
                        if (createdEntity != null && baseEntity.getNbt() != null && baseEntity.getNbt().keySet().contains("UUID")) {
                            translated.put(arrayToUUID(baseEntity.getNbt().getIntArray("UUID")),((IChunkExtent.IChunkEntity) createdEntity).uuid());
                        }
                        if (!(createdEntity != null && baseEntity.getNbt() != null && baseEntity.getNbt().keySet().contains("BukkitValues")))
                            return createdEntity;
                        CompoundBinaryTag bukkitValues = baseEntity.getNbt().getCompound("BukkitValues");
                        if (!bukkitValues.keySet().contains(FurnitureMechanic.FURNITURE_KEY.asString()))
                            return createdEntity;

                        BukkitWorld bukkitWorld = (BukkitWorld) WorldWrapper.unwrap(event.getWorld());
                        World world = bukkitWorld.getWorld();

                        String furnitureID = bukkitValues.getString(FurnitureMechanic.FURNITURE_KEY.asString());
                        if (!OraxenItems.exists(furnitureID)) return createdEntity;
                        CustomFurnitureMechanic mechanic = (CustomFurnitureMechanic) FurnitureFactory.getInstance().getMechanic(furnitureID);
                        float yaw = baseEntity.getType().getId().equals("minecraft:item_frame") ? (baseEntity.getNbt().getInt("ItemRotation") * 360.0F / 8.0F) : location.getYaw();
                        UUID uuid = ((IChunkExtent.IChunkEntity) createdEntity).uuid();
                        Vector offset = (new Vector(location.getX(), location.getY() - 0.5, location.getZ())).subtract(new Vector(location.getX(), location.getBlockY(), location.getZ()));
                        Bukkit.getScheduler().runTaskLater(gensouAddons, () -> {
                            mechanic.setBarrierHitbox(uuid, (new org.bukkit.Location(world, location.getX(), location.getY(), location.getZ())), yaw, true, offset, false);
                        }, 0);
                        return createdEntity;
                    }
                });
            }
        }
    }
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event){
        PersistentDataContainer data = event.getEntity().getPersistentDataContainer();
        if(data.has(FurnitureMechanic.BASE_ENTITY_KEY)){
            UUID uuid = data.get(FurnitureMechanic.BASE_ENTITY_KEY, DataType.UUID);
            if(translated.containsKey(uuid)){
                data.set(FurnitureMechanic.BASE_ENTITY_KEY,DataType.UUID,translated.get(uuid));
            }
        }
        if(data.has(FurnitureMechanic.INTERACTION_KEY)){
            UUID uuid = data.get(FurnitureMechanic.INTERACTION_KEY, DataType.UUID);
            if(translated.containsKey(uuid)){
                data.set(FurnitureMechanic.INTERACTION_KEY,DataType.UUID,translated.get(uuid));
            }
        }
    }
}
