package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.model.*;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/bank")
@Validated
@AllArgsConstructor
public class Controller {
	@Autowired
	private UserService userService;


	@Secured({"ROLE_USER", "ROLE_ADMIN","ROLE_DEPOSIT"})
	@GetMapping("/")
	public String hello(Authentication auth) {
		return "Hello! " + auth.getName();
	}

	@Secured({"ROLE_ADMIN"})
	@GetMapping("/admin")
	public String helloAdmin(Authentication auth) {
		return "Hello, Admin!";
	}

	@PostMapping("/addRole")
	public Long addRole(@Valid @RequestBody AddRole addRole, Authentication auth) {
		return userService.addRole(addRole);
	}

	@PostMapping("/addUser")
	public Long addUser(@Valid @RequestBody AddUser addUser) {
		addUser.setPassword(new BCryptPasswordEncoder().encode(addUser.getPassword()));
		return userService.addUser(addUser);
	}

	@Secured({"ROLE_USER", "ROLE_ADMIN"})
	@PostMapping("/transaction")
	public String transaction(@Valid @RequestBody RequestTransaction requestTransaction, Authentication auth) {
		if (requestTransaction.getUser1Id() == null) {
			requestTransaction.setUser1Id(userService.getUser(auth.getName()).getId());
		}
		Long id = userService.transaction(requestTransaction);
		if (id != -1) {
			return id.toString();
		} else {
			return "not enough money";
		}
	}

	@Secured({"ROLE_ADMIN"})
	@PostMapping("/cancel_transaction/{id}")
	public ResponseEntity cancelTransaction(@PathVariable("id") Long id, Authentication auth) {
		int status = userService.cancelTransaction(id);
		if (status == 0) {
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}

	@Secured({"ROLE_USER", "ROLE_ADMIN","ROLE_DEPOSIT"})
	@GetMapping("/getStatus")
	public GetStatus getStatus(Authentication auth) {
		User user = userService.getUser(auth.getName());
		return userService.getStatus(user.getId());
	}

}
