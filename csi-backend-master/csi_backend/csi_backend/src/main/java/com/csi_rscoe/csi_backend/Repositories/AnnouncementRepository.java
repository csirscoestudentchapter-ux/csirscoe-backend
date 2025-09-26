package com.csi_rscoe.csi_backend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.csi_rscoe.csi_backend.Models.Announcement;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
}
