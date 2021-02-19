package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ResourceBundle;

import sql.DBConnector;


public class ActivityController{

    @FXML
    private TextField athleteID;
    @FXML
    private TextField distance;
    @FXML
    private TextField time;
    @FXML
    private TextField date;

    //handle changing scenes using button clicks
    public void changeSceneButtonPushed(ActionEvent event) throws IOException {
        Parent meet = FXMLLoader.load(getClass().getResource("../ui/AddMeetResults.fxml"));
        Scene meetScene = new Scene(meet);
        //get stage
        Stage meetStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        meetStage.setScene(meetScene);
        meetStage.show();
    }

    public void changeSceneButtonPushed2(ActionEvent event) throws IOException {
        Parent activity = FXMLLoader.load(getClass().getResource("../ui/ViewActivities.fxml"));
        Scene activityScene = new Scene(activity);
        //get stage
        Stage activityStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        activityStage.setScene(activityScene);
        activityStage.show();
    }

    public void addActivity(ActionEvent event) throws IOException {
        try{
            Main.db.connect();
            PreparedStatement stmt = Main.db.getConnection().prepareCall("{call InsertActivity(?, ?, ?, ?)}");
            stmt.setInt(1, Main.id); //AthleteID
            stmt.setFloat(2, Float.parseFloat(this.distance.getText().substring(0, 4))); //distance
            stmt.setTime(3, Time.valueOf(this.time.getText())); //time
            if(this.date.getText().isEmpty()){
                stmt.setString(4, null);
            }else{
                stmt.setString(4, this.date.getText());
            }
            stmt.execute();
            Main.db.closeConnection();
        }catch (SQLException e){
            int errorCode = e.getErrorCode();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(errorCode==1){
                alert.setContentText("Cannot insert null for id of Athlete");
            }else if(errorCode==2){
                alert.setContentText("This athlete does not exist");
            }else if(errorCode==3){
                alert.setContentText("Distance cannot be empty");
            }else if(errorCode==4){
                alert.setContentText("Time cannot be empty");
            }else{
                alert.setContentText("Something went wrong. Please try again");
            }
            alert.show();
            //e.printStackTrace();
        }catch(NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Must enter a valid number for distance");
            alert.show();
        }catch(IllegalArgumentException e){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Must enter a valid time field");
            alert.show();
        }
    }

    public void goToAthletePage(ActionEvent event) throws IOException {
        Parent register = FXMLLoader.load(getClass().getResource("../ui/Athlete.fxml"));
        Scene registerScene = new Scene(register);
        //get stage
        Stage registerStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        registerStage.setScene(registerScene);
        registerStage.show();
    }

}
