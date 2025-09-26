package com.csi_rscoe.csi_backend.Repositories;

import com.csi_rscoe.csi_backend.Models.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    List<EventRegistration> findByEventId(Long eventId);
}


