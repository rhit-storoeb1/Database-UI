package sample;

public class FriendsTable {

    String id;
    String name;

    public FriendsTable(String id, String name){
        this.id=id;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
    public String getID(){
        return this.id;
    }

    public String toString(){
        return ": " + this.name;
    }
}
