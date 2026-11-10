package model;

public abstract class User { // using abstract

    protected int userId; // added this wasnt in diagram since user needs Id
    protected String name;
    private boolean loggedIn; // added a boolean logged in just so we can tell if its actually loggen in or not

    public User(int userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    // no printing here anymore
    // the view file shows the welcome and goodbye messages
    public void login() {
        loggedIn = true;
    }

    public void logout() {
        loggedIn = false;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
