package com.maroots.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
public class FileEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String type;
    private Long size;
    private String s3Key;
    private String ownerEmail;
}
