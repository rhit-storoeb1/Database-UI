package sample;

public class CommentTable {

    String name, comment;

    public CommentTable(String name, String comment){
        this.name=name;
        this.comment=comment;
    }

    public String getName(){
        return this.name;
    }

    public String getComment(){
        return this.comment;
    }

    public String toString(){
        return name + ", " + comment;
    }
}
