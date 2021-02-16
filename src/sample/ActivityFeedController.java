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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ResourceBundle;

public class ActivityFeedController implements Initializable {

    @FXML
    private TableView<ActivityFeedTable> table;
    @FXML
    private TableColumn<ActivityFeedTable, String> fname;
    @FXML
    private TableColumn<ActivityFeedTable, String> lname;
    @FXML
    private TableColumn<ActivityFeedTable, String> distance;
    @FXML
    private TableColumn<ActivityFeedTable, String> time;
    @FXML
    private TableColumn<ActivityFeedTable, String> pace;
    @FXML
    private TableColumn<ActivityFeedTable, String> date;

    public static int tempActivityID;
    public static int tempAthleteID;


    private ObservableList<ActivityFeedTable> activitylist = FXCollections.observableArrayList();

    public void showActivityFeed(){
//        String query = "SELECT a1.ID AS ActivityID, Athlete.ID, FirstName, LastName, Distance, Time, Pace, Date"
//            + " FROM IsFriendsWith f"
//            + " RIGHT JOIN Activity a1 ON (a1.AthleteID=f.Athlete1ID OR a1.AthleteID=f.Athlete2ID) AND a1.AthleteID<>" + Main.id
//            + " JOIN Athlete ON Athlete.ID = a1.AthleteID"
//            + " WHERE f.Athlete2ID = " + Main.id + "OR f.Athlete2ID = " + Main.id
//            + " ORDER BY Date desc";
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call GetActivityFeed(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, Main.id);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                String pace = rs.getString("Pace");
                if(pace.substring(pace.indexOf(':')+1, pace.length()).length()==1){
                    pace = pace + "0";
                }
                activitylist.add(new ActivityFeedTable(rs.getString("ActivityID"),
                        rs.getString("ID"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Distance"),
                        rs.getString("Time"),
                        pace,
                        rs.getString("Date")));
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        this.fname.setCellValueFactory(new PropertyValueFactory<>("FirstName"));
        this.lname.setCellValueFactory(new PropertyValueFactory<>("LastName"));
        this.distance.setCellValueFactory(new PropertyValueFactory<>("Distance"));
        this.time.setCellValueFactory(new PropertyValueFactory<>("Time"));
        this.pace.setCellValueFactory(new PropertyValueFactory<>("Pace"));
        this.date.setCellValueFactory(new PropertyValueFactory<>("Date"));

        table.setItems(activitylist);



    }

    public void goToLikeComment(ActionEvent event) throws IOException {
        String activityID = table.getSelectionModel().getSelectedItem().ActivityID;
        String athleteID = table.getSelectionModel().getSelectedItem().AthleteID;
        tempActivityID = Integer.parseInt(activityID);
        tempAthleteID = Integer.parseInt(athleteID);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../ui/LikeComment.fxml"));
        Parent register = loader.load();
        Scene registerScene = new Scene(register);
        Stage registerStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        registerStage.setScene(registerScene);
        registerStage.show();
    }

    public void goToAthletePage(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../ui/Athlete.fxml"));
        Scene scene = new Scene(parent);
        //get stage
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showActivityFeed();
    }
}
