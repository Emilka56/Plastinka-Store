package org.example.plastinka2.repository;

import org.example.plastinka2.models.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {
    FileInfo findByStorageName(String fileName);
}
