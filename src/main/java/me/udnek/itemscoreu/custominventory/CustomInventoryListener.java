package me.udnek.itemscoreu.custominventory;

import me.udnek.itemscoreu.ItemsCoreU;
import me.udnek.itemscoreu.utils.SelfRegisteringListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class CustomInventoryListener extends SelfRegisteringListener {

    public CustomInventoryListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerClicksInInventory(InventoryClickEvent event){
        if (event.getInventory().getHolder() instanceof CustomInventory){
            CustomInventory holder = (CustomInventory) event.getInventory().getHolder();
            holder.onPlayerClicksItem(event);

            new BukkitRunnable() {
                @Override
                public void run() {
                    holder.afterPlayerClicksItem(event);
                }
            }.runTaskLater(ItemsCoreU.getInstance(), 1);
        }
    }
    @EventHandler
    public void onPlayerClosesInventory(InventoryCloseEvent event){
        if (event.getInventory().getHolder() instanceof CustomInventory){
            ((CustomInventory) event.getInventory().getHolder()).onPlayerClosesInventory(event);
        }
    }

    @EventHandler
    public void onPlayerOpensInventory(InventoryOpenEvent event){
        if (event.getInventory().getHolder() instanceof CustomInventory){
            ((CustomInventory) event.getInventory().getHolder()).onPlayerOpensInventory(event);
        }
    }

    @EventHandler
    public void onPlayerDragsItem(InventoryDragEvent event){
        if (event.getInventory().getHolder() instanceof CustomInventory){
            ((CustomInventory) event.getInventory().getHolder()).onPlayerDragsItem(event);
        }
    }
}
