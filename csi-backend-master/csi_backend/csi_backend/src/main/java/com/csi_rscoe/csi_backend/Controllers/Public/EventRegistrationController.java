package com.csi_rscoe.csi_backend.Controllers.Public;

import com.csi_rscoe.csi_backend.Models.EventRegistration;
import com.csi_rscoe.csi_backend.Repositories.EventRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.csi_rscoe.csi_backend.Models.Event;
import com.csi_rscoe.csi_backend.Repositories.EventRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/public/events")
@CrossOrigin(origins = { "http://localhost:5173", "https://csirscoe.netlify.app", "https://csi-rscoe-nexus-main.netlify.app", "https://csi-rscoe.vercel.app" })
public class EventRegistrationController {

    @Autowired
    private EventRegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    @PostMapping(value = "/{eventId}/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(
            @PathVariable Long eventId,
            @RequestPart("payload") EventRegistration registration,
            @RequestPart(value = "receiptImage", required = false) MultipartFile receiptImage,
            @RequestPart(value = "qrCodeImage", required = false) MultipartFile qrCodeImage) throws IOException {
        registration.setEventId(eventId);

        // Basic validations
        if (registration.getEmail() != null && !registration.getEmail().isBlank()) {
            String email = registration.getEmail().trim();
            if (!Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$").matcher(email).matches()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        if (registration.getPhone() != null && !registration.getPhone().isBlank()) {
            String phone = registration.getPhone().replaceAll("\\s+", "");
            if (!Pattern.compile("^[0-9]{10}$").matcher(phone).matches()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        }
        // Duplicate checks by unique team name within same event
        if (registration.getTeamName() != null && !registration.getTeamName().isBlank()) {
            List<EventRegistration> existingForEvent = registrationRepository.findByEventId(eventId);
            String requestedTeam = registration.getTeamName().trim().toLowerCase();
            boolean dupTeam = existingForEvent.stream().anyMatch(r -> r.getTeamName() != null && r.getTeamName().trim().equalsIgnoreCase(requestedTeam));
            if (dupTeam) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            }
        }
        // Duplicate by email/phone/txn id within same event
        List<EventRegistration> existingForEvent = registrationRepository.findByEventId(eventId);
        if (registration.getEmail() != null) {
            String e = registration.getEmail().trim().toLowerCase();
            boolean exists = existingForEvent.stream().anyMatch(r -> r.getEmail() != null && r.getEmail().trim().equalsIgnoreCase(e));
            if (exists) return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        if (registration.getPhone() != null) {
            String p = registration.getPhone().replaceAll("\\s+", "");
            boolean exists = existingForEvent.stream().anyMatch(r -> r.getPhone() != null && r.getPhone().replaceAll("\\s+", "").equals(p));
            if (exists) return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        if (registration.getTransactionId() != null && !registration.getTransactionId().isBlank()) {
            String t = registration.getTransactionId().trim();
            boolean exists = existingForEvent.stream().anyMatch(r -> r.getTransactionId() != null && r.getTransactionId().trim().equalsIgnoreCase(t));
            if (exists) return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        // Explicitly clear binary fields to avoid bytea/oid issues
        registration.setReceiptImage(null);
        registration.setQrCodeImage(null);
        // Normalize optional fields from custom payload
        if (registration.getTeamSize() == null && registration.getMemberNames() != null) {
            String[] parts = registration.getMemberNames().split(",");
            int count = 0;
            for (String p : parts) { if (p != null && !p.trim().isEmpty()) count++; }
            registration.setTeamSize(count == 0 ? null : count);
        }
        // Best-effort: also write to a per-event table; ignore any SQL errors
        try {
            String tableName = "event_registrations";
            Event ev = eventRepository.findById(eventId).orElse(null);
            if (ev != null) {
                tableName = EventRegistration.getTableNameForEvent(ev.getTitle());
            }
            // Ensure table exists (PostgreSQL)
            entityManager.createNativeQuery("CREATE TABLE IF NOT EXISTS " + tableName + " (LIKE event_registrations INCLUDING ALL)").executeUpdate();
            // Insert using native query to target dynamic table
            entityManager.createNativeQuery(
                "INSERT INTO " + tableName + " (event_id, name, team_name, member_names, email, phone, department, college, year, rbt_no, transaction_id, transaction_details, receipt_url, message, custom_fields_json, created_at, team_size, whatsapp_group_url) " +
                "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12, ?13, ?14, ?15, now(), ?16, ?17)")
                .setParameter(1, registration.getEventId())
                .setParameter(2, registration.getName())
                .setParameter(3, registration.getTeamName())
                .setParameter(4, registration.getMemberNames())
                .setParameter(5, registration.getEmail())
                .setParameter(6, registration.getPhone())
                .setParameter(7, registration.getDepartment())
                .setParameter(8, registration.getCollege())
                .setParameter(9, registration.getYear())
                .setParameter(10, registration.getRbtNo())
                .setParameter(11, registration.getTransactionId())
                .setParameter(12, registration.getTransactionDetails())
                .setParameter(13, registration.getReceiptUrl())
                .setParameter(14, registration.getMessage())
                .setParameter(15, registration.getCustomFieldsJson())
                .setParameter(16, registration.getTeamSize())
                .setParameter(17, registration.getWhatsappGroupUrl())
                .executeUpdate();
        } catch (Exception ignore) {
            // ignore per-event table issues; base save below still ensures success
        }

        // Save to base repository as well for admin list APIs
        EventRegistration saved = registrationRepository.save(registration);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping(value = "/{eventId}/team-available", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> isTeamNameAvailable(@PathVariable Long eventId, @RequestParam("teamName") String teamName) {
        if (teamName == null || teamName.trim().isEmpty()) {
            return ResponseEntity.ok(true);
        }
        String requested = teamName.trim().toLowerCase();
        boolean exists = registrationRepository.findByEventId(eventId).stream()
                .anyMatch(r -> r.getTeamName() != null && r.getTeamName().trim().equalsIgnoreCase(requested));
        return ResponseEntity.ok(!exists);
    }
}
