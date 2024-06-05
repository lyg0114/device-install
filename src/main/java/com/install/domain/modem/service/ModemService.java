package com.install.domain.modem.service;

import com.install.domain.modem.entity.repository.ModemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.modem.service
 * @since : 05.06.24
 */
@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ModemService {

  private final ModemRepository modemRepository;

}
