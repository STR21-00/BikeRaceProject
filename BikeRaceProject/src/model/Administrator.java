package model;

// Administrator, from the DP diagram: +manageUser(), +manageLicense().
public class Administrator extends User {

    public Administrator(int userId, String name) {
        super(userId, name);
    }

    // these used to print, now they return the text so the view can show it
    public String manageUser(User user) {
        return name + " (admin) is managing user " + user.getName();
    }

    public String manageLicense(License license) {
        return name + " (admin) is managing license " + license.getLicenseId();
    }
}
