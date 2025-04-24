package com.example.backend.pdf;

import com.example.backend.entity.Pdf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PdfRepository extends JpaRepository<Pdf, Long> {
    List<Pdf> findAllByUserId(Long userId);

    Optional<Pdf> findByMongoObjectId(String objectId);
}
