package controller;


import impoClasses.HotFolderConfig;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;


public class EditController extends CommonController{

    public Button chooseOut;
    public TextField inputFolder;
    public TextField outputFolder;
    public TextField unitQuant;
    public TextField impoNup;
    public TextField backs;
    public CheckBox stepAndRepeat;
    public Controller parent;
    public HotFolderConfig hotFolderConfig;
    public Button okButton;

    private Stage stage;


    public void setParent(Controller parent) {
        this.parent = parent;
    }

    public void setCurrentHotFolder(HotFolderConfig hotFolderConfig){
        this.hotFolderConfig = hotFolderConfig;
        inputFolder.setText(hotFolderConfig.getInputFolder());
        outputFolder.setText(hotFolderConfig.getOutputFolder().toString());
        unitQuant.setText(hotFolderConfig.getUnitQuant().toString());
        impoNup.setText(hotFolderConfig.getImpoNup().toString());
        stepAndRepeat.setSelected(hotFolderConfig.getStepAndRepeat());
        backs.setText(hotFolderConfig.getBacks().toString());
    }

    public void chooseOutputFolder() {
        File file = chooseFolder();
        if (file != null) {
            outputFolder.setText(file.getPath());
        }
    }

    public File chooseFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();

        return directoryChooser.showDialog(inputFolder.getScene().getWindow());
    }

    public void addRowOk() {
        File fI = new File(parent.config.getInputSource().toPath().toString() + File.separator + inputFolder.getText());
        File fO = new File(outputFolder.getText());

        boolean notValid = false;


        if (inputFolder.getText() == null) {
            inputFolder.setStyle("-fx-control-inner-background: #ff827b;");
        } else {
            inputFolder.setStyle("-fx-control-inner-background: #ffffff");
            if (!fI.exists()) {
                if(!fI.mkdir()){
                    System.out.println("inputFolder creation error");
                }
            }
        }

        if (!fO.exists()) {
            outputFolder.setStyle("-fx-control-inner-background: #ff827b;");
            notValid = true;
        } else outputFolder.setStyle("-fx-control-inner-background: #ffffff");
        boolean current = checkInteger(unitQuant);
        if(!notValid && current)notValid = true;
        current = checkInteger(impoNup);
        if(!notValid && current)notValid = true;
        current = checkInteger(backs);
        if(!notValid && current)notValid = true;

        if (notValid) return;

        HotFolderConfig newHotFolderConfig = new HotFolderConfig(inputFolder.getText(), new File(outputFolder.getText()),
                Integer.parseInt(unitQuant.getText()), Integer.parseInt(impoNup.getText()), Integer.parseInt(backs.getText()), stepAndRepeat.isSelected());
        parent.editRow(hotFolderConfig, newHotFolderConfig);
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

}
