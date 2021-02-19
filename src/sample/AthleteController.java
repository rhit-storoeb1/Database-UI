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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ResourceBundle;

public class AthleteController implements Initializable {

    @FXML
    private TableView<FriendsTable> friendstable;
    @FXML
    private TableColumn<FriendsTable, String> friendscolumn;
    @FXML
    private TableView<PBTable> pbtable;
    @FXML
    private TableColumn<PBTable, String> eventcolumn;
    @FXML
    private TableColumn<PBTable, String> pbcolumn;

    private ObservableList<PBTable> pblist = FXCollections.observableArrayList();
    private ObservableList<FriendsTable> friendslist = FXCollections.observableArrayList();

    public static int currentID = -1;
    public static boolean isCurrentAthlete = true;

    @FXML
    private Text name;

    public void goToAddActivity(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../ui/Activity.fxml"));
        Scene scene = new Scene(parent);
        //get stage
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void goToViewActivities(ActionEvent event) throws IOException{
        Parent parent = FXMLLoader.load(getClass().getResource("../ui/ViewActivities.fxml"));
        Scene scene = new Scene(parent);
        //get stage
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void updateName(){
        //String query = "SELECT FirstName, LastName FROM Athlete WHERE ID = " + this.currentID;
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call UpdateName(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, this.currentID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            name.setText(rs.getString(1) + " " + rs.getString(2));


        }catch (SQLException e){
            int errorCode = e.getErrorCode();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(errorCode==1){
                alert.setContentText("The id for the Athlete cannot be null");
            }else{
                alert.setContentText("Something went wrong. Please try again");
            }
            alert.show();
            e.printStackTrace();
            //e.printStackTrace();
        }
    }

    //TODO: Have Blake help with this stored procedure cause its really confusing atm
    public void showFriends(){
        friendstable.getItems().clear();
        String query = "SELECT * FROM IsFriendsWith WHERE Athlete1ID = " + this.currentID + " OR Athlete2ID = " + this.currentID;
        String query2 = "SELECT ID, FirstName, LastName FROM Athlete WHERE ID = ";
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall(query);
            ResultSet rs = stmt.executeQuery();
            boolean hasFriends = false;
            while(rs.next()){
                hasFriends = true;
                //prepare new statement to get all
                String athlete1 = rs.getString(1);
                String athlete2 = rs.getString(2);
                if(Integer.parseInt(athlete1)==this.currentID){
                    query2 = query2 + athlete2 + " OR ID = ";
                }else {
                    query2 = query2 + athlete1 + " OR ID = ";
                }
            }
            if(hasFriends){
                query2 = query2.substring(0,query2.length()-8); //remove last OR ID =
            }else{
                return;
            }
            try{
                System.out.println(query2);
                CallableStatement stmt2 = Main.db.getConnection().prepareCall(query2);
                ResultSet rs2 = stmt2.executeQuery();

                while(rs2.next()){
                    String first = rs2.getString("FirstName");
                    String last = rs2.getString("LastName");
                    String id = rs2.getString("ID");
                    friendslist.add(new FriendsTable(id, first + " " + last));
                }
            }catch(SQLException e){
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.friendscolumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        friendstable.setItems(friendslist);

        Main.db.closeConnection();
        return;


    }
    public void showPBs(){
        pbtable.getItems().clear();
        //String query = "SELECT EventName, Mark FROM HasPBIn WHERE AthleteID = " + this.currentID;
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call ShowPBs(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, this.currentID);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                pblist.add(new PBTable(rs.getString("EventName"),
                        rs.getString("Mark")));
            }
        }catch(SQLException e){
            int errorCode = e.getErrorCode();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(errorCode==1){
                alert.setContentText("Cannot insert null for id of Athlete");
            }else{
                alert.setContentText("Something went wrong. Please try again");
            }
            alert.show();
            //e.printStackTrace();
        }
        this.pbcolumn.setCellValueFactory(new PropertyValueFactory<>("PB"));
        this.eventcolumn.setCellValueFactory(new PropertyValueFactory<>("Event"));
        pbtable.setItems(pblist);
        return;

    }

    public void returnToCurrentAthlete(ActionEvent event) throws IOException{
        this.currentID = Main.id;
        this.isCurrentAthlete = true;
        Parent parent = FXMLLoader.load(getClass().getResource("../ui/Athlete.fxml"));
        Scene scene = new Scene(parent);
        //get stage
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(this.isCurrentAthlete){
            this.currentID = Main.id;
        }
        updateName();
        showFriends();
        showPBs();
    }

    public void goToRacePerformances(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../ui/PerformanceView.fxml"));
        Scene scene = new Scene(parent);
        //get stage
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void goToFriendsPage(ActionEvent event) throws IOException{
        //get friends athleteID, set to local variable. fetch from here
        String friendID = friendstable.getSelectionModel().getSelectedItem().id;
        this.currentID = Integer.parseInt(friendID);
        this.isCurrentAthlete = false;

        Parent parent = FXMLLoader.load(getClass().getResource("../ui/Athlete.fxml"));
        Scene scene = new Scene(parent);
        //get stage
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void goToActivityFeed(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../ui/ActivityFeed.fxml"));
        Scene scene = new Scene(parent);
        //get stage
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

}
