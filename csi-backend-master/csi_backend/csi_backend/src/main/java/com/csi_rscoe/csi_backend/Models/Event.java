package com.csi_rscoe.csi_backend.Models;

import jakarta.persistence.*;

@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String name; // align with frontend naming
    @Column(length = 2000)
    private String description;
    private String date; // ISO date string for simplicity
    private String location;
    private Integer attendees;
    private Integer fee;
    private String status; // upcoming | ongoing | completed
    private String image;
    private String flyoverDescription;
    @Transient
    private String details; // non-persistent
    private String rulebookUrl;
    @Transient
    private String qrCodeUrl; // non-persistent
    @Transient
    private String whatsappGroupUrl; // non-persistent

    @Column(length = 4000)
    private String registrationSchemaJson; // JSON array describing custom fields
    private String registrationFieldsJson; // alternative naming used by frontend
    private String registrationDeadline; // ISO date string

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getAttendees() {
        return attendees;
    }

    public void setAttendees(Integer attendees) {
        this.attendees = attendees;
    }

    public Integer getFee() {
        return fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFlyoverDescription() {
        return flyoverDescription;
    }

    public void setFlyoverDescription(String flyoverDescription) {
        this.flyoverDescription = flyoverDescription;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getRulebookUrl() {
        return rulebookUrl;
    }

    public void setRulebookUrl(String rulebookUrl) {
        this.rulebookUrl = rulebookUrl;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getWhatsappGroupUrl() {
        return whatsappGroupUrl;
    }

    public void setWhatsappGroupUrl(String whatsappGroupUrl) {
        this.whatsappGroupUrl = whatsappGroupUrl;
    }

    public String getRegistrationSchemaJson() {
        return registrationSchemaJson;
    }

    public void setRegistrationSchemaJson(String registrationSchemaJson) {
        this.registrationSchemaJson = registrationSchemaJson;
    }

    public String getRegistrationFieldsJson() {
        return registrationFieldsJson;
    }

    public void setRegistrationFieldsJson(String registrationFieldsJson) {
        this.registrationFieldsJson = registrationFieldsJson;
    }

    public String getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(String registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }
}
