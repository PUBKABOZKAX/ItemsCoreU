package me.udnek.itemscoreu.resourcepack.merger;

import com.google.common.base.Preconditions;
import me.udnek.itemscoreu.resourcepack.FileType;
import me.udnek.itemscoreu.resourcepack.ResourcePackablePlugin;
import me.udnek.itemscoreu.resourcepack.VirtualResourcePack;
import me.udnek.itemscoreu.resourcepack.path.RpPath;
import me.udnek.itemscoreu.resourcepack.path.SamePathsContainer;
import me.udnek.itemscoreu.resourcepack.path.SortedPathsContainer;
import me.udnek.itemscoreu.util.LogUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ResourcePackMerger {

    public static final RpPath LANG_DIRECTORY = new RpPath(null, "assets/minecraft/lang");
    public static final RpPath ITEM_MODEL_DIRECTORY = new RpPath(null, "assets/minecraft/models/item");

    private static final RpPath[] MERGE_DIRECTORIES = new RpPath[]{LANG_DIRECTORY, ITEM_MODEL_DIRECTORY};

    private final SortedPathsContainer container = new SortedPathsContainer();

    private String extractDirectory;

    public ResourcePackMerger(){}

    public String checkExtractDirectory(String extractDirectory){
        if (extractDirectory == null) return "Directory can not be null!";
        File file = new File(extractDirectory);
        if (!file.isDirectory()) return "Specified path is not a directory!";
        if (!file.isAbsolute()) return "Directory must be absolute!";
        if (!file.exists()) return "Directory does not exists!";
        if (!file.canWrite()) return "Directory can not be written!";
        return null;
    }

    public void startGlobalMerging(@NotNull String extractDirectory){

        // TODO: 8/3/2024 MERGE ATLASES 
        // TODO: 8/3/2024 AUTO ADD SPACINGS
        
        
        this.extractDirectory = extractDirectory;
        Preconditions.checkArgument(checkExtractDirectory(extractDirectory) == null, "Extract directory can not be null!");
        LogUtils.pluginLog("ResourcePack merging started");
        List<VirtualResourcePack> resourcePacks = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (!(plugin instanceof ResourcePackablePlugin resourcePackable)) continue;
            LogUtils.pluginLog("Found resourcePackable plugin: " + plugin.getName());
            resourcePacks.add(resourcePackable.getResourcePack());
        }
        resourcePacks.forEach(VirtualResourcePack::initialize);

        resourcePacks.stream().flatMap(resourcePack ->
                resourcePack.getAllFoundFiles().stream()).forEach(container::add);

        for (SamePathsContainer containerSame : container.getSames()) {
            if (isInAutoMerge(containerSame.getExample())){
                autoMergeCopy(containerSame);
            } else {
                manualMergeCopy(containerSame);
            }
        }
        for (RpPath rpPath : container.getAllExcludingSame()) {
            copyFile(rpPath, rpPath);
        }

        LogUtils.pluginLog("DONE!");

    }

    public void autoMergeCopy(@NotNull SamePathsContainer container){
        RpFileMerger merger;
        if (container.getExample().isBelow(ITEM_MODEL_DIRECTORY)){
            merger = new ItemModelMerger();
        } else if (container.getExample().isBelow(LANG_DIRECTORY)){
            merger = new LanguageMerger();
        } else {
            throw new RuntimeException("Directory can not be merged automatically");
        }
        for (RpPath rpPath : container.getAll()) {
            merger.add(newBufferedReader(rpPath));
            LogUtils.pluginLog("Auto merging: " + rpPath);
        }
        merger.merge();
        RpPath rpPath = container.getExample();
        saveText(rpPath, merger.getMergedAsString());
    }

    public void manualMergeCopy(@NotNull SamePathsContainer container){
        if (extractFileExists(container.getExample())){
            LogUtils.pluginLog("Manual file already exists: " + container.getExample());
            return;
        }
        int mergeId = 0;
        for (RpPath rpPath : container.getAll()) {
            copyFile(rpPath, rpPath.withMergeId(mergeId));
            LogUtils.pluginWarning("Should be manually merged: " + rpPath.withMergeId(mergeId));
            mergeId++;
        }


    }
    public boolean isInAutoMerge(@NotNull RpPath rpPath){
        for (RpPath mergeDirectory : MERGE_DIRECTORIES) {
            if (rpPath.isBelow(mergeDirectory)) return true;
        }
        return false;
    }
    public BufferedWriter newBufferedWriter(@NotNull RpPath rpPath){
        Path path = Paths.get(rpPath.getExtractPath(extractDirectory));
        try {
            return Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        } catch (IOException e) {throw new RuntimeException(e);}
    }
    public BufferedReader newBufferedReader(@NotNull RpPath rpPath){
        InputStream stream = rpPath.getInputStream();
        return new BufferedReader(new InputStreamReader(stream));
    }

    public boolean extractFileExists(@NotNull RpPath rpPath){
        String filePath = rpPath.getExtractPath(extractDirectory);
        return new File(filePath).exists();
    }
    public void createNewFile(@NotNull RpPath rpPath){
        if (extractFileExists(rpPath)) return;
        String filePath = rpPath.getExtractPath(extractDirectory);
        String folderPath = rpPath.withLayerUp().getExtractPath(extractDirectory);
        new File(folderPath).mkdirs();
        try {
            new File(filePath).createNewFile();
        } catch (IOException e) {throw new RuntimeException(e);}
    }
    public void copyFile(@NotNull RpPath from, @NotNull RpPath to){
        createNewFile(to);

        switch (from.getFileType()) {
            case PNG -> copyImage(from, to);
            case OGG -> copySound(from, to);
            default -> copyText(from, to);
        }
    }
    public void copySound(@NotNull RpPath from, @NotNull RpPath to){
        Preconditions.checkArgument(from.getFileType() == FileType.OGG, "File " + to + " is not a sound!");

        try {
            InputStream input = from.getInputStream();
            OutputStream out = new FileOutputStream(to.getExtractPath(extractDirectory));

            byte[] buf = new byte[1024];
            int len;
            while ((len = input.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            input.close();
            out.close();

        } catch (Exception exception){
            throw new RuntimeException(exception);
        }

    }
    public void copyImage(@NotNull RpPath from, @NotNull RpPath to){
        Preconditions.checkArgument(from.getFileType() == FileType.PNG, "File " + to + " is not an image!");
        try {
            BufferedImage image = ImageIO.read(from.getInputStream());
            ImageIO.write(image, "png", new File(to.getExtractPath(extractDirectory)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void saveText(@NotNull RpPath to, @NotNull String text){
        createNewFile(to);

        BufferedWriter writer = newBufferedWriter(to);

        try {
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void copyText(@NotNull RpPath from, @NotNull RpPath to){
        try {
            BufferedWriter writer = newBufferedWriter(to);

            BufferedReader reader = newBufferedReader(from);
            List<String> lines = reader.lines().toList();
            reader.close();

            final long count = lines.size();

            final int[] i = {0};
            final boolean suppress = count > 80;
            lines.forEach(new Consumer<String>() {

                @Override
                public void accept(@NotNull String line) {

                    try {
                        writer.write(line + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    i[0]++;
                }

                public void debug(@NotNull String line){
                    if (suppress){
                        if(i[0] > 5 && i[0] < count-5) return;
                        LogUtils.pluginLog(line);
                        if (i[0] == 5){
                            LogUtils.pluginLog("................");
                            LogUtils.pluginLog("..*suppressed*..");
                            LogUtils.pluginLog("................");
                        }
                    }
                    else {
                        LogUtils.pluginLog(line);
                    }

                }
            });

            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
