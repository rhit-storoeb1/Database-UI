package sample;

public class ActivityTable {

    String ID, athleteID, distance, time, pace, date;

    public ActivityTable(String ID, String athleteID, String distance, String time, String pace, String date){
        this.ID=ID;
        this.athleteID=athleteID;
        this.distance=distance;
        this.time=time;
        this.pace=pace;
        this.date=date;
    }

    public String getID(){
        return this.ID;
    }

    public String getAthleteID(){
        return this.athleteID;
    }

    public String getDistance(){
        return this.distance;
    }

    public String getTime(){
        return this.time;
    }

    public String getPace(){
        return this.pace;
    }

    public String getDate(){
        return this.date;
    }

    public String toString(){
        return ID + ", " + athleteID + ", " + distance + ", " + time + ", " + pace + ", " + date;
    }
}
