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

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.Base64;
import java.util.Random;

public class LoginController {


    private static final Random RANDOM = new SecureRandom();
    private static final Base64.Encoder enc = Base64.getEncoder();
    private static final Base64.Decoder dec = Base64.getDecoder();

    @FXML
    private TextField username;
    @FXML
    private TextField password;

    public void login(ActionEvent event) throws IOException{

        //String query = "SELECT PasswordSalt, PasswordHash, AthleteID FROM [User] WHERE Username = '" + username.getText() + "'";
        try{
            Main.db.connect();
            CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call GetLogin(?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, username.getText());
            ResultSet rs = stmt.executeQuery();
            rs.next();

            String salt = rs.getString("PasswordSalt");
            String pass = rs.getString("PasswordHash");
            String hashed = hashPassword(dec.decode(salt), password.getText());
            if(hashed.equals(pass)){
                //login successful. store athleteID, go to athlete page
                //System.out.println("Login Successful");
                //store athleteID somewhere
                Main.id = rs.getInt("AthleteID");
                goToAthletePage(event);
            }else{
                //login failed, display fail message
                //System.out.println("login failed");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setContentText("Something went wrong while trying to login");
                alert.show();
            }

        }catch(SQLException e){
            int errorCode = e.getErrorCode();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Something went wrong while trying to login");
            alert.show();
            //e.printStackTrace();
        }

    }

    public void goToRegistration(ActionEvent event) throws IOException {
        Parent register = FXMLLoader.load(getClass().getResource("../ui/Register.fxml"));
        Scene registerScene = new Scene(register);
        //get stage
        Stage registerStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        registerStage.setScene(registerScene);
        registerStage.show();
    }

    public void goToDataImport(ActionEvent event) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../ui/DataImport.fxml"));
        Scene scene = new Scene(parent);
        //get stage
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void goToAthletePage(ActionEvent event) throws IOException{
        Parent activity = FXMLLoader.load(getClass().getResource("../ui/Athlete.fxml"));
        Scene activityScene = new Scene(activity);
        //get stage
        Stage activityStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        activityStage.setScene(activityScene);
        activityStage.show();
    }

    public byte[] getNewSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    public String getStringFromBytes(byte[] data) {
        return enc.encodeToString(data);
    }

    public String hashPassword(byte[] salt, String password) {

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory f;
        byte[] hash = null;
        try {
            f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash = f.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException e) {
            JOptionPane.showMessageDialog(null, "An error occurred during password hashing. See stack trace.");
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            JOptionPane.showMessageDialog(null, "An error occurred during password hashing. See stack trace.");
            e.printStackTrace();
        }
        return getStringFromBytes(hash);
    }
}
