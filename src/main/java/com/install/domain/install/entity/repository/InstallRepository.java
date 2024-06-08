package com.install.domain.install.entity.repository;

import com.install.domain.install.entity.InstallInfo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.install.entity.repository
 * @since : 05.06.24
 */
public interface InstallRepository
    extends JpaRepository<InstallInfo, Long>, InstallRepositoryCustom {

  @Query(
      "select i1 from InstallInfo i1 "
          + "where i1.modem.id = :modemId "
          + "and i1.workTime in (select max(i2.workTime) from InstallInfo i2)"
  )
  Optional<InstallInfo> currentInstallStateInfo(Long modemId);
}
