package com.techbank.account.cmd.api.dto;

import com.techbank.account.common.dto.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenAccountResponse extends BaseResponse {
	private String id;

	public OpenAccountResponse(final String message, final String id) {
		super(message);
		this.id = id;
	}
}
