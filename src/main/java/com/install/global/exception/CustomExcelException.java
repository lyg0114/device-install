package com.install.global.exception;

/**
 * @author : iyeong-gyo
 * @package : com.install.global.exception
 * @since : 20.06.24
 */
public class CustomExcelException extends CustomException {

	private final int row;
	private final int col;
	private final String value;
	private final String splitStr = "|:|";

	public CustomExcelException(CustomErrorCode errorCode, String value, int row, int col) {
		super(errorCode);
		this.value = value;
		this.row = row;
		this.col = col;
	}

	public String getTargetInfo() {
		return new StringBuilder(value)
			.append(splitStr)
			.append(row)
			.append(splitStr)
			.append(col)
			.toString();
	}

	@Override
	public String getErrorMessage() {
		return new StringBuilder(super.getErrorMessage())
			.append("\n")
			.append("value : ")
			.append(value)
			.append("\n")
			.append("Location : ")
			.append("Row ")
			.append(row)
			.append(", Col ")
			.append(col)
			.toString()
			;
	}
}
