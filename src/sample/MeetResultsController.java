package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;

public class MeetResultsController {

    @FXML
    private TextField event;
    @FXML
    private TextField mark;
    @FXML
    private TextField athleteID;
    @FXML
    private TextField meetID;
    @FXML
    private TextField place;

    //handle changing scenes using button clicks
    public void changeSceneButtonPushed(ActionEvent event) throws IOException {
        Parent activity = FXMLLoader.load(getClass().getResource("../ui/Activity.fxml"));
        Scene activityScene = new Scene(activity);

        //get stage
        Stage activityStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        activityStage.setScene(activityScene);
        activityStage.show();

    }
//AthleteID, MeetID, Eventname, Mark, Place
    public void addMeetResult(ActionEvent event) throws IOException {
        try{
            Main.db.connect();
            PreparedStatement stmt = Main.db.getConnection().prepareCall("{call AddRacedIn(?, ?, ?, ?, ?)}");
            stmt.setInt(1, Integer.parseInt(this.athleteID.getText())); //AthleteID
            stmt.setInt(2, Integer.parseInt(this.meetID.getText())); //MeetID
            stmt.setString(3, this.event.getText()); //EventName
            stmt.setTime(4, Time.valueOf(this.mark.getText())); //Mark
            stmt.setInt(5, Integer.parseInt(this.place.getText())); //Place
            stmt.execute();
            Main.db.closeConnection();
        }catch (SQLException e){
            int errorCode = e.getErrorCode();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(errorCode==1){
                alert.setContentText("Athlete cannot be null");
            }else if(errorCode==2){
                alert.setContentText("Meet cannot be null");
            }else if(errorCode==3){
                alert.setContentText("Event name cannot be null");
            }else if(errorCode==4){
                alert.setContentText("Athlete does not exist");
            }else if(errorCode==5){
                alert.setContentText("Meet does not exist");
            }else if(errorCode==6){
                alert.setContentText("Invalid event name");
            }else{
                alert.setContentText("Something went wrong. Please try again");
            }
            alert.show();
            //e.printStackTrace();
        }
    }
}