package me.udnek.itemscoreu.customsound;

import com.google.common.base.Preconditions;
import me.udnek.itemscoreu.customregistry.AbstractRegistrable;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConstructableCustomSound extends AbstractRegistrable implements CustomSound {

    protected NamespacedKey path;
    protected SoundCategory category;
    protected float volume;
    protected float pitch;

    public ConstructableCustomSound(@NotNull NamespacedKey path, @NotNull SoundCategory category, float volume, float pitch){
        this.path = path;
        this.category = category;
        this.volume = volume;
        this.pitch = pitch;
    }

    public ConstructableCustomSound(@NotNull NamespacedKey path, @NotNull SoundCategory category){
        this(path, category, 1F, 1F);
    }

    @Override
    public @NotNull String getRawId() {
        return path.getKey();
    }

    public @NotNull String getSoundName(){
        return path.asString();
    }

    @Override
    public void play(@Nullable Location location, @Nullable Player player) {
        play(location, player, category, volume, pitch);
    }

    @Override
    public void play(@Nullable Location location, @Nullable Player player, float volume) {
        play(location, player, volume, pitch);
    }

    @Override
    public void play(@Nullable Location location, @Nullable Player player, float volume, float pitch) {
        play(location, player, category, volume, pitch);
    }

    @Override
    public void play(@Nullable Location location, @Nullable Player player, @NotNull SoundCategory category, float volume, float pitch) {
        Preconditions.checkArgument(player != null || location != null, "Both location and player can not be null");
        if (player == null){
            location.getWorld().playSound(location, getSoundName(), category, volume, pitch);
        } else if (location != null) {
            player.playSound(location, getSoundName(), category, volume, pitch);
        } else {
            player.playSound(player, getSoundName(), category, volume, pitch);
        }
    }

    @Override
    public void stop(@NotNull Player player) {
        player.stopSound(getSoundName(), category);
    }

    @Override
    public void stop(@NotNull Player player, @NotNull SoundCategory soundCategory) {
        player.stopSound(getSoundName(), soundCategory);
    }
}
