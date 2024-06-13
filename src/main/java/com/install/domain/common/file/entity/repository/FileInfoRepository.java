package com.install.domain.common.file.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.install.domain.common.file.entity.FileInfo;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.common.file.entity.repository
 * @since : 07.06.24
 */
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {

}
