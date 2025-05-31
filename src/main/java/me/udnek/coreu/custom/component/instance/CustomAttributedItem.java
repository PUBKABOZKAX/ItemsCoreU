package me.udnek.coreu.custom.component.instance;

import me.udnek.coreu.custom.attribute.CustomAttribute;
import me.udnek.coreu.custom.attribute.CustomAttributeModifier;
import me.udnek.coreu.custom.attribute.CustomAttributesContainer;
import me.udnek.coreu.custom.component.CustomComponent;
import me.udnek.coreu.custom.component.CustomComponentType;
import me.udnek.coreu.custom.item.CustomItem;
import me.udnek.coreu.custom.item.CustomItemComponent;
import me.udnek.coreu.custom.equipmentslot.slot.CustomEquipmentSlot;
import me.udnek.coreu.rpgu.lore.AttributeLoreGenerator;
import me.udnek.coreu.rpgu.lore.AttributesLorePart;
import me.udnek.coreu.util.LoreBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class CustomAttributedItem implements CustomItemComponent {

    public static final CustomAttributedItem EMPTY = new CustomAttributedItem(){
        @Override
        public void addAttribute(@NotNull CustomAttribute attribute, @NotNull CustomAttributeModifier modifier) {
            throwCanNotChangeDefault();
        }
    };

    protected CustomAttributesContainer container;

    public CustomAttributedItem(){
        this(CustomAttributesContainer.empty());
    }

    public CustomAttributedItem(@NotNull CustomAttributesContainer container){
        this.container = container;
    }

    public void addAttribute(@NotNull CustomAttribute attribute, @NotNull CustomAttributeModifier modifier){
        container = new CustomAttributesContainer.Builder().add(container).add(attribute, modifier).build();
    }

    public @NotNull CustomAttributesContainer getAttributes() {return container;}

    @Override
    public void getLore(@NotNull CustomItem customItem, @NotNull LoreBuilder builder) {

        LoreBuilder.Componentable componentable = builder.get(LoreBuilder.Position.ATTRIBUTES);
        AttributesLorePart attributesLorePart;
        if (componentable instanceof AttributesLorePart){
            attributesLorePart = (AttributesLorePart) componentable;
        } else {
            attributesLorePart = new AttributesLorePart();
            builder.set(LoreBuilder.Position.ATTRIBUTES, attributesLorePart);
        }

        for (Map.Entry<CustomAttribute, List<CustomAttributeModifier>> entry : getAttributes().getAll().entrySet()) {
            CustomAttribute attribute = entry.getKey();
            for (CustomAttributeModifier modifier : entry.getValue()) {
                if (modifier.getAmount() == 0) continue;
                CustomEquipmentSlot slot = modifier.getEquipmentSlot();
                attributesLorePart.addAttribute(slot, AttributeLoreGenerator.getAttributeLine(attribute, modifier.getAmount(), modifier.getOperation(), slot));
            }
        }
    }

    @Override
    public @NotNull CustomComponentType<? extends CustomItem, ? extends CustomComponent<CustomItem>> getType() {
        return CustomComponentType.CUSTOM_ATTRIBUTED_ITEM;
    }
}
