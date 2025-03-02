package me.udnek.itemscoreu.customregistry;

import com.google.common.base.Preconditions;
import me.udnek.itemscoreu.util.LogUtils;
import net.minecraft.world.entity.animal.Wolf;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MappedCustomRegistry<T extends Registrable> extends AbstractRegistrable implements CustomRegistry<T>{

    protected HashMap<String, T> map = new HashMap<>();
    protected List<String> indexes = new ArrayList<>();
    protected String rawId;

    public MappedCustomRegistry(@NotNull String rawId){
        this.rawId = rawId;
    }

    @Override
    public @NotNull String getRawId() {return rawId;}


    @Override
    public <V extends T> @NotNull V register(@NotNull Plugin plugin, @NotNull V custom) {
        custom.initialize(plugin);
        Preconditions.checkArgument(!map.containsKey(custom.getId()), "Registry already contains key " + custom.getId());
        map.put(custom.getId(), custom);
        indexes.add(custom.getId());
        logRegistered(custom);
        if (custom instanceof Listener listener){
            Bukkit.getPluginManager().registerEvents(listener, plugin);
            LogUtils.pluginLog("(EventListener) " + custom.getId() + " (" +listener.getClass().getName() + ")");
        }
        return custom;
    }

    @Override
    public T get(@Nullable String id) {
        return map.get(id);
    }

    @Override
    public @NotNull T get(int index) {
        return Objects.requireNonNull(get(indexes.get(index)));
    }

    @Override
    public int getIndex(@NotNull T custom) {
        return indexes.indexOf(custom.getId());
    }

    @Override
    public boolean contains(@Nullable String id) {
        return map.containsKey(id);
    }

    @Override
    public @NotNull Collection<String> getIds() {
        return new ArrayList<>(map.keySet());
    }
    protected void logRegistered(@NotNull T custom){
        LogUtils.pluginLog("("+ getId() +") " + custom.getId());
    }

    @Override
    public void getAll(@NotNull Consumer<T> consumer) {
        map.values().forEach(consumer);
    }
    @Override
    public @NotNull Collection<T> getAll() {
        return new ArrayList<>(map.values());
    }
    @Override
    public @NotNull Collection<T> getAllByPlugin(@NotNull Plugin plugin) {
        Collection<T> all = getAll();
        String namespace = new NamespacedKey(plugin, "text").getNamespace();
        all.removeIf(object -> !object.getKey().getNamespace().equals(namespace));
        return all;
    }
}
