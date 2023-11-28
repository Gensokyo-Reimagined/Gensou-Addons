package net.gensokyoreimagined.gensouaddons;

import com.fastasyncworldedit.core.queue.IChunkExtent;
import com.fastasyncworldedit.core.wrappers.WorldWrapper;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.entity.BaseEntity;
import com.sk89q.worldedit.entity.Entity;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.util.nbt.CompoundBinaryTag;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.BlockLocation;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import io.th0rgal.oraxen.shaded.morepersistentdatatypes.DataType;
import io.th0rgal.oraxen.utils.BlockHelpers;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FaweFurnitureHandler {

    @Subscribe
    public void onEditSession(EditSessionEvent event) {

        if (event.getWorld() != null) {
            event.setExtent(new AbstractDelegateExtent(event.getExtent()) {

                @Nullable
                @Override
                public Entity createEntity(Location location, BaseEntity baseEntity) {
                    Entity createdEntity = super.createEntity(location, baseEntity);

                    if(!(createdEntity!=null&&baseEntity.getNbt()!=null&&baseEntity.getNbt().keySet().contains("BukkitValues"))) return createdEntity;
                    CompoundBinaryTag bukkitValues = baseEntity.getNbt().getCompound("BukkitValues");
                    if (!bukkitValues.keySet().contains(FurnitureMechanic.FURNITURE_KEY.asString())) return createdEntity;

                    BukkitWorld bukkitWorld = (BukkitWorld) WorldWrapper.unwrap(event.getWorld());
                    World world = bukkitWorld.getWorld();

                    String furnitureID = bukkitValues.getString(FurnitureMechanic.FURNITURE_KEY.asString());
                    if (!OraxenItems.exists(furnitureID)) return createdEntity;

                    FurnitureMechanic mechanic = (FurnitureMechanic) FurnitureFactory.getInstance().getMechanic(furnitureID);
                    float yaw = baseEntity.getType().getId().equals("minecraft:item_frame") ? (baseEntity.getNbt().getInt("ItemRotation") * 360.0F / 8.0F) : location.getYaw();
                    UUID uuid = ((IChunkExtent.IChunkEntity)createdEntity).uuid();
                    for (org.bukkit.Location barrierLocation : mechanic.getLocations(yaw, new org.bukkit.Location(world,location.getBlockX(),location.getBlockY(),location.getBlockZ()), mechanic.getBarriers())) {
                        PersistentDataContainer data = BlockHelpers.getPDC(world.getBlockAt(barrierLocation));

                        data.set(FurnitureMechanic.FURNITURE_KEY, PersistentDataType.STRING, mechanic.getItemID());
//                        if (mechanic.hasSeat()) {
//                            data.set(FurnitureMechanic.SEAT_KEY, DataType.UUID, mechanic.spawnSeat(block, this.hasSeatYaw ? this.seatYaw : yaw));
//                        }


                        data.set(FurnitureMechanic.ROOT_KEY, PersistentDataType.STRING, new BlockLocation(new org.bukkit.Location(world,location.getX(),location.getY(),location.getZ()).clone()).toString());
                        data.set(FurnitureMechanic.ORIENTATION_KEY, PersistentDataType.FLOAT, yaw);
                        data.set(FurnitureMechanic.BASE_ENTITY_KEY, DataType.UUID, uuid);
//                        if (mechanic.handleLight && mechanic.light != -1) {
//                            WrappedLightAPI.createBlockLight(barrierLocation, this.light);
//                        }
                    }



                    return createdEntity;
                }
            });
        }
    }
}
