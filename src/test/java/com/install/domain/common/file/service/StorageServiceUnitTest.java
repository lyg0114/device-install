package com.install.domain.common.file.service;

import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.install.domain.common.file.config.StorageProperties;
import com.install.domain.common.file.entity.FileInfo;
import com.install.domain.common.file.entity.repository.FileInfoRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.common.file.service
 * @since : 07.06.24
 */
@ExtendWith(MockitoExtension.class) // 어노테이션을 사용할 경우 추가해 주어야함
@DisplayName("StorageService Unit test")
class StorageServiceUnitTest {

  @Mock
  private FileInfoRepository fileInfoRepository;
  private StorageService storageService;
  private StorageProperties properties = new StorageProperties();

  @BeforeEach
  public void init() {
    properties.setLocation("build/static/images/" + Math.abs(new Random().nextLong()));
    storageService = new StorageServiceImpl(properties, fileInfoRepository);
    storageService.init();
  }

  // MockMultipartFile 생성
  private MockMultipartFile createMockImageFile() {
    String fileName = "sample.jpg";
    InputStream inputStream = getClass().getResourceAsStream("/images/" + fileName);
    byte[] bytes;
    try {
      bytes = inputStream.readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return new MockMultipartFile(
        "file",
        fileName,
        MediaType.IMAGE_JPEG_VALUE,
        bytes
    );
  }

  @Test
  void 파일_저장후_조회에_성공한다() {
    //given
    // mockFile 생성
    MockMultipartFile multipartFile = createMockImageFile();
    StringBuilder builder = new StringBuilder();
    String fileType = "." + requireNonNull(multipartFile.getContentType()).split("/")[1];
    String randomNum = randomUUID().toString().substring(0, 6);
    Long fileId = 7L;

    String fileName = builder.append(System.currentTimeMillis())
        .append("_").append(randomNum)
        .append(fileType)
        .toString();

    builder.delete(0, builder.length()); // builder 초기화
    String dirPath = builder
        .append("sample-dir")
        .append("/")
        .append(fileId).append("/")
        .toString();

    // mockFile 저장
    storageService.store(multipartFile, dirPath, fileName);

    // fileInfoRepository mocking
    when(fileInfoRepository.findById(fileId))
        .thenReturn(Optional.of(FileInfo.builder()
            .fileUri(dirPath + "/" + fileName)
            .fileSize(multipartFile.getSize())
            .id(fileId)
            .build()));

    //when
    Resource resource = storageService.loadAsResource(fileId);

    //then
    assertThat(resource.exists()).isTrue();
  }
}