package sample;

public class PerformanceTable {

    String EventName, Mark, Place;

    public PerformanceTable(String EventName, String Mark, String Place){
        this.EventName = EventName;
        this.Mark = Mark;
        this.Place = Place;
    }

    public String getEventName(){
        return this.EventName;
    }
    public String getMark(){
        return this.Mark;
    }
    public String getPlace(){
        return this.Place;
    }

    public String toString(){
        return EventName + ", " + Mark + ", " + Place;
    }
}
