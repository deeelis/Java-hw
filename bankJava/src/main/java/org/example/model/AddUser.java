package org.example.model;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
@Data
public class AddUser {

	@NotBlank
	String login;

	@NotBlank
	String password;
	@NotNull
	@Min(0)
	Long summ;
	Long role;
	Boolean deposit;
}
