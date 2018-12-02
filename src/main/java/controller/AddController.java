package controller;


import impoClasses.HotFolderConfig;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;


public class AddController extends CommonController{

    public Button chooseOut;
    public TextField inputFolder;
    public TextField outputFolder;
    public TextField unitQuant;
    public TextField impoNup;
    public TextField backs;
    public CheckBox stepAndRepeat;
    public Controller parent;
    public Button okButton;
    public Text configError;

    private Stage stage;


    public void setParent(Controller parent) {
        this.parent = parent;
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
        if(parent.config == null){
            configError.setVisible(true);
            return;
        } else {
            configError.setVisible(false);
        }
        File fI = new File(parent.config.getInputSource().toPath().toString() + '\\' +inputFolder.getText());
        File fO = new File(outputFolder.getText());
        boolean notValid = false;
        notValid = checkInput(fI, inputFolder);

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

        HotFolderConfig hotFolderConfig = new HotFolderConfig(inputFolder.getText(), new File(outputFolder.getText()),
                Integer.parseInt(unitQuant.getText()), Integer.parseInt(impoNup.getText()), Integer.parseInt(backs.getText()), stepAndRepeat.isSelected());
        parent.addRow(hotFolderConfig);
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }



}
