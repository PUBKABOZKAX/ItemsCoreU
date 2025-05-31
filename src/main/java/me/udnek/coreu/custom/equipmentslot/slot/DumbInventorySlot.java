package me.udnek.coreu.custom.equipmentslot.slot;

import me.udnek.coreu.CoreU;
import me.udnek.coreu.custom.equipmentslot.universal.UniversalInventorySlot;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class DumbInventorySlot extends AbstractCustomEquipmentSlot implements GroupSlot {
    public DumbInventorySlot(@NotNull String id) {
        super(id);
    }

    @Override
    public boolean intersects(@NotNull CustomEquipmentSlot slot) {
        return true;
    }

    @Override
    public boolean intersects(@NotNull UniversalInventorySlot slot) {
        return true;
    }

    @Override
    public @Nullable EquipmentSlotGroup getVanillaGroup() {return null;}
    @Override
    public @Nullable EquipmentSlot getVanillaSlot() {return null;}
    @Override
    public void getAllUniversal(@NotNull Consumer<@NotNull UniversalInventorySlot> consumer) {}
    @Override
    public void getAllSingle(@NotNull Consumer<@NotNull SingleSlot> consumer) {}

    @Override
    public @NotNull String translationKey() {
        return "slot." + new NamespacedKey(CoreU.getInstance(),"text").getNamespace() + ".dumb_inventory";
    }
}
