package aziz.ca.ylachat;

public class Profile {

    private String Gender;
    private String Name;
    private String Email;


    public Profile() {
    }


    public Profile(String gender, String name, String email){
        this.Gender = gender;
        this.Name = name;
        this.Email = email;
    }


    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        this.Gender = gender;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}
