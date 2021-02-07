package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    private TableView<CommentTable> table;
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

    private int AthleteID;
    private int ActivityID;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showLikes();
        showName();
    }

    public void showComments(){

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
            e.printStackTrace();
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
        String query = "INSERT INTO CommentsOn (AthleteID, ActivityID, Content) VALUES (" + this.AthleteID + ", " + this.ActivityID + ", " + commentfield.getText() + ")";
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall(query);
            stmt.execute();
        }catch(SQLException e){
            e.printStackTrace();
        }
        //update Comments table after this
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
