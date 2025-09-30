package com.csi_rscoe.csi_backend.Controllers.Admin;

import com.csi_rscoe.csi_backend.Models.EventRegistration;
import com.csi_rscoe.csi_backend.Repositories.EventRegistrationRepository;
import com.csi_rscoe.csi_backend.Models.Event;
import com.csi_rscoe.csi_backend.Repositories.EventRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/events")
@CrossOrigin(origins = { "http://localhost:5173" })
public class AdminEventRegistrationController {

    @Autowired
    private EventRegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping(value = "/{eventId}/registrations", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EventRegistration> list(@PathVariable Long eventId) {
        // Prefer dynamic per-event table if exists; else fallback to base table
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isPresent()) {
            String table = EventRegistration.getTableNameForEvent(eventOpt.get().getTitle());
            try {
                // Query dynamic table rows into lightweight EventRegistration objects (subset fields)
                List<Object[]> rows = entityManager.createNativeQuery(
                    "SELECT id, event_id, name, email, phone, department, college, year, team_name, member_names, rbt_no, transaction_id, transaction_details, message, custom_fields_json, created_at, team_size, whatsapp_group_url FROM " + table + " ORDER BY created_at DESC"
                ).getResultList();
                List<EventRegistration> out = new ArrayList<>();
                for (Object[] r : rows) {
                    EventRegistration e = new EventRegistration();
                    e.setId(r[0] != null ? ((Number) r[0]).longValue() : null);
                    e.setEventId(r[1] != null ? ((Number) r[1]).longValue() : null);
                    e.setName((String) r[2]);
                    e.setEmail((String) r[3]);
                    e.setPhone((String) r[4]);
                    e.setDepartment((String) r[5]);
                    e.setCollege((String) r[6]);
                    e.setYear((String) r[7]);
                    e.setTeamName((String) r[8]);
                    // Deprecated: memberNames no longer used
                    e.setRbtNo((String) r[10]);
                    e.setTransactionId((String) r[11]);
                    e.setTransactionDetails((String) r[12]);
                    e.setMessage((String) r[13]);
                    e.setCustomFieldsJson((String) r[14]);
                    e.setCreatedAt((java.util.Date) r[15]);
                    e.setTeamSize(r[16] != null ? ((Number) r[16]).intValue() : null);
                    e.setWhatsappGroupUrl((String) r[17]);
                    out.add(e);
                }
                return out;
            } catch (Exception ignore) {}
        }
        return registrationRepository.findByEventId(eventId);
    }

    @GetMapping(value = "/{eventId}/registrations/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> count(@PathVariable Long eventId) {
        long count = registrationRepository.findByEventId(eventId).size();
        return ResponseEntity.ok(count);
    }

