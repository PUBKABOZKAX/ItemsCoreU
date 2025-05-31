package me.udnek.coreu.custom.attribute;

import me.udnek.coreu.custom.registry.Registrable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public interface CustomAttribute extends Translatable, Registrable {

    TextColor EQUALS_COLOR = NamedTextColor.DARK_GREEN;
    TextColor TAKE_COLOR = NamedTextColor.RED;
    TextColor PLUS_COLOR = NamedTextColor.BLUE;

    @NotNull Component getLoreLine(double amount, @NotNull AttributeModifier.Operation operation);
    @NotNull Component getLoreLineWithBase(double base);
    default double calculate(@NotNull LivingEntity entity){
        return calculateWithBase(entity, getDefault());
    }
    double calculateWithBase(@NotNull LivingEntity entity, double base);
    double getDefault();
    double getMin();
    double getMax();
}
