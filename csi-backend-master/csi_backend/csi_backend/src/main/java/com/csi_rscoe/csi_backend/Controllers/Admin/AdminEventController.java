package com.csi_rscoe.csi_backend.Controllers.Admin;

import com.csi_rscoe.csi_backend.Models.Event;
import com.csi_rscoe.csi_backend.Repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/events")
@CrossOrigin(origins = { "http://localhost:5173" })
public class AdminEventController {

    @Autowired
    private EventRepository eventRepository;

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
                .map(e -> ResponseEntity.ok()
                        .body(e.getRegistrationFieldsJson() != null ? e.getRegistrationFieldsJson()
                                : e.getRegistrationSchemaJson()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{id}/registration-schema", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateRegistrationSchema(@PathVariable Long id, @RequestBody String schemaJson) {
        Optional<Event> existing = eventRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Event e = existing.get();
        e.setRegistrationFieldsJson(schemaJson);
        eventRepository.save(e);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
