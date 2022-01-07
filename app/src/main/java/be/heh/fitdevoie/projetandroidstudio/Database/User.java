package be.heh.fitdevoie.projetandroidstudio.Database;

public class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String password;
    private int rights;

    public User() {}

    public User(String firstName, String lastName, String emailAddress, String password, int rights) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.password = password;
        this.rights = rights;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int id) {
        this.userId = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return this.emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRights() {
        return this.rights;
    }

    public void setRights(int rights) {
        this.rights = rights;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID : " + getUserId() + "\nEmail : " + getEmailAddress().replace("'","") + "\nPrénom : " + getFirstName() + "\nNom : " +  getLastName() + "\nDroits : " + getRights());
        return sb.toString();
    }

}
