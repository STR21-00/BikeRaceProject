package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

public class UserTest {

    @Test
    public void newUserIsNotLoggedIn() {
        assertFalse(new Racer(1, "Sam").isLoggedIn());
    }

    @Test
    public void loginAndLogoutToggleState() {
        User u = new Organizer(1, "Chris");
        u.login();
        assertTrue(u.isLoggedIn());
        u.logout();
        assertFalse(u.isLoggedIn());
    }

    @Test
    public void idAndNameAreStored() {
        User u = new Administrator(7, "Morgan");
        assertEquals(7, u.getUserId());
        assertEquals("Morgan", u.getName());
    }

    @Test
    public void administratorManageUserReturnsText() {
        Administrator admin = new Administrator(1, "Morgan");
        User target = new Racer(2, "Sam");
        assertEquals("Morgan (admin) is managing user Sam", admin.manageUser(target));
    }

    @Test
    public void administratorManageLicenseReturnsText() {
        Administrator admin = new Administrator(1, "Morgan");
        License l = new License("LIC-2", LocalDate.now().plusDays(5), Category.CAT_5);
        assertEquals("Morgan (admin) is managing license LIC-2", admin.manageLicense(l));
    }
}
