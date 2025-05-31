package me.udnek.coreu.custom.attribute;

import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ConstructableReversedCustomAttribute extends ConstructableCustomAttribute {
    public ConstructableReversedCustomAttribute(@NotNull String rawId, double defaultValue, double min, double max, boolean beneficial, boolean numberAsPercentageLore) {
        super(rawId, defaultValue, min, max, beneficial, numberAsPercentageLore);
    }
    public ConstructableReversedCustomAttribute(@NotNull String rawId, double defaultValue, double min, double max, boolean beneficial) {
        this(rawId, defaultValue, min, max, beneficial, true);
    }
    public ConstructableReversedCustomAttribute(@NotNull String rawId, double defaultValue, double min, double max) {
        this(rawId, defaultValue, min, max, true);
    }

    @Override
    public double calculateWithBase(@NotNull LivingEntity entity, double base) {
        return clamp(getMax() - CustomAttributeUtils.calculate(this, entity, getMax()-base, true));
    }
}
