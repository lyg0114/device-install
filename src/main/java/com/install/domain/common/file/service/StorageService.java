package com.install.domain.common.file.service;

import java.nio.file.Path;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.common.file.service
 * @since : 07.06.24
 */
public interface StorageService {

  void init();

  void store(MultipartFile file, String dirPath, String fileName);

  Path load(Long fileId);

}
