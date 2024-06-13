package com.install.domain.consumer.dto;

import static lombok.AccessLevel.*;
import static org.springframework.util.StringUtils.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.install.domain.consumer.entity.Address;
import com.install.domain.consumer.entity.Address.AddressBuilder;
import com.install.domain.consumer.entity.Consumer;
import com.install.domain.consumer.entity.Consumer.ConsumerBuilder;
import com.install.domain.consumer.entity.Location;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * @author : iyeong-gyo
 * @package : com.install.domain.consumer.dto
 * @since : 03.06.24
 */
public class ConsumerDto {

	@ToString
	@Getter
	@Builder
	@AllArgsConstructor(access = PRIVATE)
	public static class ConsumerSearchCondition {
		private String modemNo;
		private String meterNo;
		private String consumerNo;
		private LocalDateTime from;
		private LocalDateTime to;
	}

	@ToString
	@Getter
	@Builder
	@AllArgsConstructor(access = PRIVATE)
	public static class ConsumerRequest {

		@NotBlank(message = "고객번호를 입력 해주세요.")
		private String consumerNo;

		@NotBlank(message = "고객명을 입력 해주세요.")
		private String consumerName;

		@NotBlank(message = "계량기 번호을 입력 해주세요.")
		private String meterNo;

		@NotBlank(message = "도시를 입력 해주세요.")
		private String city;

		private String street;
		private String zipcode;
		private String geoX;
		private String geoY;

		public Consumer toEntity() {
			ConsumerBuilder builder = Consumer.builder()
				.consumerNo(this.consumerNo)
				.consumerName(this.consumerName)
				.meterNo(this.meterNo);

			// 주소정보
			AddressBuilder addressBuilder = Address.builder().city(this.city);
			if (hasText(this.street)) {
				addressBuilder.street(this.street);
			}
			if (hasText(this.zipcode)) {
				addressBuilder.street(this.zipcode);
			}
			builder.address(addressBuilder.build());

			// 위치정보
			if (hasText(geoX) && hasText(geoY)) {
				builder.location(Location.builder()
					.geoX(this.geoX)
					.geoY(this.geoY)
					.build());
			}
			return builder.build();
		}
	}

	@ToString
	@Getter
	@Builder
	@AllArgsConstructor
	public static class ConsumerResponse {

		private String consumerNo;
		private String consumerName;
		private String meterNo;
		private String city;
		private String street;
		private String zipcode;
		private String geoX;
		private String geoY;

		private String installedModemNo;
		private BigDecimal usage;
	}

}
