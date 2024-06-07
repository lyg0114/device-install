package com.install.domain.common.file.entity.repository;

import com.install.domain.common.file.entity.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.common.file.entity.repository
 * @since : 07.06.24
 */
public interface FileInfoRepository extends JpaRepository<FileInfo, Long> {

}
