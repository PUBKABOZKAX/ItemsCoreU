package me.udnek.coreu.custom.component.instance;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.item.CustomItemComponent;
import me.udnek.coreu.nms.Nms;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public interface DispensableItem extends CustomItemComponent {
    DispensableItem DEFAULT = new DispensableItem() {
        @Override
        public void onDispense(@NotNull CustomItem item, @NotNull BlockDispenseEvent event) {}

        @Override
        public void onDrop(@NotNull CustomItem item, @NotNull BlockDispenseEvent event) {}
    };

    DispensableItem ALWAYS_DROP = new DispensableItem() {
        @Override
        public void onDispense(@NotNull CustomItem item, @NotNull BlockDispenseEvent event) {
            event.setCancelled(true);
            Block block = event.getBlock();
            ItemStack itemStack = event.getItem();
            Nms.get().simulateDropperDrop(itemStack, block);

            Inventory inventory = ((Dispenser) block.getState()).getInventory();
            new BukkitRunnable() {
                @Override
                public void run() {
                    inventory.removeItemAnySlot(itemStack);
                }
            }.runTaskLater(CoreU.getInstance(), 1);
        }

        @Override
        public void onDrop(@NotNull CustomItem item, @NotNull BlockDispenseEvent event) {}
    };

    void onDispense(@NotNull CustomItem item, @NotNull BlockDispenseEvent event);

    void onDrop(@NotNull CustomItem item, @NotNull BlockDispenseEvent event);

    @Override
    default @NotNull CustomComponentType<? extends CustomItem, ? extends CustomComponent<CustomItem>> getType() {
        return CustomComponentType.DISPENSABLE_ITEM;
    }
}
