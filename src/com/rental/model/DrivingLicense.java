package com.rental.model;

import java.time.LocalDate;

public class DrivingLicense {
    private String licenseNumber;
    private String issuingAuthority;
    private LocalDate issueDate;
    private LocalDate expiryDate;

    public DrivingLicense() {}

    public DrivingLicense(String licenseNumber, String issuingAuthority, LocalDate issueDate, LocalDate expiryDate) {
        this.licenseNumber = licenseNumber; this.issuingAuthority = issuingAuthority;
        this.issueDate = issueDate; this.expiryDate = expiryDate;
    }

    public boolean isExpired(LocalDate onDate) {
        if (expiryDate == null || onDate == null) return true;
        return onDate.isAfter(expiryDate);
    }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public String getIssuingAuthority() { return issuingAuthority; }
    public void setIssuingAuthority(String issuingAuthority) { this.issuingAuthority = issuingAuthority; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    @Override
    public String toString() { return licenseNumber + " (expires " + expiryDate + ")"; }
}
