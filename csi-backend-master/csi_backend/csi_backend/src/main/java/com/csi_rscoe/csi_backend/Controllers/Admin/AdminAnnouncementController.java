package com.csi_rscoe.csi_backend.Controllers.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.csi_rscoe.csi_backend.Models.Announcement;
import com.csi_rscoe.csi_backend.Repositories.AnnouncementRepository;
import java.util.List;

@RestController
@RequestMapping("/api/Admin/Announcements")
@CrossOrigin(origins = {"http://localhost:5173"})
public class AdminAnnouncementController {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Announcement> createAnnouncement(@RequestBody Announcement announcement) {
        announcement.setCreatedAt(new java.util.Date());
        Announcement saved = announcementRepository.save(announcement);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAnnouncement(@PathVariable Long id) {
        if (announcementRepository.existsById(id)) {
            announcementRepository.deleteById(id);
            return ResponseEntity.ok("Announcement deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Announcement not found");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
