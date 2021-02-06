package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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


    private ObservableList<ActivityFeedTable> activitylist = FXCollections.observableArrayList();

    public void showActivityFeed(){
        String query = "SELECT Athlete.ID, FirstName, LastName, Distance, Time, Pace, Date"
            + " FROM IsFriendsWith f"
            + " RIGHT JOIN Activity a1 ON (a1.AthleteID=f.Athlete1ID OR a1.AthleteID=f.Athlete2ID) AND a1.AthleteID<>" + Main.id
            + " JOIN Athlete ON Athlete.ID = a1.AthleteID"
            + " WHERE f.Athlete2ID = " + Main.id + "OR f.Athlete2ID = " + Main.id
            + " ORDER BY Date desc";
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall(query);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                String pace = rs.getString("Pace");
                if(pace.substring(pace.indexOf(':')+1, pace.length()).length()==1){
                    pace = pace + "0";
                }
                activitylist.add(new ActivityFeedTable(rs.getString("ID"),
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Main.id=1;
        showActivityFeed();
    }
}