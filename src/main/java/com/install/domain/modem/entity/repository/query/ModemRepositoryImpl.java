package com.install.domain.modem.entity.repository.query;

import com.install.domain.modem.dto.ModemDto.ModemSearchCondition;
import com.install.domain.modem.entity.Modem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.modem.entity.repository.query
 * @since : 11.06.24
 */
@RequiredArgsConstructor
public class ModemRepositoryImpl implements ModemRepositoryCustom {

  @Override
  public Page<Modem> searchModems(ModemSearchCondition condition, Pageable pageable) {
    return null;
  }
}
