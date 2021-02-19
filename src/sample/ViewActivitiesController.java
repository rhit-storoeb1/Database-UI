package sample;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import sql.DBConnector;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ViewActivitiesController implements Initializable {

    @FXML
    private TableView<ActivityTable> table;
    @FXML
    private TableColumn<ActivityTable, String> distance;
    @FXML
    private TableColumn<ActivityTable, String> time;
    @FXML
    private TableColumn<ActivityTable, String> pace;
    @FXML
    private TableColumn<ActivityTable, String> date;

    @FXML
    private TextField athleteIDbox;

    private ObservableList<ActivityTable> oblist = FXCollections.observableArrayList();


    public void showActivities() {
        table.getItems().clear();
        //String query = "SELECT ID, AthleteID, Distance, Time, Pace, Date FROM Activity WHERE AthleteID = ?";

        try {
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call ViewActivities(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, Main.id);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                String pace = rs.getString("Pace");
                if(pace.substring(pace.indexOf(':')+1, pace.length()).length()==1){
                    pace = pace + "0";
                }
                oblist.add(new ActivityTable(rs.getString("ID"),
                        rs.getString("AthleteID"),
                        rs.getString("Distance"),
                        rs.getString("Time"),
                        pace,
                        rs.getString("Date")));
            }
        }catch(SQLException e){
            int errorCode = e.getErrorCode();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(errorCode==1){
                alert.setContentText("Athlete cannot be null");
            }else{
                alert.setContentText("Something went wrong. Please try again");
            }
            alert.show();
            //e.printStackTrace();
            return;
        }

        this.distance.setCellValueFactory(new PropertyValueFactory<>("Distance"));
        this.time.setCellValueFactory(new PropertyValueFactory<>("Time"));
        this.pace.setCellValueFactory(new PropertyValueFactory<>("Pace"));
        this.date.setCellValueFactory(new PropertyValueFactory<>("Date"));

        table.setItems(oblist);

        Main.db.closeConnection();
        return;

    }

    public void changeSceneButtonPushed(ActionEvent event) throws IOException {
        Parent activity = FXMLLoader.load(getClass().getResource("../ui/Activity.fxml"));
        Scene activityScene = new Scene(activity);

        //get stage
        Stage activityStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        activityStage.setScene(activityScene);
        activityStage.show();

    }

    public void deleteActivity(ActionEvent event) throws IOException{
        String deleteID = table.getSelectionModel().getSelectedItem().ID;
        try{
            Main.db.connect();
            PreparedStatement stmt = Main.db.getConnection().prepareCall("{call DeleteActivity(?)}");
            stmt.setInt(1, Integer.parseInt(deleteID));
            stmt.execute();
            table.getItems().clear();
            this.showActivities();
        }catch(SQLException e){
            int errorCode = e.getErrorCode();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(errorCode==1){
                alert.setContentText("Activity does not exist");
            }else{
                alert.setContentText("Something went wrong. Please try again");
            }
            alert.show();
            //e.printStackTrace();
            return;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showActivities();
    }
}
