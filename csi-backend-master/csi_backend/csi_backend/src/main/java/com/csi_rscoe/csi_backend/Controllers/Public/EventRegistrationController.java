package com.csi_rscoe.csi_backend.Controllers.Public;

import com.csi_rscoe.csi_backend.Models.EventRegistration;
import com.csi_rscoe.csi_backend.Repositories.EventRegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.csi_rscoe.csi_backend.Models.Event;
import com.csi_rscoe.csi_backend.Repositories.EventRepository;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

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
        // Validate structured team members via customFieldsJson (optional)
        try {
            if (registration.getCustomFieldsJson() != null && !registration.getCustomFieldsJson().isBlank()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(registration.getCustomFieldsJson());
                if (root != null && root.isObject()) {
                    int requiredTeamSize = 0;
                    if (root.has("Required Team Size")) {
                        requiredTeamSize = parseIntSafe(root.get("Required Team Size"));
                    } else if (root.has("Team Size")) {
                        requiredTeamSize = parseIntSafe(root.get("Team Size"));
                    }
                    boolean leaderRequired = false;
                    if (root.has("Leader Required")) {
                        String v = root.get("Leader Required").asText("");
                        leaderRequired = "true".equalsIgnoreCase(v) || "yes".equalsIgnoreCase(v);
                    }
                    int present = 0;
                    for (int i = 1; i <= Math.max(requiredTeamSize, 10); i++) {
                        String nameKey = "Member " + i + " Name";
                        String emailKey = "Member " + i + " Email";
                        String phoneKey = "Member " + i + " Phone";
                        boolean any = (root.has(nameKey) && !root.get(nameKey).asText("").isBlank())
                                || (root.has(emailKey) && !root.get(emailKey).asText("").isBlank())
                                || (root.has(phoneKey) && !root.get(phoneKey).asText("").isBlank());
                        if (any) present++;
                    }
                    if (requiredTeamSize > 0 && present < requiredTeamSize) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Insufficient team members: required " + requiredTeamSize);
                    }
                    if (leaderRequired) {
                        Integer leaderIndex = null;
                        if (root.has("Team Leader")) {
                            int idx = parseIntSafe(root.get("Team Leader"));
                            leaderIndex = idx > 0 ? idx : null;
                        } else {
                            for (int i = 1; i <= 10; i++) {
                                String leaderKey = "Member " + i + " Is Leader";
                                if (root.has(leaderKey) && root.get(leaderKey).asText("").trim().equalsIgnoreCase("true")) {
                                    leaderIndex = i;
                                    break;
                                }
                            }
                        }
                        if (leaderIndex == null) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("Team leader is required");
                        }
                    }
                    if (registration.getTeamSize() == null && requiredTeamSize > 0) {
                        registration.setTeamSize(requiredTeamSize);
                    }
                    // Stop using deprecated memberNames
                    registration.setMemberNames(null);
                }
            }
        } catch (Exception ignore) {}
        // Explicitly clear binary fields to avoid bytea/oid issues
        registration.setReceiptImage(null);
        registration.setQrCodeImage(null);
        // Deprecated normalization from comma separated members removed
        // Best-effort: also write to a per-event table; ignore any SQL errors
        try {
            String tableName = "event_registrations";
            Event ev = eventRepository.findById(eventId).orElse(null);
            if (ev != null) {
                tableName = EventRegistration.getTableNameForEvent(ev.getTitle());
            }
            // Ensure table exists in public schema (PostgreSQL)
            String fqtn = "public." + tableName;
            entityManager.createNativeQuery("CREATE TABLE IF NOT EXISTS " + fqtn + " (LIKE event_registrations INCLUDING ALL)").executeUpdate();
            // Insert using native query to target dynamic table
            entityManager.createNativeQuery(
                "INSERT INTO " + fqtn + " (event_id, name, team_name, email, phone, department, college, year, rbt_no, transaction_id, transaction_details, receipt_url, message, custom_fields_json, created_at, team_size, whatsapp_group_url) " +
                "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10, ?11, ?12, ?13, ?14, now(), ?15, ?16)")
                .setParameter(1, registration.getEventId())
                .setParameter(2, registration.getName())
                .setParameter(3, registration.getTeamName())
                .setParameter(4, registration.getEmail())
                .setParameter(5, registration.getPhone())
                .setParameter(6, registration.getDepartment())
                .setParameter(7, registration.getCollege())
                .setParameter(8, registration.getYear())
                .setParameter(9, registration.getRbtNo())
                .setParameter(10, registration.getTransactionId())
                .setParameter(11, registration.getTransactionDetails())
                .setParameter(12, registration.getReceiptUrl())
                .setParameter(13, registration.getMessage())
                .setParameter(14, registration.getCustomFieldsJson())
                .setParameter(15, registration.getTeamSize())
                .setParameter(16, registration.getWhatsappGroupUrl())
                .executeUpdate();
        } catch (Exception ignore) {
            // ignore per-event table issues; base save below still ensures success
        }

        // Save to base repository only (single table as earlier)
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

    private static int parseIntSafe(JsonNode node) {
        try {
            if (node == null || node.isNull()) return 0;
            if (node.isInt()) return node.asInt(0);
            String s = node.asText("").trim();
            if (s.isEmpty()) return 0;
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }
}
