package org.example.model;

import lombok.*;

import javax.persistence.*;
@Data
@Entity
@Table (name="roles")
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String name;

}
