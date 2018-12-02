package controller;


import javafx.scene.control.TextField;

import java.io.File;

public class CommonController {

    public boolean checkInput(File file, TextField textField){
        if (textField.getText() == null) {
            textField.setStyle("-fx-control-inner-background: #ff827b;");
            return true;
        } else {
            textField.setStyle("-fx-control-inner-background: #ffffff");
            if (!file.exists()) {
                if(!file.mkdir()){
                    System.out.println("File creation error");
                }
            }
        }
        return false;
    }

    public boolean checkInteger(TextField textField) {
        try {
            Integer.parseInt(textField.getText());
            textField.setStyle("-fx-control-inner-background: #ffffff");
        } catch (NumberFormatException e) {
            textField.setStyle("-fx-control-inner-background: #ff827b;");
            return true;
        }
        return false;
    }
}
