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
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LikeCommentController implements Initializable {
    @FXML
    private TableView<CommentTable> commentstable;
    @FXML
    private TableColumn<CommentTable, String> namecolumn;
    @FXML
    private TableColumn<CommentTable, String> commentcolumn;
    @FXML
    private Text distance;
    @FXML
    private Text time;
    @FXML
    private Text pace;
    @FXML
    private Text date;
    @FXML
    private Text name;
    @FXML
    private Text likes;
    @FXML
    private TextField commentfield;

    private ObservableList<CommentTable> commentlist = FXCollections.observableArrayList();


    public int AthleteID = -1;
    public int ActivityID = -1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setInfo(ActivityFeedController.tempAthleteID, ActivityFeedController.tempActivityID);
        showLikes();
        showName();
        showComments();
    }

    public void showComments(){
        this.commentstable.getItems().clear();
        String query = "SELECT FirstName, LastName, Content FROM [Comments On] " +
                        "JOIN Athlete ON Athlete.ID = [Comments On].AthleteID " +
                        "WHERE ActivityID = " + this.ActivityID;
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall(query);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                commentlist.add(new CommentTable(rs.getString("FirstName") + " " + rs.getString("LastName"),
                        rs.getString("Content")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.namecolumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        this.commentcolumn.setCellValueFactory(new PropertyValueFactory<>("Comment"));

        commentstable.setItems(commentlist);
    }

    public void showLikes(){ //done on startup + when likes update
        String query = "SELECT COUNT(*) FROM Likes WHERE ActivityID = " + ActivityID;
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall(query);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            String count = rs.getString(1);
            likes.setText(count);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addLikes(){ //activates on button press
        String query = "INSERT INTO Likes (AthleteID, ActivityID) VALUES (" + this.AthleteID + ", " + this.ActivityID + ")";
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall(query);
            stmt.execute();
        }catch(SQLException e){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("You have already Liked this activity");
            alert.show();
        }
        showLikes();
    }

    public void showName(){
        System.out.println(this.ActivityID);
        String query = "SELECT FirstName, LastName FROM Athlete WHERE ID = (SELECT AthleteID FROM Activity WHERE ID = " + this.ActivityID + ")";
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall(query);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            name.setText(rs.getString(1) + " " + rs.getString(2));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void removeLikes(){ //activates on button press
        String query = "DELETE FROM Likes WHERE AthleteID = " + this.AthleteID + " AND ActivityID = " + this.ActivityID;
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall(query);
            stmt.execute();
        }catch(SQLException e){
            e.printStackTrace();
        }
        showLikes();
    }

    public void addComment(){ //activates on button press
        String query = "INSERT INTO [Comments On] (AthleteID, ActivityID, Content) VALUES (" + Main.id + ", " + this.ActivityID + ", '" + commentfield.getText() + "')";
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall(query);
            stmt.execute();
        }catch(SQLException e){
            e.printStackTrace();
        }
        showComments();
    }

    public void goToActivityFeed(ActionEvent event) throws IOException {
        Parent activity = FXMLLoader.load(getClass().getResource("../ui/ActivityFeed.fxml"));
        Scene activityScene = new Scene(activity);
        Stage activityStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        activityStage.setScene(activityScene);
        activityStage.show();
    }

    public void setInfo(int AthleteID, int ActivityID){
        this.AthleteID=AthleteID;
        this.ActivityID=ActivityID;
    }
}
