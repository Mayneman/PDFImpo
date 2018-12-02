package utils;

import controller.Controller;
import impoClasses.HotFolderConfig;
import javafx.application.Platform;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Watcher {

    private ArrayList<WatchService> watchServices = new ArrayList<>();
    private Controller parent;
    private Integer timeDelay;

    public void watch(Path path, HotFolderConfig hotFolderConfig, File tempFolder, Integer timeDelay, Controller parent) {
        this.parent = parent;
        this.timeDelay = timeDelay;
        File sourceFolder = path.toFile();

        new Thread(() -> {
            clearFolder(sourceFolder, tempFolder, hotFolderConfig);
            try (WatchService service = FileSystems.getDefault().newWatchService()) {
                Map<WatchKey, Path> keyMap = new HashMap<>();
                keyMap.put(path.register(service,
                        StandardWatchEventKinds.ENTRY_CREATE),
                        path);
                WatchKey watchKey;
                watchServices.add(service);
                System.out.println("Watcher started on: " + path.getFileName().toString());
                watcherNewActivity("Watcher started on: " + path.getFileName().toString());
                parent.incrementWatcherStat();

                do {
                    if (watchServices.contains(service)) {
                        try {
                            watchKey = service.take();
                        } catch (Exception e) {
                            return;
                        }
                    } else {
                        return;
                    }
                    Path eventDir = keyMap.get(watchKey);

                    for (WatchEvent<?> event : watchKey.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();
                        Path eventPath = (Path) event.context();
                        System.out.println(eventDir + ": " + kind + ": " + eventPath);
                        watcherNewActivity(eventDir + ": " + kind + ": " + eventPath);
                        if (!eventPath.toString().contains(".")) {
                            processFolder(sourceFolder, eventPath, tempFolder, hotFolderConfig);
                        }
                    }
                } while (watchKey.reset());
            } catch (NoSuchFileException e){
                e.printStackTrace();
                System.out.println("No such file: " + sourceFolder.toPath());
                watcherNewActivity("No such file: " + sourceFolder.toPath());
            } catch (Exception e) {
                System.out.println("run");
                watcherNewActivity("run");
                e.printStackTrace();
            }


        }).start();
    }

    private void clearFolder(File sourceFolder, File tempFolder, HotFolderConfig hotFolderConfig) {
        //for folder in path
        System.out.println("Clearing folder: " + sourceFolder);
        watcherNewActivity("Clearing folder: " + sourceFolder);
        String[] directories = sourceFolder.list((current, name) -> new File(current, name).isDirectory());
        if(directories == null){
            return;
        }
        for(String file : directories){
            Path path = Paths.get(file);
            try {
                processFolder(sourceFolder, path, tempFolder, hotFolderConfig);
            } catch (Exception e){
                System.out.println("Error in clearFolder: " + sourceFolder);
                watcherNewActivity("Error in clearFolder: " + sourceFolder);
                e.printStackTrace();
            }
        }
    }

    private void processFolder(File sourceFolder, Path eventPath, File tempFolder, HotFolderConfig hotFolderConfig) throws InterruptedException{
        System.out.println(sourceFolder.toPath() + "\\" + eventPath);
        watcherNewActivity(sourceFolder.toPath() + "\\" + eventPath);
        isCompletelyWritten(new File(sourceFolder.toPath() + "\\" + eventPath));
        File currentSource = new File(sourceFolder.toPath() + "\\" + eventPath);
        File currentTemp = new File(tempFolder.toPath() + "\\" + eventPath);
        ProcessFolder processFolder = new ProcessFolder(currentSource, hotFolderConfig, currentTemp, eventPath.toString(), this.parent, false);
        processFolder.run();
    }

    public void stopAllWatchers(){
        for(WatchService service : watchServices){
            try {
                service.close();
                System.out.println("Stopped Service");
                watcherNewActivity("Stopped Service");
            } catch (Exception e) {
                System.out.println("stopAllWatchers");
                e.printStackTrace();
            }
        }
        watchServices.clear();
    }

    private void isCompletelyWritten(File file) throws InterruptedException{
        Long fileSizeBefore = file.length();
        Thread.sleep(timeDelay);
        Long fileSizeAfter = file.length();
        System.out.println("Waiting for folder to copy...");
        watcherNewActivity("Waiting for folder to copy...");
        System.out.println("Waiting for " + timeDelay/1000 + " seconds");
        watcherNewActivity("Waiting for " + timeDelay/1000 + " seconds");
        if (!fileSizeBefore.equals(fileSizeAfter)) {
            isCompletelyWritten(file);
        } else {
            System.out.println("Starting to process PDFs");
            watcherNewActivity("Starting to process PDFs");
        }
    }

    private void watcherNewActivity(String string){
        Platform.runLater(() -> parent.newActivity(string));
    }
}
