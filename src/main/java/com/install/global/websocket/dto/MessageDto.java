package com.install.global.websocket.dto;

import static lombok.AccessLevel.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author : iyeong-gyo
 * @package : com.install.global.websocket.dto
 * @since : 21.06.24
 */
@ToString
@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class MessageDto {
	private int progress;
	private String value;
	private int row;
	private int col;
	private boolean isSuccess;

}
