package com.install.domain.modem.entity.repository.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.install.domain.modem.dto.ModemDto.ModemInstallCount;
import com.install.domain.modem.dto.ModemDto.ModemSearchCondition;
import com.install.domain.modem.entity.Modem;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.modem.entity.repository.query
 * @since : 11.06.24
 */
public interface ModemRepositoryCustom {

	Page<Modem> searchModems(ModemSearchCondition condition, Pageable pageable);

	ModemInstallCount modeminstallCount();
}
