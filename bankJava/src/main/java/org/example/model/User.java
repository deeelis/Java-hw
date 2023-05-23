package org.example.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	@NotBlank
	private String login;
	@Column
	@NotBlank
	private String password;
	@Column
	@NotNull
	@Min(0)
	private Long summ;
	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;
	@Column
	private OffsetDateTime timestamp;


}
