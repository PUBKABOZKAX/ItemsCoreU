package me.udnek.coreu.custom.component;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.mgu.ability.MGUAbilityHolderComponent;
import me.udnek.coreu.mgu.player.MGUPlayer;
import me.udnek.coreu.custom.component.instance.*;
import me.udnek.coreu.custom.entitylike.block.CustomBlockType;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.registry.CustomRegistries;
import me.udnek.coreu.custom.registry.Registrable;
import org.jetbrains.annotations.NotNull;

public interface CustomComponentType<HolderType, Component extends CustomComponent<HolderType>> extends Registrable {

    CustomComponentType<CustomItem, CustomAttributedItem>
            CUSTOM_ATTRIBUTED_ITEM = register(new ConstructableComponentType<>("custom_attributed_item", CustomAttributedItem.EMPTY, CustomAttributedItem::new));

    CustomComponentType<CustomItem, RightClickableItem>
            RIGHT_CLICKABLE_ITEM = register(new ConstructableComponentType<>("right_clickable_item", RightClickableItem.EMPTY));

    CustomComponentType<CustomItem, LeftClickableItem>
            LEFT_CLICKABLE_ITEM = register(new ConstructableComponentType<>("left_clickable_item", LeftClickableItem.EMPTY));

    CustomComponentType<CustomItem, VanillaAttributedItem>
            VANILLA_ATTRIBUTED_ITEM = register(new ConstructableComponentType<>("vanilla_attributed_item", VanillaAttributedItem.EMPTY, VanillaAttributedItem::new));

    CustomComponentType<CustomItem, InventoryInteractableItem>
            INVENTORY_INTERACTABLE_ITEM = register(new ConstructableComponentType<>("inventory_interactable_item", InventoryInteractableItem.EMPTY));

    CustomComponentType<CustomItem, BlockPlacingItem>
            BLOCK_PLACING_ITEM = register(new ConstructableComponentType<>("block_placing_item", BlockPlacingItem.EMPTY));

    CustomComponentType<CustomItem, AutoGeneratingFilesItem>
            AUTO_GENERATING_FILES_ITEM = register(new ConstructableComponentType<>("auto_generating_files_item", AutoGeneratingFilesItem.GENERATED));

    CustomComponentType<CustomItem, DispensableItem>
            DISPENSABLE_ITEM = register(new ConstructableComponentType<>("dispensable_item", DispensableItem.DEFAULT));

    // BLOCK

    CustomComponentType<CustomBlockType, RightClickableBlock>
            RIGHT_CLICKABLE_BLOCK = register(new ConstructableComponentType<>("right_clickable_block", RightClickableBlock.EMPTY));

    CustomComponentType<CustomBlockType, HopperInteractingBlock>
            HOPPER_INTERACTING_BLOCK = register(new ConstructableComponentType<>("hopper_interacting_block", HopperInteractingBlock.DENY));

    // MGU

    CustomComponentType<MGUPlayer, MGUAbilityHolderComponent> MGU_ABILITY_HOLDER = register(new ConstructableComponentType<>(
            "mgu_ability_holder", MGUAbilityHolderComponent.DEFAULT, MGUAbilityHolderComponent::new));


    @NotNull Component getDefault();
    @NotNull Component createNewDefault();

    private static <T extends CustomComponentType<?, ?>> @NotNull T register(T type){
        return CustomRegistries.COMPONENT_TYPE.register(CoreU.getInstance(), type);
    }
}
