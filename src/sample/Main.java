package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sql.DBConnector;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class Main extends Application {

    public static DBConnector db = new DBConnector();
    private Connection con = null;
    public static int id = -1;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../ui/Login.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("TrackFlo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public String calculatePace(String distanceRun, String timeRun){
        double dist;
        int minutes = Integer.parseInt(timeRun.substring(0, timeRun.indexOf(':')));
        int seconds = Integer.parseInt(timeRun.substring(timeRun.indexOf(':')+1, timeRun.length()));
        try{
            dist = Double.parseDouble(distanceRun);
        }catch(Exception e){
            System.out.println("distance error");
            return "Distance error";
        }
        double calcTime = minutes + ((double)seconds/60);
        double paceMin = (calcTime/dist);
        double paceSec = (paceMin - ((int) paceMin))*60;
        String finalProduct = ((int) paceMin) + ":" + ((int) paceSec);
        if(finalProduct.substring(finalProduct.indexOf(':')+1,finalProduct.length()).length()==1){
            finalProduct += "0";
        }
        System.out.println(finalProduct);
        return finalProduct;
    }

}
