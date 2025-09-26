package com.csi_rscoe.csi_backend.Controllers.Public;

import com.csi_rscoe.csi_backend.Models.Event;
import com.csi_rscoe.csi_backend.Models.Blog;
import com.csi_rscoe.csi_backend.Models.TeamMember;
import com.csi_rscoe.csi_backend.Models.Announcement;
import com.csi_rscoe.csi_backend.Repositories.EventRepository;
import com.csi_rscoe.csi_backend.Repositories.BlogRepository;
import com.csi_rscoe.csi_backend.Repositories.TeamMemberRepository;
import com.csi_rscoe.csi_backend.Repositories.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/public")
@CrossOrigin(origins = {"http://localhost:5173"})
public class PublicContentController {

    @Autowired private EventRepository eventRepository;
    @Autowired private BlogRepository blogRepository;
    @Autowired private TeamMemberRepository teamMemberRepository;
    @Autowired private AnnouncementRepository announcementRepository;

    @GetMapping("/events")
    public List<Event> events() {
        List<Event> list = eventRepository.findAll();
        // Populate transient fields if missing so the client can display QR
        for (Event e : list) {
            if (e.getQrCodeUrl() == null && e.getRulebookUrl() != null) {
                String u = e.getRulebookUrl();
                String lower = u.toLowerCase(Locale.ROOT);
                if (lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
                    e.setQrCodeUrl(u);
                }
            }
        }
        return list;
    }

    @GetMapping("/blogs")
    public List<Blog> blogs() { return blogRepository.findAll(); }

    @GetMapping("/team")
    public List<TeamMember> team() { return teamMemberRepository.findAll(); }

    @GetMapping("/announcements")
    public List<Announcement> announcements() { return announcementRepository.findAll(); }
}


