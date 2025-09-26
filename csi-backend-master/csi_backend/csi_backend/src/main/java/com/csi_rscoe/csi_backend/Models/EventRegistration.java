package com.csi_rscoe.csi_backend.Models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "event_registrations") // Default table, will be overridden dynamically
public class EventRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventId;

    private String name;
    private String teamName;
    @Column(length = 2000)
    private String memberNames; // comma-separated list
    private String email;
    private String phone;
    private String department;
    private String college;
    private String year;
    private String rbtNo;
    private String transactionId;
    private String transactionDetails;
    private String receiptUrl; // screenshot of payment/receipt
    private String qrCodeUrl; // optional QR used
    private String message;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] receiptImage;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] qrCodeImage;

    @Column(length = 8000)
    private String customFieldsJson; // JSON of dynamic fields captured from schema

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getReceiptUrl() {
        return receiptUrl;
    }

    public void setReceiptUrl(String receiptUrl) {
        this.receiptUrl = receiptUrl;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public byte[] getReceiptImage() {
        return receiptImage;
    }

    public void setReceiptImage(byte[] receiptImage) {
        this.receiptImage = receiptImage;
    }

    public byte[] getQrCodeImage() {
        return qrCodeImage;
    }

    public void setQrCodeImage(byte[] qrCodeImage) {
        this.qrCodeImage = qrCodeImage;
    }

    public String getCustomFieldsJson() {
        return customFieldsJson;
    }

    public void setCustomFieldsJson(String customFieldsJson) {
        this.customFieldsJson = customFieldsJson;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    // Added missing getters and setters used by AdminEventRegistrationController
    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getMemberNames() {
        return memberNames;
    }

    public void setMemberNames(String memberNames) {
        this.memberNames = memberNames;
    }

    public String getRbtNo() {
        return rbtNo;
    }

    public void setRbtNo(String rbtNo) {
        this.rbtNo = rbtNo;
    }

    public String getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(String transactionDetails) {
        this.transactionDetails = transactionDetails;
    }

    // Helper method to get table name for specific event
    public static String getTableNameForEvent(String eventName) {
        if (eventName == null || eventName.trim().isEmpty()) {
            return "event_registrations";
        }
        // Sanitize event name for table name: remove special chars, spaces, convert to
        // lowercase
        String sanitized = eventName.toLowerCase()
                .replaceAll("[^a-z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
        return "registrations_" + sanitized;
    }
}
