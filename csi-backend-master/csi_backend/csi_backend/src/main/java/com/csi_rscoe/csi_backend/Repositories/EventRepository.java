package com.csi_rscoe.csi_backend.Repositories;

import com.csi_rscoe.csi_backend.Models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(String status);
}


