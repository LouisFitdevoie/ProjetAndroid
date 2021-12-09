package be.heh.fitdevoie.projetandroidstudio.Database;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public User() { }

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID : "
                + Integer.toString(getId()) + "\n"
                + "FirstName : " + getFirstName() + "\n"
                + "LastName : " + getLastName() + "\n"
                + "Password : " + getPassword() + "\n"
                + "Email : " + getEmail());
        return sb.toString();
    }
}
