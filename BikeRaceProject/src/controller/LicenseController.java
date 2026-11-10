package controller;

import java.time.LocalDate;

import model.Category;
import model.License;
import model.Racer;

// LicenseController, from the MVC diagram: +purchaseLicense()
public class LicenseController {

    // Our design constraint: a license is valid for 1 year and we need payment info to buy one.
    public License purchaseLicense(Racer racer, Category category, String creditCardInfo) {
        racer.setCreditCardInfo(creditCardInfo);
        String licenseId = "LIC-" + racer.getUserId();
        LocalDate expiration = LocalDate.now().plusYears(1);
        return racer.purchaseLicense(licenseId, category, expiration);
    }
}
