package sample;

public class ActivityFeedTable {

    private String AthleteID, FirstName, LastName, Distance, Time, Pace, Date;

    public ActivityFeedTable(String AthleteID, String FirstName, String LastName, String Distance, String Time, String Pace, String Date){
        this.AthleteID = AthleteID;
        this.FirstName = FirstName;
        this.LastName = LastName;
        this.Distance = Distance;
        this.Time = Time;
        this.Pace = Pace;
        this.Date = Date;
    }

    public String getAthleteID(){
        return this.AthleteID;
    }

    public String getFirstName(){
        return this.FirstName;
    }

    public String getLastName(){
        return this.LastName;
    }

    public String getDistance(){
        return this.Distance;
    }

    public String getTime(){
        return this.Time;
    }

    public String getPace(){
        return this.Pace;
    }

    public String getDate(){
        return this.Date;
    }
}
