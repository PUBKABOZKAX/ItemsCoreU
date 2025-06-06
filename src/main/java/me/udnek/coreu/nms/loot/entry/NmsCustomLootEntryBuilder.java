package me.udnek.coreu.nms.loot.entry;

import me.udnek.coreu.nms.NmsUtils;
import me.udnek.coreu.nms.loot.util.*;
import me.udnek.coreu.util.Reflex;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.bukkit.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class NmsCustomLootEntryBuilder implements NmsLootEntryContainer, NmsFunctioned {

    protected int weight = 1;
    protected int quality = 0;
    protected List<LootItemFunction> functions = new ArrayList<>();
    protected List<LootItemCondition> conditions = new ArrayList<>();
    protected ItemStackCreator creator;

    public NmsCustomLootEntryBuilder(@NotNull ItemStackCreator creator){
        this.creator = creator;
    }

    public static @NotNull NmsCustomLootEntryBuilder fromVanilla(@NotNull LootTable lootTable, @NotNull Predicate<org.bukkit.inventory.ItemStack> predicate, @NotNull ItemStackCreator creator){
        NmsCustomLootEntryBuilder builder = new NmsCustomLootEntryBuilder(creator);
        return builder.copyConditionsFrom(lootTable, predicate).copyFunctionsFrom(lootTable, predicate);
    }

    public @NotNull NmsCustomLootEntryBuilder copyConditionsFrom(@NotNull LootTable lootTable, @NotNull Predicate<org.bukkit.inventory.ItemStack> predicate){
        LootPoolSingletonContainer foundContainer = NmsUtils.getSingletonContainerByPredicate(
                NmsUtils.toNmsLootTable(lootTable),
                itemStack -> predicate.test(NmsUtils.toBukkitItemStack(itemStack)));
        LootPool foundPool = NmsUtils.getLootPoolByPredicate(
                NmsUtils.toNmsLootTable(lootTable),
                itemStack -> predicate.test(NmsUtils.toBukkitItemStack(itemStack)));
        if (foundContainer != null){
            List<LootItemCondition> conditions = (List<LootItemCondition>) Reflex.getFieldValue(foundContainer, NmsFields.CONDITIONS);
            if (!conditions.isEmpty()) return conditions(conditions);
        } if (foundPool != null) {
            List<LootItemCondition> conditions = (List<LootItemCondition>) Reflex.getFieldValue(foundPool, NmsFields.CONDITIONS);
            if (!conditions.isEmpty()) return conditions(conditions);
        }
        return this;
    }
    public @NotNull NmsCustomLootEntryBuilder copyFunctionsFrom(@NotNull LootTable lootTable, @NotNull Predicate<org.bukkit.inventory.ItemStack> predicate){
        LootPoolSingletonContainer foundContainer = NmsUtils.getSingletonContainerByPredicate(
                NmsUtils.toNmsLootTable(lootTable),
                itemStack -> predicate.test(NmsUtils.toBukkitItemStack(itemStack)));
        LootPool foundPool = NmsUtils.getLootPoolByPredicate(
                NmsUtils.toNmsLootTable(lootTable),
                itemStack -> predicate.test(NmsUtils.toBukkitItemStack(itemStack)));
        if (foundContainer != null){
            List<LootItemFunction> functions = (List<LootItemFunction>) Reflex.getFieldValue(foundContainer, NmsFields.FUNCTIONS);
            if (!functions.isEmpty()) return functions(functions);
        } if (foundPool != null) {
            List<LootItemFunction> conditions = (List<LootItemFunction>) Reflex.getFieldValue(foundPool, NmsFields.FUNCTIONS);
            if (!conditions.isEmpty()) return functions(functions);
        }
        return this;
    }


    public @NotNull NmsCustomLootEntryBuilder weight(int weight){
        this.weight= weight;
        return this;
    }
    public @NotNull NmsCustomLootEntryBuilder quality(int quality){
        this.quality = quality;
        return this;
    }
    public @NotNull NmsCustomLootEntryBuilder creator(@NotNull ItemStackCreator creator){
        this.creator = creator;
        return this;
    }
    public @NotNull NmsCustomLootEntryBuilder functions(@NotNull List<LootItemFunction> functions){
        this.functions = functions;
        return this;
    }
    public @NotNull NmsCustomLootEntryBuilder conditions(@NotNull List<LootItemCondition> conditions){
        this.conditions = conditions;
        return this;
    }


    public @NotNull NmsCustomLootEntry build(){
        return new NmsCustomLootEntry(weight, quality, conditions, functions, creator);
    }

    @Override
    public @NotNull LootPoolEntryContainer get() {
        return build();
    }

    @Override
    public @NotNull NmsLootConditionsContainer getConditions() {
        return new NmsLootConditionsContainer(conditions);
    }

    @Override
    public void setConditions(@NotNull NmsLootConditionsContainer conditions) {
        conditions(conditions.get());
    }

    @Override
    public @NotNull NmsLootFunctionsContainer getFunctions() {
        return new NmsLootFunctionsContainer(functions);
    }

    @Override
    public void setFunctions(@NotNull NmsLootFunctionsContainer functions) {
        functions(functions.get());
    }
}


















