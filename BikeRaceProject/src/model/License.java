package model;

import java.time.LocalDate;

// License, from the DP diagram: -licenseID, -expirationDate, -category; +isValid(), +getCategory(), +upgradeCategory().
public class License {

    private final String licenseId;
    private LocalDate expirationDate;
    private Category category;

    public License(String licenseId, LocalDate expirationDate, Category category) {
        this.licenseId = licenseId;
        this.expirationDate = expirationDate;
        this.category = category;
    }

    public boolean isValid() {
        return expirationDate != null && !LocalDate.now().isAfter(expirationDate);
    }

    public Category getCategory() {
        return category;
    }

    // Design constraint: 5 podium finishes moves a racer up one category.
    // (the upgrade message now comes from the Observer notification, not a print here)
    public void upgradeCategory() {
        category = category.upgrade();
    }

    public String getLicenseId() {
        return licenseId;
    }
    public LocalDate getExpirationDate() {
        return expirationDate;
    }
}
