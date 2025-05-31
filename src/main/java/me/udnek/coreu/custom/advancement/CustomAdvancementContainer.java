package me.udnek.coreu.custom.advancement;

import net.kyori.adventure.key.Key;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Supplier;

public interface CustomAdvancementContainer extends Supplier<AdvancementHolder> {
    @NotNull AdvancementHolder get();
    @Nullable CustomAdvancementDisplayBuilder getDisplay();
    @NotNull Set<@NotNull CustomAdvancementContainer> getFakes();
    @NotNull CustomAdvancementContainer copy(@NotNull Key key);
    void setParent(@Nullable CustomAdvancementContainer parent);

    enum RequirementsStrategy implements Supplier<AdvancementRequirements.Strategy> {
        AND(AdvancementRequirements.Strategy.AND),
        OR(AdvancementRequirements.Strategy.OR);

        private final AdvancementRequirements.Strategy strategy;
        RequirementsStrategy(AdvancementRequirements.Strategy strategy){
            this.strategy = strategy;
        }
        @Override
        public AdvancementRequirements.Strategy get() {
            return strategy;
        }
    }
}
