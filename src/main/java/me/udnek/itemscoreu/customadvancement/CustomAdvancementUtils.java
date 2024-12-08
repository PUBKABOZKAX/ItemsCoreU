package me.udnek.itemscoreu.customadvancement;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectMutablePair;
import me.udnek.itemscoreu.util.ItemUtils;
import me.udnek.itemscoreu.util.LogUtils;
import me.udnek.itemscoreu.util.Reflex;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.advancements.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R2.CraftServer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CustomAdvancementUtils {

    private static final HashMap<CustomAdvancementContainer, CustomAdvancementContainer> realPoses = new HashMap<>();

    public static @NotNull ConstructableCustomAdvancement itemAdvancement(@NotNull Key key, @NotNull ItemStack itemStack){
        ConstructableCustomAdvancement advancement = new ConstructableCustomAdvancement(key);
        advancement.addCriterion(ItemUtils.getId(itemStack), AdvancementCriterion.INVENTORY_CHANGE.create(itemStack));
        CustomAdvancementDisplayBuilder display = new CustomAdvancementDisplayBuilder(itemStack);
        display.title(ItemUtils.getDisplayName(itemStack));
        advancement.display(display);
        return advancement;
    }

    public static void register(@NotNull CustomAdvancementContainer advancementContainer){
        DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
        ServerAdvancementManager manager = server.getAdvancements();

        AdvancementHolder advancementHolder = advancementContainer.get();

        Reflex.invokeMethod(
                manager,
                Reflex.getMethod(
                        ServerAdvancementManager.class,
                        "validate",
                        ResourceLocation.class, Advancement.class
                ),
                advancementHolder.id(),
                advancementHolder.value()
        );

        ImmutableMap.Builder<ResourceLocation, AdvancementHolder> mapBuilder = ImmutableMap.builder();
        mapBuilder.put(advancementHolder.id(), advancementHolder);

        Map<ResourceLocation, AdvancementHolder> advancements = new HashMap<>(mapBuilder.buildOrThrow());
        advancements.putAll((Map<ResourceLocation, AdvancementHolder>) Reflex.getFieldValue(manager, "advancements"));
        AdvancementTree tree = new AdvancementTree();
        tree.addAll(advancements.values());
        for (AdvancementNode advancementnode : tree.roots()) {
            if (advancementnode.holder().value().display().isPresent()) {
                TreeNodePosition.run(advancementnode);
            }
        }

        Reflex.setFieldValue(manager, "advancements", advancements);
        Reflex.setFieldValue(manager, "tree", tree);

        for (@NotNull CustomAdvancementContainer fake : advancementContainer.getFakes()) {
            realPoses.put(fake, advancementContainer);
            register(fake);
        }

        for (Map.Entry<CustomAdvancementContainer, CustomAdvancementContainer> entry : realPoses.entrySet()) {
            CustomAdvancementContainer fake = entry.getKey();
            CustomAdvancementContainer real = entry.getValue();

            DisplayInfo fakeDisplay = (DisplayInfo) Reflex.getFieldValue(fake.get().value(), "display", Optional.class).orElse(null);
            if (fakeDisplay == null) continue;

            DisplayInfo realDisplay = (DisplayInfo) Reflex.getFieldValue(real.get().value(), "display", Optional.class).orElse(null);
            if (realDisplay == null) return;

            fakeDisplay.setLocation(realDisplay.getX(), realDisplay.getY());
        }

/*        for (@NotNull CustomAdvancementContainer fake : advancementContainer.getFakes()) {
            DisplayInfo fakeDisplay = (DisplayInfo) Reflex.getFieldValue(fake.get().value(), "display", Optional.class).orElse(null);
            System.out.println("fakedisp: " + fakeDisplay);
            if (fakeDisplay == null) continue;

            DisplayInfo realDisplay = (DisplayInfo) Reflex.getFieldValue(fake.getRealPosition().get().value(), "display", Optional.class).orElse(null);
            System.out.println("realdisp: " + realDisplay);
            if (realDisplay == null) return;

            LogUtils.log(Component.text("old fake pos: " + fakeDisplay.getX() + " "+  fakeDisplay.getY()).color(NamedTextColor.RED));
            LogUtils.log(Component.text("setting fake pos: " + realDisplay.getX() + " "+  realDisplay.getY()).color(NamedTextColor.RED));

            fakeDisplay.setLocation(realDisplay.getX(), realDisplay.getY());
        }*/
        

    }
}
