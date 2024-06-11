package com.install.domain.modem.entity.repository;

import com.install.domain.modem.entity.Modem;
import com.install.domain.modem.entity.repository.query.ModemRepositoryCustom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.modem.entity.repository
 * @since : 05.06.24
 */
public interface ModemRepository extends JpaRepository<Modem, Long>, ModemRepositoryCustom {

  Optional<Modem> findByModemNo(String modemNo);

  boolean existsByModemNo(String modemNo);

  boolean existsByImei(String imei);
}