    @GetMapping(value = "/{eventId}/registrations/{registrationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventRegistration> getOne(@PathVariable Long eventId, @PathVariable Long registrationId) {
        Optional<EventRegistration> opt = registrationRepository.findById(registrationId);
        if (opt.isPresent() && opt.get().getEventId().equals(eventId)) {
            return ResponseEntity.ok(opt.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping(value = "/{eventId}/registrations/{registrationId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventRegistration> update(@PathVariable Long eventId, @PathVariable Long registrationId,
            @RequestBody EventRegistration updated) {
        Optional<EventRegistration> opt = registrationRepository.findById(registrationId);
        if (opt.isEmpty() || !opt.get().getEventId().equals(eventId)) {
            return ResponseEntity.notFound().build();
        }
        EventRegistration r = opt.get();
        r.setName(updated.getName());
        r.setEmail(updated.getEmail());
        r.setPhone(updated.getPhone());
        r.setDepartment(updated.getDepartment());
        r.setCollege(updated.getCollege());
        r.setYear(updated.getYear());
        r.setTeamName(updated.getTeamName());
        // Deprecated: ignore memberNames updates
        r.setRbtNo(updated.getRbtNo());
        r.setTransactionId(updated.getTransactionId());
        r.setTransactionDetails(updated.getTransactionDetails());
        r.setMessage(updated.getMessage());
        r.setReceiptUrl(updated.getReceiptUrl());
        r.setQrCodeUrl(updated.getQrCodeUrl());
        r.setReceiptImage(updated.getReceiptImage());
        r.setQrCodeImage(updated.getQrCodeImage());
        r.setCustomFieldsJson(updated.getCustomFieldsJson());
        EventRegistration saved = registrationRepository.save(r);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping(value = "/{eventId}/registrations/{registrationId}")
    public ResponseEntity<Void> delete(@PathVariable Long eventId, @PathVariable Long registrationId) {
        Optional<EventRegistration> opt = registrationRepository.findById(registrationId);
        if (opt.isEmpty() || !opt.get().getEventId().equals(eventId)) {
            return ResponseEntity.notFound().build();
        }
        registrationRepository.deleteById(registrationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{eventId}/registrations.csv", produces = "text/csv")
    public ResponseEntity<String> exportCsv(@PathVariable Long eventId) {
        List<EventRegistration> regs = registrationRepository.findByEventId(eventId);
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        List<String> headers = new ArrayList<>();
        headers.add("S.No.");
        headers.add("Name");
        headers.add("Email");
        headers.add("Phone");
        headers.add("Department");
        headers.add("College");
        headers.add("Year");
        headers.add("Team Name");
        headers.add("Team Size");
        // Removed: Member Names (deprecated)
        headers.add("WhatsApp Group");
        headers.add("RBT No");
        headers.add("Transaction ID");
        headers.add("Transaction Details");
        headers.add("Message");
        // derive dynamic headers from event.registrationFieldsJson if present
        if (eventOpt.isPresent() && eventOpt.get().getRegistrationFieldsJson() != null) {
            try {
                JsonNode arr = objectMapper.readTree(eventOpt.get().getRegistrationFieldsJson());
                if (arr.isArray()) {
                    for (JsonNode field : arr) {
                        JsonNode labelNode = field.get("label");
                        if (labelNode != null && labelNode.isTextual()) {
                            headers.add(labelNode.asText());
                        }
                    }
                }
            } catch (Exception ignored) {
            }
        }
        headers.add("Payment Status");

        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", headers)).append('\n');
        int i = 1;
        for (EventRegistration r : regs) {
            List<String> row = new ArrayList<>();
            row.add(Integer.toString(i++));

            // Use customFieldsJson if available, else map from fixed fields
            JsonNode custom = null;
            try {
                if (r.getCustomFieldsJson() != null) {
                    custom = objectMapper.readTree(r.getCustomFieldsJson());
                }
            } catch (Exception ignored) {
            }

            if (eventOpt.isPresent() && eventOpt.get().getRegistrationFieldsJson() != null && custom != null
                    && custom.isObject()) {
                // First append fixed fields from entity (without deprecated Member Names)
                row.add(escape(r.getName()));
                row.add(escape(r.getEmail()));
                row.add(escape(r.getPhone()));
                row.add(escape(r.getDepartment()));
                row.add(escape(r.getCollege()));
                row.add(escape(r.getYear()));
                row.add(escape(r.getTeamName()));
                row.add(escape(r.getTeamSize() == null ? "" : r.getTeamSize().toString()));
                row.add(escape(r.getWhatsappGroupUrl()));
                row.add(escape(r.getRbtNo()));
                row.add(escape(r.getTransactionId()));
                row.add(escape(r.getTransactionDetails()));
                row.add(escape(r.getMessage()));
                // Then append dynamic custom fields
                int fixedCount = 1 /*S.No.*/ + 13; // 13 fixed fields added above
                for (int h = fixedCount; h < headers.size() - 1; h++) { // after fixed fields, before Payment Status
                    String label = headers.get(h);
                    String value = custom.has(label) && !custom.get(label).isNull() ? custom.get(label).asText() : "";
                    if ("Event Fee".equalsIgnoreCase(label)) {
                        value = value.isEmpty() ? "Rs. 0/-"
                                : (value.startsWith("Rs.") ? value : ("Rs. " + value + "/-"));
                    }
                    row.add(escape(value));
                }
            } else {
                // Only fixed fields available
                row.add(escape(r.getName()));
                row.add(escape(r.getEmail()));
                row.add(escape(r.getPhone()));
                row.add(escape(r.getDepartment()));
                row.add(escape(r.getCollege()));
                row.add(escape(r.getYear()));
                row.add(escape(r.getTeamName()));
                row.add(escape(r.getTeamSize() == null ? "" : r.getTeamSize().toString()));
                row.add(escape(r.getWhatsappGroupUrl()));
                row.add(escape(r.getRbtNo()));
                row.add(escape(r.getTransactionId()));
                row.add(escape(r.getTransactionDetails()));
                row.add(escape(r.getMessage()));
            }

            // Payment Status best-effort from custom fields or transaction details
            String paymentStatus = "";
            if (custom != null && custom.has("Payment Status")) {
                paymentStatus = custom.get("Payment Status").asText("");
            } else if (r.getTransactionDetails() != null) {
                paymentStatus = r.getTransactionDetails();
            }
            row.add(escape(paymentStatus));

            sb.append(String.join(",", row)).append('\n');
        }
        // Use event name for filename
        String eventName = eventOpt.isPresent() ? eventOpt.get().getTitle() : "event_" + eventId;
        String filename = EventRegistration.getTableNameForEvent(eventName) + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(sb.toString());
    }

    private String escape(String v) {
        if (v == null)
            return "";
        boolean needsQuote = v.contains(",") || v.contains("\n") || v.contains("\"");
        v = v.replace("\"", "\"\"");
        return needsQuote ? ("\"" + v + "\"") : v;
    }
}
