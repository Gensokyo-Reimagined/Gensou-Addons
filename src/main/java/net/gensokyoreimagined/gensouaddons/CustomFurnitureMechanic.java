package net.gensokyoreimagined.gensouaddons;

import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Slab;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class CustomFurnitureMechanic extends FurnitureMechanic {
    public CustomFurnitureMechanic(MechanicFactory mechanicFactory, ConfigurationSection section) {
        super(mechanicFactory, section);
    }

    @Override
    public Entity place(Location location, ItemStack originalItem, Float yaw, BlockFace facing) {
        Entity entity = super.place(location,originalItem,yaw,facing);
        if((facing.isCartesian())&&((entity instanceof Display))){
            if(location.clone().add(facing.getOppositeFace().getDirection()).getBlock().getBlockData() instanceof Slab slab){
                if(slab.getType()== Slab.Type.BOTTOM&&facing.getModY()==1){
                    entity.teleport(entity.getLocation().clone().add(0,-0.5,0));
                }
                if(slab.getType()== Slab.Type.TOP&&facing.getModY()==-1){
                    entity.teleport(entity.getLocation().clone().add(0,0.5,0));
                }
            }
        }

        return entity;
    }
}
