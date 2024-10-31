package me.udnek.itemscoreu.customitem;

import com.google.common.base.Preconditions;
import me.udnek.itemscoreu.customattribute.AttributeUtils;
import me.udnek.itemscoreu.customcomponent.AbstractComponentHolder;
import me.udnek.itemscoreu.customevent.CustomItemGeneratedEvent;
import me.udnek.itemscoreu.customrecipe.RecipeManager;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public abstract class ConstructableCustomItem extends AbstractComponentHolder<CustomItem> implements CustomItem, CustomItemProperties {
    private String id;
    protected ItemStack itemStack;
    protected List<Recipe> recipes = null;

    ///////////////////////////////////////////////////////////////////////////
    // INITIAL
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public final @NotNull String getId(){return this.id;}

    @Override
    public void initialize(@NotNull Plugin plugin){
        Preconditions.checkArgument(id == null, "Item already initialized!");
        id = new NamespacedKey(plugin, getRawId()).asString();
    }

    public void initializeComponents(){}

    @Override
    public void afterInitialization() {
        CustomItem.super.afterInitialization();
        generateRecipes(this::registerRecipe);
    }

    ///////////////////////////////////////////////////////////////////////////
    // CREATING
    ///////////////////////////////////////////////////////////////////////////

    protected void setPersistentData(ItemMeta itemMeta){
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(PERSISTENT_DATA_CONTAINER_NAMESPACE, PersistentDataType.STRING, id);
    }

    protected ItemMeta getMainItemMeta(){
        ItemMeta itemMeta = new ItemStack(getMaterial()).getItemMeta();
        setPersistentData(itemMeta);

        itemMeta.itemName(this.getItemName());

        ArrayList<Component> lore = new ArrayList<>();
        getLore(lore::add);
        if (lore.isEmpty()) itemMeta.lore(null);
        else itemMeta.lore(lore);

        itemMeta.setRarity(getRarity());
        itemMeta.setHideTooltip(getHideTooltip());
        itemMeta.setFood(getFood());
        itemMeta.setTool(getTool());
        itemMeta.displayName(getDisplayName());
        itemMeta.setUnbreakable(getUnbreakable());
        itemMeta.setEnchantmentGlintOverride(getEnchantmentGlintOverride());
        itemMeta.setCustomModelData(getCustomModelData());
        if (getDamageResistant() != null) itemMeta.setDamageResistant(getDamageResistant());
        itemMeta.setMaxStackSize(getMaxStackSize());
        if (itemMeta instanceof Damageable damageable){
            if (getMaxDamage() != null){
                damageable.setMaxStackSize(1);
                damageable.setMaxDamage(getMaxDamage());
            }
        }
        if (itemMeta instanceof ArmorMeta armorMeta) armorMeta.setTrim(getTrim());
        if (itemMeta instanceof MusicInstrumentMeta instrumentMeta) instrumentMeta.setInstrument(getMusicInstrument());
        if (getAddDefaultAttributes()) AttributeUtils.addDefaultAttributes(itemMeta, getMaterial());
        if (getAttributes() != null){
            for (Map.Entry<Attribute, List<AttributeModifier>> entry : getAttributes().entrySet()) {
                Attribute attribute = entry.getKey();
                for (AttributeModifier modifier : entry.getValue()) {
                    itemMeta.addAttributeModifier(attribute, modifier);
                }
            }
        }
        if (getBlockData() != null && itemMeta instanceof BlockDataMeta blockDataMeta){
            blockDataMeta.setBlockData(getBlockData());
        }
        ItemFlag[] tooltipHides = getTooltipHides();
        if (tooltipHides != null) itemMeta.addItemFlags(tooltipHides);

        // 1.21.3
        if (getItemModel() != null) itemMeta.setItemModel(getItemModel());
        if (getUseRemainder() != null) itemMeta.setUseRemainder(getUseRemainder());
        if (getUseRemainderCustom() != null) itemMeta.setUseRemainder(getUseRemainderCustom().getItem());
        if (getEquippable() != null) itemMeta.setEquippable(getEquippable());

        return itemMeta;
    }

    protected void modifyFinalItemMeta(ItemMeta itemMeta){}

    protected ItemStack getMainItemStack(){
        ItemMeta itemMeta = this.getMainItemMeta();
        this.modifyFinalItemMeta(itemMeta);
        ItemStack itemStack = new ItemStack(this.getMaterial());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    protected void modifyFinalItemStack(ItemStack itemStack){}

    @Override
    public @NotNull ItemStack getItem(){
        if (itemStack == null){
            initializeComponents();
            ItemStack newItem = this.getMainItemStack();
            modifyFinalItemStack(newItem);
            CustomItemGeneratedEvent event = new CustomItemGeneratedEvent(this, newItem, getLoreBuilder());
            event.callEvent();
            event.getLoreBuilder().buildAndApply(event.getItemStack());
            itemStack = event.getItemStack();
        }
        return itemStack.clone();
    }

    @Override
    public @NotNull NamespacedKey getNewRecipeKey() {
        return NamespacedKey.fromString(getId() + "_" + (recipes != null ? Integer.toString(recipes.size()) : "0"));
    }

    @Override
    public void getRecipes(@NotNull Consumer<@NotNull Recipe> consumer) {
        if (recipes == null) return;
        recipes.forEach(consumer);
    }

    @Override
    public void registerRecipe(@NotNull Recipe recipe) {
        if (recipes == null) recipes = new ArrayList<>();
        recipes.add(recipe);
        RecipeManager.getInstance().register(recipe);
    }

    protected void generateRecipes(@NotNull Consumer<@NotNull Recipe> consumer){}
}
