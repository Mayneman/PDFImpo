package controller;

import impoClasses.Config;
import impoClasses.HotFolderConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class ConfigController extends CommonController{

    public Button chooseOut;
    public Button okButton;
    public TextField tempSource;
    public TextField inputSource;
    public TextField timeDelay;
    public Controller parent;

    public void setParent(Controller parent){
        this.parent = parent;
        if(parent.config != null) {
            if (parent.config.getTempSource() != null) {
                tempSource.setText(parent.config.getTempSource().getPath());
            }
            if (parent.config.getInputSource() != null) {
                inputSource.setText(parent.config.getInputSource().getPath());
            }
            if (parent.config.getTimeDelay() != null) {
                Integer time = parent.config.getTimeDelay()/1000;
                timeDelay.setText(time.toString());
            }
        }
    }

    public void chooseInputSource(){
        File file = chooseFolder(inputSource);
        if(file != null) {
            inputSource.setText(file.getPath());
        }
    }

    public void chooseTempSource(){
        File file = chooseFolder(tempSource);
        if(file != null) {
            tempSource.setText(file.getPath());
        }
    }

    public File chooseFolder(TextField textField){
        DirectoryChooser directoryChooser = new DirectoryChooser();

        return directoryChooser.showDialog(textField.getScene().getWindow());
    }

    public void configOK() {
        File in = new File(inputSource.getText());
        File temp = new File(tempSource.getText());
        boolean notValid = false;

        if (!in.exists()) {
            inputSource.setStyle("-fx-control-inner-background: #ff827b;");
            notValid = true;
        } else inputSource.setStyle("-fx-control-inner-background: #ffffff");
        if (!temp.exists()) {
            tempSource.setStyle("-fx-control-inner-background: #ff827b;");
            notValid = true;
        } else tempSource.setStyle("-fx-control-inner-background: #ffffff");

        notValid = checkInteger(timeDelay);
        Integer time = Integer.parseInt(timeDelay.getText())*1000;

        if(notValid)return;

    parent.saveConfig(new Config(in, temp, time));
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
}
