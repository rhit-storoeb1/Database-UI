package sample;

public class PBTable {

    String event, mark;

    public PBTable(String event, String mark){
        this.event = event;
        this.mark = mark;
    }

    public String getPB(){
        return this.mark;
    }

    public String getEvent(){
        return this.event;
    }

    public String toString(){
        return event + ", " + mark;
    }
}
