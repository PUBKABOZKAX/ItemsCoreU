package me.udnek.itemscoreu.customminigame.game;

import me.udnek.itemscoreu.customminigame.MGUId;
import me.udnek.itemscoreu.customminigame.command.MGUCommandContext;
import me.udnek.itemscoreu.customminigame.command.MGUCommandType;
import me.udnek.itemscoreu.customminigame.map.MGUMap;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface MGUGameInstance {

    @NotNull MGUGameType getType();

    @NotNull MGUMap getMap();

    @NotNull MGUId getId();

    boolean isRunning();

    @NotNull MGUCommandType.ExecutionResult executeCommand(@NotNull MGUCommandContext context);
    @NotNull List<String> getCommandOptions(@NotNull MGUCommandContext context);
    boolean testCommandArgs(@NotNull MGUCommandContext context);
}
