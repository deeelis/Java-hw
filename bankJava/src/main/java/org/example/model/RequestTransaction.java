package org.example.model;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
@Data
public class RequestTransaction {

	Long user1Id;
	@NotNull
	Long user2Id;
	@NotNull
	@Min(0)
	Long summ;
}
