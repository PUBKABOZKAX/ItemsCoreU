package me.udnek.itemscoreu.customentitylike.block.constructabletype;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import me.udnek.itemscoreu.customcomponent.CustomComponentMap;
import me.udnek.itemscoreu.customcomponent.instance.RightClickableBlock;
import me.udnek.itemscoreu.customentitylike.block.CustomBlockManager;
import me.udnek.itemscoreu.customentitylike.block.CustomBlockType;
import me.udnek.itemscoreu.customregistry.AbstractRegistrable;
import me.udnek.itemscoreu.nms.Nms;
import me.udnek.itemscoreu.nms.loot.LootContextBuilder;
import me.udnek.itemscoreu.util.Either;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CreakingHeartBlock;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreakingHeart;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstactCustomBlockType extends AbstractRegistrable implements CustomBlockType {

    private final CustomComponentMap<CustomBlockType> components = new CustomComponentMap<>();

    @Override
    public @NotNull CustomComponentMap<CustomBlockType> getComponents() {
        return components;
    }


    public abstract @NotNull TileState getRealState();
    public abstract @Nullable ItemStack getParticleBase();

    @ApiStatus.Experimental
    public @Nullable SoundGroup getSoundGroup(){
        return getBreakSpeedBaseBlock().createBlockData().getSoundGroup();
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void place(@NotNull Location location){
        TileState blockState = (TileState) getRealState().copy(location);
        blockState.getPersistentDataContainer().set(PDC_KEY, PersistentDataType.STRING, getId());
        blockState.update(true, false);
        CustomBlockManager.getInstance().loadAny(this, blockState);
        @Nullable SoundGroup soundGroup = getSoundGroup();
        if (soundGroup != null) {
            location.getWorld().playSound(location.toCenterLocation(), soundGroup.getPlaceSound(), SoundCategory.BLOCKS, soundGroup.getVolume(), soundGroup.getPitch());
        }
    }


    @ApiStatus.Experimental
    public abstract @Nullable Either<LootTable, List<ItemStack>> getLoot();

    @Override
    public void afterInitialization() {
        CustomBlockType.super.afterInitialization();
        initializeComponents();
    }

    public abstract @NotNull Material getBreakSpeedBaseBlock();

    @Override
    public float getCustomBreakProgress(@NotNull Player player, @NotNull Block block) {
        return Nms.get().getBreakProgressPerTick(player, getBreakSpeedBaseBlock());
    }


    @Override
    public void destroy(@NotNull Location location) {
        location.getWorld().getBlockAt(location).setType(Material.AIR);
        onGenericDestroy(location.getBlock());
    }

    @Override
    public void onDestroy(@NotNull EntityExplodeEvent event, @NotNull Block block) {
        onGenericDestroy(block);
        block.setType(Material.AIR);
    }
    @Override
    public void onDestroy(@NotNull BlockExplodeEvent event) {
        onGenericDestroy(event.getExplodedBlockState().getBlock());
    }
    @Override
    public void onDestroy(@NotNull BlockDestroyEvent event){
        onGenericDestroy(event.getBlock());
    }

    @MustBeInvokedByOverriders
    public void initializeComponents() {
        getComponents().set(new RightClickableBlock() {
            @Override
            public void onRightClick(@NotNull CustomBlockType customBlockType, @NotNull PlayerInteractEvent event) {
                event.setUseInteractedBlock(Event.Result.DENY);

                ItemStack item = event.getItem();
                if (item == null) return;
                if (!Nms.get().mayBuild(event.getPlayer())) return;

                Block relative = event.getClickedBlock().getRelative(event.getBlockFace());
                BlockState state = relative.getState(true);

                Nms.BlockPlaceResult placeResult = Nms.get().placeBlockFromItem(
                        event.getPlayer(),
                        event.getItem(),
                        event.getHand(),
                        event.getInteractionPoint(),
                        event.getBlockFace(),
                        event.getClickedBlock()
                );

                if (!placeResult.isSuccess()) return;

                boolean passed = new BlockPlaceEvent(
                        relative,
                        state,
                        event.getClickedBlock(),
                        item,
                        event.getPlayer(), true,
                        event.getHand()
                ).callEvent();

                if (!passed) {
                    event.getClickedBlock().setBlockData(state.getBlockData());
                } else {
                    event.getPlayer().getInventory().setItem(event.getHand(), placeResult.resultingItem());
                }
            }
        });
    }


    public void dropItems(@NotNull BlockBreakEvent event){
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        @Nullable Either<LootTable, List<ItemStack>> loot = getLoot();
        if (loot == null) return;
        World world = event.getBlock().getWorld();
        Location centerLocation = event.getBlock().getLocation().toCenterLocation();
        List<ItemStack> drops = new ArrayList<>();
        loot.consumeEither(new Consumer<List<ItemStack>>() {
            @Override
            public void accept(List<ItemStack> stacks) {
                drops.addAll(stacks);
            }
        }, new Consumer<LootTable>() {
            @Override
            public void accept(LootTable lootTable) {
                World world = event.getBlock().getWorld();

                List<ItemStack> stacks = Nms.get().populateLootTable(lootTable,
                        new LootContextBuilder(lootTable, world)
                                .blockState(event.getBlock().getState())
                                .tool(event.getPlayer().getInventory().getItemInMainHand())
                                .origin(centerLocation)
                );

                drops.addAll(stacks);
            }
        });

        for (ItemStack itemStack : drops) world.dropItemNaturally(centerLocation, itemStack);
    }

    @Override
    public void onDestroy(@NotNull BlockBreakEvent event){
        event.setExpToDrop(0);
        event.setDropItems(false);
        dropItems(event);
        onGenericDestroy(event.getBlock());
    }
    @Override
    public void onDestroy(@NotNull BlockBurnEvent event) {
        onGenericDestroy(event.getBlock());
    }
    @Override
    public void onDestroy(@NotNull BlockFadeEvent event) {
        onGenericDestroy(event.getBlock());
    }

    @Override
    public void customBreakTickProgress(@NotNull Block block, @NotNull Player player, float progress) {
        ItemStack particleBase = getParticleBase();
        if (particleBase == null) return;

        Location centerLocation = block.getLocation().toCenterLocation();
        ParticleBuilder builder = new ParticleBuilder(Particle.ITEM);
        builder.location(centerLocation);
        builder.data(particleBase);
        builder.count(2);
        builder.offset(0.3f, 0.3f, 0.3f);
        builder.extra(0);
        builder.force(false);
        builder.spawn();


        if (Bukkit.getCurrentTick() % 5 != 0 ) return;
        @Nullable SoundGroup soundGroup = getSoundGroup();
        if (soundGroup != null) {
            Location location = block.getLocation().toCenterLocation();
            location.getWorld().playSound(location, soundGroup.getHitSound(), SoundCategory.BLOCKS, soundGroup.getVolume()/5f, soundGroup.getPitch());
        }
    }

    @Override
    public void onPlayerFall(@NotNull Player player, @NotNull Location location, int particlesCount) {
        ItemStack particleBase = getParticleBase();
        if (particleBase != null){
            ParticleBuilder builder = new ParticleBuilder(Particle.ITEM);
            builder.location(location);
            builder.data(particleBase);
            builder.count(particlesCount);
            builder.offset(0f, 0f, 0f);
            builder.extra(0.15);
            builder.force(false);
            builder.receivers(player);
            builder.spawn();
        }
        @Nullable SoundGroup soundGroup = getSoundGroup();
        if (soundGroup != null) {
            location.getWorld().playSound(location, soundGroup.getFallSound(), SoundCategory.BLOCKS, soundGroup.getVolume(), soundGroup.getPitch());
        }
    }

    @OverridingMethodsMustInvokeSuper
    public void onGenericDestroy(@NotNull Block block){
        CustomBlockManager.getInstance().unloadAny(this, (TileState) block.getState());

        ItemStack particleBase = getParticleBase();
        if (particleBase == null) return;

        Location centerLocation = block.getLocation().toCenterLocation();
        ParticleBuilder builder = new ParticleBuilder(Particle.ITEM);
        builder.location(centerLocation);
        builder.data(particleBase);
        builder.count(50);
        builder.offset(0.3f, 0.3f, 0.3f);
        builder.extra(0.07);
        builder.force(false);
        builder.spawn();

        @Nullable SoundGroup soundGroup = getSoundGroup();
        if (soundGroup != null) {
            Location location = block.getLocation().toCenterLocation();
            location.getWorld().playSound(location, soundGroup.getBreakSound(), SoundCategory.BLOCKS, soundGroup.getVolume(), soundGroup.getPitch());
        }
    }
}
