package me.udnek.itemscoreu.customsound;

import me.udnek.itemscoreu.customregistry.Registrable;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CustomSound extends Registrable {
    void play(@Nullable Location location, @Nullable Player player, @NotNull SoundCategory category, float volume, float pitch);
    void play(@Nullable Location location, @Nullable Player player, float volume, float pitch);
    void play(@Nullable Location location, @Nullable Player player, float volume);
    void play(@Nullable Location location, @Nullable Player player);
    default void play(@NotNull Player player){play(null, player);}
    default void play(@NotNull Location location){
        play(location, null);
    }
    void stop(@NotNull Player player);
    void stop(@NotNull Player player, @NotNull SoundCategory soundCategory);
}
