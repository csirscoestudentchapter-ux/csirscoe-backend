package com.csi_rscoe.csi_backend.Controllers.Admin;

import com.csi_rscoe.csi_backend.Models.TeamMember;
import com.csi_rscoe.csi_backend.Repositories.TeamMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/team")
@CrossOrigin(origins = {"http://localhost:5173"})
public class AdminTeamMemberController {

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @GetMapping
    public List<TeamMember> getAll() { return teamMemberRepository.findAll(); }

    @PostMapping
    public TeamMember create(@RequestBody TeamMember member) { return teamMemberRepository.save(member); }

    @PutMapping("/{id}")
    public TeamMember update(@PathVariable Long id, @RequestBody TeamMember updated) {
        Optional<TeamMember> existing = teamMemberRepository.findById(id);
        if (existing.isPresent()) {
            TeamMember m = existing.get();
            m.setName(updated.getName());
            m.setRole(updated.getRole());
            m.setImage(updated.getImage());
            m.setLinkedin(updated.getLinkedin());
            m.setEmail(updated.getEmail());
            return teamMemberRepository.save(m);
        }
        return teamMemberRepository.save(updated);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { teamMemberRepository.deleteById(id); }
}


