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
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Base64;
import java.util.Random;


public class RegisterController {
    @FXML
    private TextField Rusername;
    @FXML
    private TextField Rpassword;
    @FXML
    private TextField Rteamname;
    @FXML
    private TextField RFName;
    @FXML
    private TextField RLName;

    private static final Random RANDOM = new SecureRandom();
    private static final Base64.Encoder enc = Base64.getEncoder();
    private static final Base64.Decoder dec = Base64.getDecoder();

    public void register() throws IOException{

        byte[] newSalt = getNewSalt();
        String hashedPass = hashPassword(newSalt, Rpassword.getText());
        int errorCode = 0;
        try{
            Main.db.connect();
            //user, salt, hash, teamname
            CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call Register(?, ?, ?, ?, ?, ?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, Rusername.getText());
            stmt.setString(3, getStringFromBytes(newSalt));
            stmt.setString(4, hashedPass);
            stmt.setString(5, Rteamname.getText());
            stmt.setString(6, RFName.getText());
            stmt.setString(7, RLName.getText());
            stmt.setString(8, null);
            stmt.execute();
            errorCode = stmt.getInt(1);

            //registration successful

            Main.db.closeConnection();
        } catch (SQLException e) {
            errorCode = e.getErrorCode();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(errorCode==1){
                alert.setContentText("Username field cannot be empty");
            }else if(errorCode==2){
                alert.setContentText("Password Salt cannot be empty");
            }else if(errorCode==3){
                alert.setContentText("Password Hash cannot be empty");
            }else if(errorCode==4){
                alert.setContentText("This username already exists");
            }else{
                alert.setContentText("Something went wrong. Please try again");
            }
            alert.show();
            //e.printStackTrace();
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
