package com.install.domain.install.entity.repository;

import com.install.domain.install.entity.InstallInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.install.entity.repository
 * @since : 05.06.24
 */
public interface InstallRepositoryCustom {

  Page<InstallInfo> searchInstallInfoPageByModem(Long modemId, Pageable pageable);
}
