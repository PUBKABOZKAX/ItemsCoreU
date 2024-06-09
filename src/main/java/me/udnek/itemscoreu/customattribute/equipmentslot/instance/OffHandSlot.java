package me.udnek.itemscoreu.customattribute.equipmentslot.instance;

import me.udnek.itemscoreu.customattribute.equipmentslot.SoloSlot;
import org.bukkit.inventory.EquipmentSlotGroup;

public class OffHandSlot extends SoloSlot {
    @Override
    public int getSlot() {return 40;}

    @Override
    public EquipmentSlotGroup getVanillaAlternative() {return EquipmentSlotGroup.OFFHAND;}
}
