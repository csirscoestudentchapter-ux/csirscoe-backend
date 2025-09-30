package com.csi_rscoe.csi_backend.Controllers.Admin;

import com.csi_rscoe.csi_backend.Models.Event;
import com.csi_rscoe.csi_backend.Repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/events")
@CrossOrigin(origins = { "http://localhost:5173", "https://csirscoe.netlify.app", "https://csi-rscoe-nexus-main.netlify.app", "https://csi-rscoe.vercel.app" })
public class AdminEventController {

    @Autowired
    private EventRepository eventRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    @GetMapping(value = "/upcoming", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> getUpcoming() {
        return eventRepository.findByStatus("upcoming");
    }

    @GetMapping(value = "/completed", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> getCompleted() {
        return eventRepository.findByStatus("completed");
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Event> create(@RequestBody Event event) {
        Event saved = eventRepository.save(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Event update(@PathVariable Long id, @RequestBody Event updated) {
        Optional<Event> existing = eventRepository.findById(id);
        if (existing.isPresent()) {
            Event e = existing.get();
            e.setTitle(updated.getTitle());
            e.setName(updated.getName());
            e.setDescription(updated.getDescription());
            e.setDate(updated.getDate());
            e.setLocation(updated.getLocation());
            e.setAttendees(updated.getAttendees());
            e.setFee(updated.getFee());
            e.setStatus(updated.getStatus());
            e.setImage(updated.getImage());
            e.setFlyoverDescription(updated.getFlyoverDescription());
            e.setDetails(updated.getDetails());
            e.setRulebookUrl(updated.getRulebookUrl());
            e.setQrCodeUrl(updated.getQrCodeUrl());
            e.setWhatsappGroupUrl(updated.getWhatsappGroupUrl());
            e.setRegistrationSchemaJson(updated.getRegistrationSchemaJson());
            e.setRegistrationFieldsJson(updated.getRegistrationFieldsJson());
            e.setRegistrationDeadline(updated.getRegistrationDeadline());
            return eventRepository.save(e);
        }
        return eventRepository.save(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}/registration-schema", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRegistrationSchema(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(e -> {
                    String raw = e.getRegistrationFieldsJson() != null ? e.getRegistrationFieldsJson()
                            : e.getRegistrationSchemaJson();
                    try {
                        ArrayNode arr = ensureArray(raw);
                        ensureTeamFields(arr);
                        return ResponseEntity.ok().body(objectMapper.writeValueAsString(arr));
                    } catch (Exception ex) {
                        return ResponseEntity.ok().body(raw);
                    }
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{id}/registration-schema", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateRegistrationSchema(@PathVariable Long id, @RequestBody String schemaJson) {
        Optional<Event> existing = eventRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event e = existing.get();
        try {
            ArrayNode arr = ensureArray(schemaJson);
            ensureTeamFields(arr);
            e.setRegistrationFieldsJson(objectMapper.writeValueAsString(arr));
        } catch (Exception ex) {
            e.setRegistrationFieldsJson(schemaJson);
        }
        eventRepository.save(e);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    private ArrayNode ensureArray(String json) {
        ArrayNode arr = objectMapper.createArrayNode();
        try {
            if (json != null && !json.isBlank()) {
                if (objectMapper.readTree(json).isArray()) {
                    arr = (ArrayNode) objectMapper.readTree(json);
                }
            }
        } catch (Exception ignored) {}
        return arr;
    }

    private void ensureTeamFields(ArrayNode arr) {
        boolean hasTeamSize = false;
        boolean hasLeaderReq = false;
        for (int i = 0; i < arr.size(); i++) {
            if (!arr.get(i).isObject()) continue;
            ObjectNode o = (ObjectNode) arr.get(i);
            String label = o.has("label") ? o.get("label").asText("") : "";
            if ("Required Team Size".equalsIgnoreCase(label) || "Team Size".equalsIgnoreCase(label)) {
                hasTeamSize = true;
            }
            if ("Leader Required".equalsIgnoreCase(label)) {
                hasLeaderReq = true;
            }
        }
        if (!hasTeamSize) {
            ObjectNode teamSize = objectMapper.createObjectNode();
            teamSize.put("label", "Required Team Size");
            teamSize.put("type", "select");
            teamSize.put("required", false);
            teamSize.put("options", "1,2,3,4,5,6,7,8,9,10");
            arr.insert(0, teamSize);
        }
        if (!hasLeaderReq) {
            ObjectNode leader = objectMapper.createObjectNode();
            leader.put("label", "Leader Required");
            leader.put("type", "select");
            leader.put("required", false);
            leader.put("options", "Yes,No");
            arr.insert(1, leader);
        }
    }
}
