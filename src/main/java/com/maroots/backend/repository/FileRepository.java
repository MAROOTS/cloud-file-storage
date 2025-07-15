package com.maroots.backend.repository;

import com.maroots.backend.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FileRepository extends JpaRepository<FileEntity, UUID> {
    List<FileEntity> findByOwnerEmail(String ownerEmail);
}
