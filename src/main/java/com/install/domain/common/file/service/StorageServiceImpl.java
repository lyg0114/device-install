package com.install.domain.common.file.service;

import static com.install.global.exception.CustomErrorCode.*;
import static java.nio.file.Files.*;
import static java.nio.file.Path.*;
import static java.nio.file.Paths.*;
import static java.nio.file.StandardCopyOption.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.install.domain.common.config.StorageProperties;
import com.install.domain.common.file.entity.FileInfo;
import com.install.domain.common.file.entity.repository.FileInfoRepository;
import com.install.global.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

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

	private static void validateIsExistFile(MultipartFile multipartFile) {
		if (multipartFile.isEmpty()) {
			throw new CustomException(FILE_NOT_EXIST);
		}
	}

	@Override
	public void init() {
		try {
			createDirectories(rootLocation);
		} catch (IOException ex) {
			throw new CustomException(FAIL_ITIT_FILE_DIRECTORY, ex);
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
			} catch (IOException ex) {
				throw new CustomException(FILE_STORE_ERROR, ex);
			}
		} catch (IOException ex) {
			throw new CustomException(FILE_STORE_ERROR, ex);
		}
	}

	@Override
	public Path load(Long fileId) {
		FileInfo fileInfo = fileInfoRepository.findById(fileId)
			.orElseThrow((() -> new CustomException(FILE_NOT_EXIST)));

		return rootLocation.resolve(fileInfo.getFileUri());
	}

	@Override
	public Resource loadAsResource(Long fileId) {
		try {
			Path file = load(fileId);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new CustomException(FILE_NOT_EXIST, "Could not read fileId: " + fileId);
			}
		} catch (MalformedURLException ex) {
			throw new CustomException(FILE_NOT_EXIST, "Could not read fileId: " + fileId, ex);
		}
	}
}
