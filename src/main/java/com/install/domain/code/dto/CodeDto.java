package com.install.domain.code.dto;

import static lombok.AccessLevel.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.common
 * @since : 11.06.24
 */

@ToString
@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
public class CodeDto {

	private String code;
	private String name;
}
