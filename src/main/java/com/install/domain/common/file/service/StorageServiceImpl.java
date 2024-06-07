package com.install.domain.common.file.service;

import static com.install.global.exception.CustomErrorCode.FAIL_ITIT_FILE_DIRECTORY;
import static com.install.global.exception.CustomErrorCode.FILE_NOT_EXIST;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Path.of;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import com.install.domain.common.file.config.StorageProperties;
import com.install.domain.common.file.entity.FileInfo;
import com.install.domain.common.file.entity.repository.FileInfoRepository;
import com.install.global.exception.CustomException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.common.file.service
 * @since : 07.06.24
 */
@Slf4j
@Transactional
@Service
public class StorageServiceImpl implements StorageService {

  private final Path rootLocation;
  private final FileInfoRepository fileInfoRepository;

  public StorageServiceImpl(
      StorageProperties properties, FileInfoRepository fileInfoRepository) {
    this.rootLocation = get(properties.getLocation());
    this.fileInfoRepository = fileInfoRepository;
  }

  @Override
  public void init() {
    try {
      createDirectories(rootLocation);
    } catch (IOException ex) {
      log.error("[CustomException] errorCode: {} | errorMessage: {} | cause Exception: ",
          FAIL_ITIT_FILE_DIRECTORY.getHttpStatus().value(),
          FAIL_ITIT_FILE_DIRECTORY.getErrorMessage(), ex);
      throw new CustomException(FAIL_ITIT_FILE_DIRECTORY);
    }
  }

  @Override
  public void store(MultipartFile multipartFile, String dirPath, String fileName) {
    validateIsExistFile(multipartFile);

    try {
      createDirectories(of(rootLocation + "/" + dirPath));
      Path destinationFile = rootLocation
          .resolve(get(dirPath, fileName))
          .normalize()
          .toAbsolutePath();

      log.info("destinationFile - {}", destinationFile);

      try (InputStream inputStream = multipartFile.getInputStream()) {
        copy(inputStream, destinationFile, REPLACE_EXISTING);
      } catch (IOException e) {
        throw new RuntimeException(e); // TODO : 예외처리 개선 필요
      }
    } catch (IOException e) {
      throw new RuntimeException(e); // TODO : 예외처리 개선 필요
    }
  }

  private static void validateIsExistFile(MultipartFile multipartFile) {
    if (multipartFile.isEmpty()) {
      throw new CustomException(FILE_NOT_EXIST);
    }
  }

  @Override
  public Path load(Long fileId) {
    FileInfo fileInfo = fileInfoRepository.findById(fileId)
        .orElseThrow((() -> new CustomException(FILE_NOT_EXIST)));

    return rootLocation.resolve(fileInfo.getFileUri());
  }

  //TODO : 예외처리 추가 작업 필요
  @Override
  public Resource loadAsResource(Long fileId) {
    try {
      Path file = load(fileId);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        return null;
//        throw new StorageFileNotFoundException("Could not read fileId: " + fileId);
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
      return null;
//      throw new StorageFileNotFoundException("Could not read fileId: " + fileId, e);
    }
  }
}
