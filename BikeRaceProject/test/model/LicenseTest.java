package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

public class LicenseTest {

    @Test
    public void licenseExpiringInTheFutureIsValid() {
        License l = new License("L1", LocalDate.now().plusDays(30), Category.CAT_5);
        assertTrue(l.isValid());
    }

    @Test
    public void licenseExpiringTodayIsStillValid() {
        License l = new License("L1", LocalDate.now(), Category.CAT_5);
        assertTrue(l.isValid());
    }

    @Test
    public void licenseThatExpiredYesterdayIsInvalid() {
        License l = new License("L1", LocalDate.now().minusDays(1), Category.CAT_5);
        assertFalse(l.isValid());
    }

    @Test
    public void licenseWithNoExpirationDateIsInvalid() {
        License l = new License("L1", null, Category.CAT_5);
        assertFalse(l.isValid());
    }

    @Test
    public void upgradeCategoryMovesUpOneLevel() {
        License l = new License("L1", LocalDate.now().plusDays(30), Category.CAT_5);
        l.upgradeCategory();
        assertEquals(Category.CAT_4, l.getCategory());
    }

    @Test
    public void gettersReturnConstructorValues() {
        LocalDate exp = LocalDate.now().plusYears(1);
        License l = new License("LIC-9", exp, Category.CAT_3);
        assertEquals("LIC-9", l.getLicenseId());
        assertEquals(exp, l.getExpirationDate());
        assertEquals(Category.CAT_3, l.getCategory());
    }
}
