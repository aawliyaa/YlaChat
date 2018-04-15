package aziz.ca.ylachat;

public class AddedFriends {


    private int numberOfFriends;


    public AddedFriends() {
    }

    public AddedFriends(int numberOfFriends) {
        this.numberOfFriends = numberOfFriends;
    }

    public int getNumberOfFriends() {
        return numberOfFriends;
    }

    public void setNumberOfFriends(int numberOfFriends) {
        this.numberOfFriends = numberOfFriends;
    }

    public void increaseCounter(){
        numberOfFriends++;
    }
}
