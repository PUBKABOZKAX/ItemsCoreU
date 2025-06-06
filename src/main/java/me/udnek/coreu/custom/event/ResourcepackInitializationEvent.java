package me.udnek.coreu.custom.event;

import me.udnek.coreu.resourcepack.path.VirtualRpJsonFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ResourcepackInitializationEvent extends CustomEvent{

    protected List<VirtualRpJsonFile> files = new ArrayList<>();
    protected List<VirtualRpJsonFile> forced = new ArrayList<>();

    public ResourcepackInitializationEvent(){
    }

    public void addFile(@NotNull VirtualRpJsonFile file){
        files.add(file);
    }
    public void forceAddFile(@NotNull VirtualRpJsonFile file){
        forced.add(file);
    }

    public @NotNull List<VirtualRpJsonFile> getFiles() {
        return files;
    }

    public @NotNull List<VirtualRpJsonFile> getForcedFiles() {return forced;}
}
