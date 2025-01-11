package me.udnek.itemscoreu.customitem;

import io.papermc.paper.datacomponent.DataComponentType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static io.papermc.paper.datacomponent.DataComponentTypes.*;

public interface UpdatingCustomItem extends CustomItem{

    default boolean isUpdateMaterial(){return true;}

    default void getComponentsToUpdate(@NotNull ConstructableCustomItem.ComponentConsumer consumer){
        consumer.accept(MAX_DAMAGE);
        consumer.accept(CONSUMABLE);
        consumer.accept(RARITY);
        consumer.accept(LORE);
        consumer.accept(ATTRIBUTE_MODIFIERS);
        consumer.accept(FOOD);
        consumer.accept(ITEM_MODEL);
        consumer.accept(REPAIRABLE);
        consumer.accept(ITEM_NAME);
    }

    @Override
    default @NotNull ItemStack update(@NotNull ItemStack itemStack) {
        ItemStack relevantItem = getItem();
        if (isUpdateMaterial()) itemStack = itemStack.withType(relevantItem.getType());
        @NotNull ItemStack finalItemStack = itemStack;
        getComponentsToUpdate(new ConstructableCustomItem.ComponentConsumer() {
            @Override
            public <T> void accept(DataComponentType.@NotNull Valued<T> type) {
                T data = relevantItem.getData(type);
                if (data == null) finalItemStack.resetData(type);
                else finalItemStack.setData(type, data);
            }

            @Override
            public void accept(DataComponentType.@NotNull NonValued type) {
                boolean relevantHas = relevantItem.hasData(type);
                boolean currentHas = finalItemStack.hasData(type);
                if (currentHas && !relevantHas) finalItemStack.unsetData(type);
                else if (!currentHas && relevantHas) finalItemStack.setData(type);
            }
        });
        return itemStack;
    }

    interface ComponentConsumer{
        <T> void accept(@NotNull DataComponentType.Valued<T> type);
        void accept(@NotNull DataComponentType.NonValued type);
    }
}
