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
import java.sql.Types;
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
        loadData();
        showComments();
    }

    public void loadData(){
       // String query = "SELECT Distance, Time, Pace, Date FROM Activity WHERE ID = " + this.ActivityID;
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call LoadData(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, this.ActivityID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            this.distance.setText(Main.truncateDecimal(rs.getString("Distance")));
            this.time.setText(Main.trimTime(rs.getString("Time")));
            this.pace.setText(rs.getString("Pace"));
            String dateString = rs.getString("Date");
            if(dateString!=null){
                this.date.setText(dateString);
            }
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(errorCode==1){
                alert.setContentText("Tried to load data from an activity that does not exist");
            }else{
                alert.setContentText("Something went wrong. Please try again");
            }
            alert.show();
            //e.printStackTrace();
        }
    }

    public void showComments(){
        this.commentstable.getItems().clear();
//        String query = "SELECT FirstName, LastName, Content FROM [Comments On] " +
//                        "JOIN Athlete ON Athlete.ID = [Comments On].AthleteID " +
//                        "WHERE ActivityID = " + this.ActivityID;
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call ShowComments(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, this.ActivityID);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                commentlist.add(new CommentTable(rs.getString("FirstName") + " " + rs.getString("LastName"),
                        rs.getString("Content")));
            }
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(errorCode==1){
                alert.setContentText("Tried to get comments from an activity that does not exist");
            }else{
                alert.setContentText("Something went wrong. Please try again");
            }
            alert.show();
            //e.printStackTrace();
        }
        this.namecolumn.setCellValueFactory(new PropertyValueFactory<>("Name"));
        this.commentcolumn.setCellValueFactory(new PropertyValueFactory<>("Comment"));

        commentstable.setItems(commentlist);
    }

    public void showLikes(){ //done on startup + when likes update
        //String query = "SELECT COUNT(*) FROM Likes WHERE ActivityID = " + ActivityID;
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call ShowLikes(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, this.ActivityID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            String count = rs.getString(1);
            likes.setText(count);
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(errorCode==1){
                alert.setContentText("Tried to get the likes from an activity that does not exist");
            }else{
                alert.setContentText("Something went wrong. Please try again");
            }
            alert.show();
            //e.printStackTrace();
        }
    }

    public void addLikes(){ //activates on button press
        //String query = "INSERT INTO Likes (AthleteID, ActivityID) VALUES (" + this.AthleteID + ", " + this.ActivityID + ")";
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call AddLike(?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, this.AthleteID);
            stmt.setInt(3, this.ActivityID);
            stmt.execute();
        }catch(SQLException e){
            int errorCode = e.getErrorCode();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(errorCode==1){
                alert.setContentText("Error with liking this run with current user's ID");
            }else if(errorCode==2){
                alert.setContentText("Tried liking an activity that does not exist");
            }else if(errorCode==3){
                alert.setContentText("You have already liked this post");
            }else{
                alert.setContentText("You have already liked this post");
            }
            alert.show();
            //e.printStackTrace();
        }
        showLikes();
    }

    public void showName(){
        //System.out.println(this.ActivityID);
        //String query = "SELECT FirstName, LastName FROM Athlete WHERE ID = (SELECT AthleteID FROM Activity WHERE ID = " + this.ActivityID + ")";
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call ShowName(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, this.ActivityID);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            name.setText(rs.getString(1) + " " + rs.getString(2));
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(errorCode==1){
                alert.setContentText("Tried to get the name from an athlete who does not exist");
            }else{
                alert.setContentText("Something went wrong. Please try again");
            }
            alert.show();
            //e.printStackTrace();
        }
    }

    public void removeLikes(){ //activates on button press
        //String query = "DELETE FROM Likes WHERE AthleteID = " + this.AthleteID + " AND ActivityID = " + this.ActivityID;
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call DeleteLike(?, ?)}");
            stmt.registerOutParameter(1,Types.INTEGER);
            stmt.setInt(2, this.AthleteID);
            stmt.setInt(3, this.ActivityID);
            stmt.execute();
        }catch(SQLException e){
            int errorCode = e.getErrorCode();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(errorCode==1){
                alert.setContentText("Tried to remove like, but had an error with current user's ID");
            }else if(errorCode==2){
                alert.setContentText("Tried to remove a like from an activity that does not exist");
            }else{
                alert.setContentText("Something went wrong. Please try again");
            }
            alert.show();
            //e.printStackTrace();
        }
        showLikes();
    }

    public void addComment(){ //activates on button press
        //String query = "INSERT INTO [Comments On] (AthleteID, ActivityID, Content) VALUES (" + Main.id + ", " + this.ActivityID + ", '" + commentfield.getText() + "')";
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call AddComment(?, ?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, Main.id);
            stmt.setInt(3, this.ActivityID);
            stmt.setString(4, commentfield.getText());
            stmt.execute();
        }catch(SQLException e){
            int errorCode = e.getErrorCode();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(errorCode==1){
                alert.setContentText("Tried to add a comment, but had a problem with the current User's iD");
            }else if(errorCode==2){
                alert.setContentText("Tried to add a comment to an activity that does not exist");
            }else{
                alert.setContentText("Something went wrong. Please try again");
            }
            alert.show();
            //e.printStackTrace();
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
