package controller;

import com.google.gson.Gson;
import impoClasses.Config;
import impoClasses.HotFolderConfig;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.Watcher;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;



public class Controller extends CommonController{

    @FXML
    public Button addButton;
    public TableView mainTable;
    public TableColumn<HotFolderConfig, String> inColumn;
    public TableColumn<HotFolderConfig, File> outColumn;
    public TableColumn<HotFolderConfig, Integer> qtyColumn;
    public TableColumn<HotFolderConfig, Integer> impoNupColumn;
    public TableColumn<HotFolderConfig, Boolean> stepAndRepeatColumn;
    public TableColumn<HotFolderConfig, Integer> backsColumn;
    public Config config;
    public TextArea activityLog;

    public Circle watcherStatColour;
    public Text watcherStatText;

    private int watchersRunning = 0;
    private Watcher watcher;
    private Stage stage;
    private Timeline flasher;

    @FXML
    public void initialize(){
        inColumn.setCellValueFactory(new PropertyValueFactory<>("inputFolder"));
        outColumn.setCellValueFactory(new PropertyValueFactory<>("outputFolder"));
        qtyColumn.setCellValueFactory(new PropertyValueFactory<>("unitQuant"));
        impoNupColumn.setCellValueFactory(new PropertyValueFactory<>("impoNup"));
        stepAndRepeatColumn.setCellValueFactory(new PropertyValueFactory<>("stepAndRepeat"));
        stepAndRepeatColumn.setCellValueFactory(new PropertyValueFactory<>("stepAndRepeat"));
        backsColumn.setCellValueFactory(new PropertyValueFactory<>("backs"));
        //FLASHER
        flasher = new Timeline(new KeyFrame(javafx.util.Duration.seconds(0.5), e -> {
            watcherStatColour.setFill(Color.LIME);
        }),
                new KeyFrame(javafx.util.Duration.seconds(1.0), e -> {
                    watcherStatColour.setFill(Color.LIGHTGREEN);
                })
        );
        flasher.setCycleCount(Animation.INDEFINITE);
        System.out.println(mainTable);
        mainTable.setRowFactory( tv -> {
            TableRow<HotFolderConfig> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    HotFolderConfig rowData = row.getItem();
                    newEditWindow(rowData);
                }
            });
            return row ;
        });
        Gson gson = new Gson();
        watcher = new Watcher();
        try {
            File config = new File(System.getProperty("user.dir") + "/Data.json");
            if(!config.exists()){
                if(!config.createNewFile()){
                    System.out.println("Data.json creation error");
                    newActivity("Data.json creation error");
                }
            } else {
                HotFolderConfig[] hotFolderConfigs = gson.fromJson(new FileReader(System.getProperty("user.dir") + "/Data.json"), HotFolderConfig[].class);
                if(hotFolderConfigs != null) {
                    for (HotFolderConfig hotFolderConfig : hotFolderConfigs) {
                        mainTable.getItems().add(hotFolderConfig);
                    }
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        try {
            File config = new File(System.getProperty("user.dir") + "/config.json");
            if(!config.exists()){
                if(!config.createNewFile()){
                    System.out.println("config.json creation error");
                    newActivity("config.json creation error");
                }
            } else {
                this.config = gson.fromJson(new FileReader(System.getProperty("user.dir") + "/config.json"), Config.class);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }



    public void newAddWindow(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/addWindow.fxml"));
            Parent root = loader.load();
            AddController addController = loader.getController();
            addController.setParent(this);
            stage = new Stage();
            stage.setTitle("Add Row");
            stage.setScene(new Scene(root, 350, 400));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void newEditWindow(HotFolderConfig hotFolderConfig){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/editWindow.fxml"));
            Parent root = loader.load();
            EditController editController = loader.getController();
            editController.setParent(this);
            editController.setCurrentHotFolder(hotFolderConfig);
            stage = new Stage();
            stage.setTitle("Edit Row");
            stage.setScene(new Scene(root, 350, 400));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void editRow(HotFolderConfig current, HotFolderConfig newConfig){
        try{
            for(int i = 0; i < mainTable.getItems().size(); i++){
                if(mainTable.getItems().get(i) == current){
                    mainTable.getItems().set(i, newConfig);
                    break;
                }
            }
            saveTable();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void newConfigWindow(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/configWindow.fxml"));
            Parent root = loader.load();
            ConfigController configController = loader.getController();
            configController.setParent(this);
            stage = new Stage();
            stage.setTitle("Config");
            stage.setScene(new Scene(root, 350, 400));
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig(Config config){
        Gson gson = new Gson();
        try {
            this.config = config;
            FileWriter writer = new FileWriter("config.json");
            gson.toJson(config,writer);
            writer.flush();
            writer.close();
        } catch (IOException e){
            System.out.println("Caught IO Exception");
            newActivity("Caught IO Exception");
        }

    }

    public void addRow(HotFolderConfig hotFolderConfig){
        mainTable.getItems().add(hotFolderConfig);
        saveTable();
    }

    public void removeRow(){
        mainTable.getItems().remove(mainTable.getSelectionModel().getSelectedItem());
        saveTable();
    }

    private void saveTable(){
        Gson gson = new Gson();
        try {
            FileWriter writer = new FileWriter("data.json");
            gson.toJson(mainTable.getItems(),writer);
            writer.flush();
            writer.close();
        } catch (IOException e){
            System.out.println("Caught IO Exception");
            newActivity("Caught IO Exception");
        }

    }
    @FXML
    private void startWatchers(){
        stopWatchers();
        for(Object object : mainTable.getItems()){
            HotFolderConfig hotFolderConfig = (HotFolderConfig) object;
            Path path = Paths.get((config.getInputSource() + "/" + hotFolderConfig.getInputFolder()));
            watcher.watch(path, hotFolderConfig,config.getTempSource(), config.getTimeDelay(),this);
        }
    }

    public void incrementWatcherStat(){
        watcherStatColour.setFill(Color.LIME);
        watchersRunning++;
        flasher.play();
        watcherStatText.setText(watchersRunning + " Watchers Running");
    }


    @FXML
    private void stopWatchers(){
        watcher.stopAllWatchers();
        flasher.stop();
        watcherStatColour.setFill(Color.LIGHTGREY);
        watchersRunning = 0;
        watcherStatText.setText(watchersRunning + " Watchers Running");


    }

    public void newActivity(String string){
        activityLog.setText(string + '\n' +activityLog.getText());
    }
}
