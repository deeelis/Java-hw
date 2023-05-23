package org.example.model;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class StatusTransactions {
	@NotBlank
	String user1;
	@NotBlank
	String user2;
	@NotNull
	@Min(0)
	Long summ;
}
