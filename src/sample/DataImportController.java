package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class DataImportController {

    @FXML
    private TextField ImportPath;


    public void runDataImport(){

        //System.out.println(ImportPath.getText());
        DataImport di = new DataImport();
        try {
            di.importData(ImportPath.getText());
        } catch (IOException e) {
            e.printStackTrace();
       }
    }

    public void goToLogin(ActionEvent event) throws IOException {
        Parent login = FXMLLoader.load(getClass().getResource("../ui/Login.fxml"));
        Scene loginScene = new Scene(login);
        //get stage
        Stage loginStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        loginStage.setScene(loginScene);
        loginStage.show();
    }
}
