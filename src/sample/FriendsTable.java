package sample;

public class FriendsTable {

    String name;

    public FriendsTable(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public String toString(){
        return ": " + this.name;
    }
}
