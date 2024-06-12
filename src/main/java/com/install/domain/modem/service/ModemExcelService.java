package com.install.domain.modem.service;

import com.install.domain.modem.entity.repository.ModemRepository;
import com.install.global.websocket.handler.ProgressWebSocketHandler;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.modem.service
 * @since : 12.06.24
 */
@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ModemExcelService {

  private final ModemRepository modemRepository;
  private final ProgressWebSocketHandler progressWebSocketHandler;

  public void uploadModemExcel(MultipartFile file, String sessionId) {
    int totalRows = getTotalRows(file);
    try {
      for (int i = 0; i < totalRows; i++) {
        int progress = (i + 1) * 100 / totalRows;
        progressWebSocketHandler.sendProgressUpdate(sessionId, Integer.toString(progress));
        sleep();
      }

      progressWebSocketHandler.closeSession(sessionId);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void sleep() {
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private int getTotalRows(MultipartFile file) {
    return 100;
  }
}
