package me.udnek.itemscoreu;

import me.udnek.itemscoreu.customattribute.ClearAttributeCommand;
import me.udnek.itemscoreu.customattribute.CustomAttributeCommand;
import me.udnek.itemscoreu.customentitylike.block.CustomBlockManager;
import me.udnek.itemscoreu.customentitylike.block.command.LoadedCustomBlocksCommand;
import me.udnek.itemscoreu.customentitylike.block.command.SetCustomBlockCommand;
import me.udnek.itemscoreu.customentitylike.entity.CustomEntityManager;
import me.udnek.itemscoreu.customentitylike.entity.command.LoadedCustomEntitiesCommand;
import me.udnek.itemscoreu.customentitylike.entity.command.SummonCustomEntityCommand;
import me.udnek.itemscoreu.customhelp.CustomHelpCommand;
import me.udnek.itemscoreu.customhud.CustomHudManager;
import me.udnek.itemscoreu.custominventory.CustomInventoryListener;
import me.udnek.itemscoreu.customitem.CraftListener;
import me.udnek.itemscoreu.customitem.CustomItemCommand;
import me.udnek.itemscoreu.customitem.CustomItemListener;
import me.udnek.itemscoreu.customitem.VanillaItemManager;
import me.udnek.itemscoreu.customloot.LootTableUtils;
import me.udnek.itemscoreu.customrecipe.RecipeManager;
import me.udnek.itemscoreu.customregistry.CustomRegistries;
import me.udnek.itemscoreu.customregistry.CustomRegistry;
import me.udnek.itemscoreu.customregistry.InitializationProcess;
import me.udnek.itemscoreu.customsound.CustomSoundCommand;
import me.udnek.itemscoreu.nms.PacketHandler;
import me.udnek.itemscoreu.resourcepack.ResourcePackCommand;
import me.udnek.itemscoreu.resourcepack.ResourcePackablePlugin;
import me.udnek.itemscoreu.serializabledata.SerializableDataManager;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import org.bukkit.Bukkit;
import org.bukkit.event.world.WorldEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItemsCoreU extends JavaPlugin implements ResourcePackablePlugin {
    private static Plugin instance;


    public static Plugin getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        CustomRegistry<CustomRegistry<?>> registry = CustomRegistries.REGISTRY;

        // EVENTS
        new CustomItemListener(this);
        new CraftListener(this);
        new CustomInventoryListener(this);
        VanillaItemManager.getInstance();
        Bukkit.getPluginManager().registerEvents(new LootTableUtils(), this);
        RecipeManager.getInstance();


        // COMMANDS
        getCommand("giveu").setExecutor(new CustomItemCommand());
        getCommand("summonu").setExecutor(new SummonCustomEntityCommand());
        getCommand("resourcepacku").setExecutor(new ResourcePackCommand());
        getCommand("helpu").setExecutor(CustomHelpCommand.getInstance());
        getCommand("attributeu").setExecutor(new CustomAttributeCommand());
        getCommand("clear_attribute_modifiers").setExecutor(new ClearAttributeCommand());
        getCommand("custom_entities").setExecutor(new LoadedCustomEntitiesCommand());
        getCommand("set_blocku").setExecutor(new SetCustomBlockCommand());
        getCommand("custom_block_entities").setExecutor(new LoadedCustomBlocksCommand());
        getCommand("play_soundu").setExecutor(new CustomSoundCommand());

        // TICKERS
        CustomEntityManager.getInstance().start(this);
        CustomBlockManager.getInstance().start(this);
        CustomHudManager.getInstance().start(this);

        PacketHandler.initialize();

        SerializableDataManager.loadConfig();
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
            public void run() {
                InitializationProcess.start();
            }
        });
    }


    @Override
    public void onDisable() {
        SerializableDataManager.saveConfig();
    }
}
