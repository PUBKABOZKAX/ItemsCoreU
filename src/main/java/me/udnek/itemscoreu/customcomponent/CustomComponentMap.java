package me.udnek.itemscoreu.customcomponent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CustomComponentMap<HolderType, Component extends CustomComponent<HolderType>> implements Iterable<Component>{

    public static <H, C extends CustomComponent<H>> CustomComponentMap<H, C> newAlwaysEmpty(){
        return new CustomComponentMap<>(){
            @Override
            public void set(@NotNull C component) {
                throw new RuntimeException("Can not set component to AlwaysEmptyHolder!");
            }
        };
    }

    private @Nullable Map<CustomComponentType<HolderType, Component>,  Component> map = null;

    public @NotNull <SpicificComponent extends Component> SpicificComponent getOrDefault(@NotNull CustomComponentType<? extends HolderType, SpicificComponent> type) {
        if (map == null) return type.getDefault();
        return (SpicificComponent) map.getOrDefault(type, type.getDefault());
    }

    public @NotNull <SpicificComponent extends Component> SpicificComponent getOrCreateDefault(@NotNull CustomComponentType<? extends HolderType, SpicificComponent> type) {
        SpicificComponent component = get(type);
        if (component == null) {
            SpicificComponent newDefault = type.createNewDefault();
            set(newDefault);
            return newDefault;
        }
        return component;
    }

    public @NotNull <SpicificComponent extends Component> Component getOrException(@NotNull CustomComponentType<? extends HolderType, SpicificComponent> type){
        return Objects.requireNonNull(map == null ? null : get(type), "Component " + type.getKey().asString() + " is not present!");
    }

    public @Nullable  <SpicificComponent extends Component> SpicificComponent get(@NotNull CustomComponentType<? extends HolderType, SpicificComponent> type) {
        if (map == null) return null;
        return (SpicificComponent) map.get(type);
    }
    public <Type extends CustomComponentType<HolderType, Component>> boolean has(@NotNull Type type) {
        if (map == null) return false;
        return map.containsKey(type);
    }
    public void set(@NotNull Component component) {
        if (map == null) map = new HashMap<>();
        map.put((CustomComponentType<HolderType, Component>) component.getType(), component);
    }
    public void remove(@NotNull CustomComponentType<HolderType, Component> type) {
        if (map != null) map.remove(type);
    }
    @Override
    public @NotNull Iterator<Component> iterator() {
        if (map == null) return Collections.emptyIterator();
        return map.values().iterator();
    }
}
