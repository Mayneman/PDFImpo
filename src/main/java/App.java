/**
 * Created by Lewis White 30/11/2018
 */


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {


    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("view/main.fxml"));
        primaryStage.setTitle("Impo Application");
        primaryStage.setScene(new Scene(root,1280,800));
        primaryStage.show();
    }

}
