package sample;

import javafx.scene.control.Alert;
import org.apache.poi.ss.usermodel.*;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;


public class DataImport {

    private static final Random RANDOM = new SecureRandom();
    private static final Base64.Encoder enc = Base64.getEncoder();
    private static final Base64.Decoder dec = Base64.getDecoder();

    public void importData (String fileName) throws IOException{
        Workbook wb = WorkbookFactory.create(new File(fileName));
        Sheet trainingLog = wb.getSheetAt(0);
        Sheet performanceList = wb.getSheetAt(1);
        DataFormatter df = new DataFormatter();
        registerAthlete(df, performanceList);
        addEvent(df, performanceList);
        addMeet(df, performanceList);
        addMeetResults(df, performanceList);
        addTrainingLog(df, trainingLog);
        wb.close();
    }

    public void registerAthlete(DataFormatter df, Sheet performanceList){
        Main.db.connect();
        for(int i = 1; i < performanceList.getLastRowNum() + 1; i++){
            String fName = df.formatCellValue(performanceList.getRow(i).getCell(0)).replaceAll("\\s", "");
            String lName = df.formatCellValue(performanceList.getRow(i).getCell(1)).replaceAll("\\s", "");
            String teamName = df.formatCellValue(performanceList.getRow(i).getCell(5));
            String yearAsString = df.formatCellValue(performanceList.getRow(i).getCell(6));
            int year = Integer.parseInt(yearAsString);
            String userName = fName + lName;
            userName = userName.toLowerCase();
            byte[] newSalt = getNewSalt();
            String hashedPass = hashPassword(newSalt, "Password123");
            try {
                CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call Register(?, ?, ?, ?, ?, ?, ?)}");
                stmt.registerOutParameter(1, Types.INTEGER);
                stmt.setString(2, userName);
                stmt.setString(3, getStringFromBytes(newSalt));
                stmt.setString(4, hashedPass);
                stmt.setString(5, teamName);
                stmt.setString(6, fName);
                stmt.setString(7, lName);
                stmt.setInt(8, year);
                stmt.execute();

            } catch (SQLException e) {
                int errorCode = e.getErrorCode();
                Alert alert = new Alert(Alert.AlertType.WARNING);
                if(errorCode==1){
                    alert.setContentText("Import: Username field cannot be empty");
                }else if(errorCode==2){
                    alert.setContentText("Import: Password Salt cannot be empty");
                }else if(errorCode==3){
                    alert.setContentText("Import: Password Hash cannot be empty");
                }else if(errorCode==4){
                    alert.setContentText("Import: This username already exists");
                }else{
                    alert.setContentText("Import: Something went wrong. Please try again");
                }
                alert.show();
                //e.printStackTrace();
            }

        }
    }

    public void addMeetResults(DataFormatter df, Sheet performanceList){
        Main.db.connect();
        for(int i = 1; i < performanceList.getLastRowNum(); i++){
            String event = df.formatCellValue(performanceList.getRow(i).getCell(2)).replaceAll("\\s", "");
            String mark = df.formatCellValue(performanceList.getRow(i).getCell(3)).replaceAll("\\s", "");
            String placeAsString = df.formatCellValue(performanceList.getRow(i).getCell(4)).replaceAll("\\s", "");
            int place = Integer.parseInt(placeAsString);
            try {
                CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call AddRacedIn(?, ?, ?, ?, ?)}");
                stmt.registerOutParameter(1, Types.INTEGER);
                stmt.setInt(2, getAthleteID(df, performanceList, i)); //how to actually get athlete ID?
                stmt.setInt(3, getMeetID(df, performanceList, i)); // how to actually get meet ID?
                stmt.setString(4, event);
                stmt.setString(5, mark);
                stmt.setInt(6, place);
                stmt.execute();
            } catch (SQLException e) {
                int errorCode = e.getErrorCode();
                Alert alert = new Alert(Alert.AlertType.WARNING);
                if(errorCode==1){
                    alert.setContentText("Import: Cannot insert null for id of Athlete");
                }else if(errorCode==2){
                    alert.setContentText("Import: Cannot insert null for id of Meet");
                }else if(errorCode==3){
                    alert.setContentText("Import: Missing name for event");
                }else if(errorCode==4){
                    alert.setContentText("Import: This athlete does not exist");
                }else if(errorCode==5){
                    alert.setContentText("Import: This meet does not exist");
                }else if(errorCode==6){
                    alert.setContentText("Import: This event does not exist");
                }else{
                    alert.setContentText("Import: Something went wrong. Please try again");
                }
                alert.show();
                //e.printStackTrace();
            }
        }
    }

    public void addTrainingLog(DataFormatter df, Sheet trainingLog){
        Main.db.connect();
        for(int i = 1; i < trainingLog.getLastRowNum(); i++){
            String distanceAsString = df.formatCellValue(trainingLog.getRow(i).getCell(3));
            String time = df.formatCellValue(trainingLog.getRow(i).getCell(2));
            String date = df.formatCellValue(trainingLog.getRow(i).getCell(4));
            float distance = Float.parseFloat(distanceAsString.substring(0, 3));
            if(time.length() == 7){
                time = "0" + time;
            }
            Date dateIn = null;
            if (date != null && date != ""){
                dateIn = Date.valueOf(date);
            }
            try {
                CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call InsertActivity(?, ?, ?, ?)}");
                stmt.registerOutParameter(1, Types.INTEGER);
                stmt.setInt(2, getIDFromLog(df, trainingLog, i));
                stmt.setFloat(3, distance);
                stmt.setTime(4, Time.valueOf(time));
                stmt.setDate(5, dateIn);
                stmt.execute();
            } catch (SQLException e) {
                int errorCode = e.getErrorCode();
                Alert alert = new Alert(Alert.AlertType.WARNING);
                if(errorCode==1){
                    alert.setContentText("Import: Cannot insert null for id of Athlete");
                }else if(errorCode==2){
                    alert.setContentText("Import: This athlete does not exist");
                }else if(errorCode==3){
                    alert.setContentText("Import: Distance cannot be empty");
                }else if(errorCode==4){
                    alert.setContentText("Import: Time cannot be empty");
                }else{
                    alert.setContentText("Import: Something went wrong. Please try again");
                }
                alert.show();
                //e.printStackTrace();
            }
        }
    }

    public int getIDFromLog(DataFormatter df, Sheet trainingLog, int loc){
        //String query = "SELECT ID FROM Athlete WHERE FirstName = ? AND LastName = ?";
        Main.db.connect();
        int id = 0;
        try {
            CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call GetIDFromLog(?, ?)}");
            String fName = df.formatCellValue(trainingLog.getRow(loc).getCell(0)).replaceAll("\\s", "");
            String lName = df.formatCellValue(trainingLog.getRow(loc).getCell(1)).replaceAll("\\s", "");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, fName);
            stmt.setString(3, lName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                id = rs.getInt("ID");
            } else {
                String teamName = "Unspecified";
                String userName = fName + lName;
                userName = userName.toLowerCase();
                byte[] newSalt = getNewSalt();
                String hashedPass = hashPassword(newSalt, "Password123");
                try {
                    CallableStatement stmt2 = Main.db.getConnection().prepareCall("{?= call Register(?, ?, ?, ?, ?, ?, ?)}");
                    stmt2.registerOutParameter(1, Types.INTEGER);
                    stmt2.setString(2, userName);
                    stmt2.setString(3, getStringFromBytes(newSalt));
                    stmt2.setString(4, hashedPass);
                    stmt2.setString(5, teamName);
                    stmt2.setString(6, fName);
                    stmt2.setString(7, lName);
                    stmt2.setString (8, null);
                    stmt2.execute();

                } catch (SQLException e) {
                    int errorCode = e.getErrorCode();
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    if(errorCode==1){
                        alert.setContentText("Import: Username field cannot be empty");
                    }else if(errorCode==2){
                        alert.setContentText("Import: Password Salt cannot be empty");
                    }else if(errorCode==3){
                        alert.setContentText("Import: Password Hash cannot be empty");
                    }else if(errorCode==4){
                        alert.setContentText("Import: This username already exists");
                    }else{
                        alert.setContentText("Import: Something went wrong. Please try again");
                    }
                    alert.show();
                    //e.printStackTrace();
                    return -1;
                }
                getIDFromLog(df, trainingLog, loc);
            }
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(errorCode==1){
                alert.setContentText("Import: First name cannot be empty");
            }else if(errorCode==2){
                alert.setContentText("Import: Last name cannot be empty");
            }else{
                alert.setContentText("Import: Something went wrong. Please try again");
            }
            alert.show();
            //e.printStackTrace();
        }
        return id;
    }

    public void addMeet(DataFormatter df, Sheet performanceList){
        Main.db.connect();
        for(int i = 1; i < performanceList.getLastRowNum(); i++) {
            String name = df.formatCellValue(performanceList.getRow(i).getCell(7));
            String host = df.formatCellValue(performanceList.getRow(i).getCell(8));
            String date = df.formatCellValue(performanceList.getRow(i).getCell(9)).replaceAll("\\s", "");
            try {
                CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call AddMeet(?, ?, ?)}");
                stmt.registerOutParameter(1, Types.INTEGER);
                stmt.setString(2, name);
                stmt.setString(3, host);
                stmt.setDate(4, Date.valueOf(date));
                stmt.execute();
            } catch (SQLException e) {
                int errorCode = e.getErrorCode();
                Alert alert = new Alert(Alert.AlertType.WARNING);
                if(errorCode==1){
                    alert.setContentText("Import: Meet name cannot be empty");
                }else if(errorCode==2){
                    alert.setContentText("Import: Meet host cannot be empty");
                }else if(errorCode==3){
                    alert.setContentText("Import: Meet Date cannot be empty");
                }else if(errorCode==4){
                    alert.setContentText("Import: This Meet already exists");
                }else{
                    alert.setContentText("Import: Something went wrong. Please try again");
                }
                alert.show();
                //e.printStackTrace();
            }
        }
    }

    public void addEvent(DataFormatter df, Sheet performanceList){
        Main.db.connect();
        for(int i = 1; i < performanceList.getLastRowNum(); i++) {
            String event = df.formatCellValue(performanceList.getRow(i).getCell(2));
            //String query = "SELECT Name FROM Event WHERE Name = ?";
            try {
                CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call GetEvent(?)}");
                stmt.registerOutParameter(1, Types.INTEGER);
                stmt.setString(2, event);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()){
                    try {
                        CallableStatement stmt2 = Main.db.getConnection().prepareCall("{?= call AddEvent(?)}");
                        stmt2.registerOutParameter(1, Types.INTEGER);
                        stmt2.setString(2, event);
                        stmt2.execute();
                    } catch (SQLException e) {
                        int errorCode = e.getErrorCode();
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        if(errorCode==1){
                            alert.setContentText("Import: Event name cannot be empty");
                        }else if(errorCode==2){
                            alert.setContentText("Import: This event already exists");
                        }else{
                            alert.setContentText("Import: Something went wrong. Please try again");
                        }
                        alert.show();
                        //e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                int errorCode = e.getErrorCode();
                Alert alert = new Alert(Alert.AlertType.WARNING);
                if(errorCode==1){
                    alert.setContentText("Import: Event name cannot be empty");
                }else{
                    alert.setContentText("Import: Something went wrong. Please try again");
                }
                alert.show();
                //e.printStackTrace();
            }
        }
    }

    public int getAthleteID(DataFormatter df, Sheet performanceList, int loc){
        //String query = "SELECT ID FROM Athlete WHERE FirstName = ? AND LastName = ? AND TeamName = ?";
        Main.db.connect();
        int id = 0;
        try {
            CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call GetAthleteID(?, ?, ?)}");
            String fName = df.formatCellValue(performanceList.getRow(loc).getCell(0));
            String lName = df.formatCellValue(performanceList.getRow(loc).getCell(1));
            String teamName = df.formatCellValue(performanceList.getRow(loc).getCell(5));
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, fName);
            stmt.setString(3, lName);
            stmt.setString(4, teamName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                id = rs.getInt("ID");
            }
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(errorCode==1){
                alert.setContentText("Import: The name of a team cannot be empty");
            }else if(errorCode==2){
                alert.setContentText("Import: First name cannot be empty");
            }else if(errorCode==3){
                alert.setContentText("Import: Last name cannot be empty");
            }else{
                alert.setContentText("Import: Something went wrong. Please try again");
            }
            alert.show();
            //e.printStackTrace();
            return -1;
        }
        return id;
    }

    private int getMeetID(DataFormatter df, Sheet performanceList, int loc) {
        //String query = "SELECT ID FROM Meet WHERE [Name] = ? AND Host = ? AND [Date] = ?";
        int id = 0;
        try {
            CallableStatement stmt = Main.db.getConnection().prepareCall("{?= call GetMeetID(?, ?, ?)}");
            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, df.formatCellValue(performanceList.getRow(loc).getCell(7)));
            stmt.setString(3, df.formatCellValue(performanceList.getRow(loc).getCell(8)));
            stmt.setString(4, df.formatCellValue(performanceList.getRow(loc).getCell(9)));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                id = rs.getInt("ID");
            }
        } catch (SQLException e) {
            int errorCode = e.getErrorCode();
            Alert alert = new Alert(Alert.AlertType.WARNING);
            if(errorCode==1){
                alert.setContentText("Import: Meet name cannot be empty");
            }else if(errorCode==2){
                alert.setContentText("Import: Meet host cannot be empty");
            }else if(errorCode==3){
                alert.setContentText("Import: Meet date cannot be empty");
            }else{
                alert.setContentText("Import: Something went wrong. Please try again");
            }
            alert.show();
            //e.printStackTrace();
            return -1;
        }
        return id;
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
