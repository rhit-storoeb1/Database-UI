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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class PerformanceController implements Initializable {

    @FXML
    private TableView<PerformanceTable> table;
    @FXML
    private TableColumn<PerformanceTable, String> eventName;
    @FXML
    private TableColumn<PerformanceTable, String> mark;
    @FXML
    private TableColumn<PerformanceTable, String> place;

    private ObservableList<PerformanceTable> oblist = FXCollections.observableArrayList();

    public void showActivities() {
        table.getItems().clear();
        //String query = "SELECT AthleteID, MeetID, EventName, Mark, Place FROM RacedIn WHERE AthleteID = ?";

        try {
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call ViewPerformances(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setInt(2, Main.id);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                oblist.add(new PerformanceTable(
                        rs.getString("EventName"),
                        rs.getString("Mark"),
                        rs.getString("Place")));
            }
        }catch(SQLException ex){
            ex.printStackTrace();
            return;
        }

        this.eventName.setCellValueFactory(new PropertyValueFactory<>("EventName"));
        this.mark.setCellValueFactory(new PropertyValueFactory<>("Mark"));
        this.place.setCellValueFactory(new PropertyValueFactory<>("Place"));

        table.setItems(oblist);

        Main.db.closeConnection();
        return;

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
