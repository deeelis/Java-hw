package org.example;

import org.example.model.AddUser;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.RoleRepository;
import org.example.repository.TransactionRepository;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;


@SpringBootTest
@AutoConfigureMockMvc
public class MyTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;


	@BeforeEach
	public void addUsers(){
		Role role1=new Role();
		role1.setName("ROLE_USER");
		roleRepository.save(role1);
		Role role2=new Role();
		role2.setName("ROLE_ADMIN");
		roleRepository.save(role2);
		User addUser1=new User();
		addUser1.setLogin("user1");
		addUser1.setPassword("123");
		addUser1.setSumm(1000L);
		userRepository.save(addUser1);
		User addUser2=new User();
		addUser2.setLogin("user2");
		addUser2.setPassword("123");
		addUser2.setSumm(1000L);
		userRepository.save(addUser2);
		User addUser3=new User();
		addUser3.setLogin("user3");
		addUser3.setPassword("123");
		addUser3.setSumm(1000L);
		addUser3.setRole(role2);
		userRepository.save(addUser3);

	}
	@Test
	@WithMockUser(username = "user1", authorities = {  "ROLE_USER" })
	public void testTransactions() throws Exception {
		mockMvc.perform(post("/bank/transaction")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"user1Id": 3,
							"user2Id": 1,
							"summ": 300
						}
						""")).andExpect(status().isOk());
		assertThat(transactionRepository.findAll(), hasItem(
				allOf(
						hasProperty("user1", hasProperty("id",equalTo(3L))),
						hasProperty("user2", hasProperty("id",equalTo(1L))),
						hasProperty("summ", equalTo(300L))
				)));

		int i = 1300;
		assertEquals(userRepository.findById(1L).get().getSumm(),1300L);
		assertEquals(1,1);
	}
	@Test
	@WithMockUser(username = "user3", authorities = {  "ROLE_ADMIN" })
	public void testCancelTransaction() throws Exception {
		mockMvc.perform(post("/bank/transaction")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"user1Id": 3,
							"user2Id": 1,
							"summ": 300
						}
						""")).andExpect(status().isOk());
		mockMvc.perform(post("/bank/cancel_transaction/1"))
				.andExpect(status().isOk());
		assertEquals(userRepository.findById(1L).get().getSumm(),1000L);
	}
	@Test
	@WithMockUser(username = "user1", authorities = {  "ROLE_USER" })
	public void testCancelTransaction2() throws Exception {
		mockMvc.perform(post("/bank/transaction")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"user1Id": 1,
							"user2Id": 3,
							"summ": 300
						}
						""")).andExpect(status().isOk());
		mockMvc.perform(post("/bank/cancel_transaction/1"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(username = "user1", authorities = {  "ROLE_USER" })
	public void testGetStatus() throws Exception {
		mockMvc.perform(post("/bank/transaction")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"user1Id": 3,
							"user2Id": 1,
							"summ": 300
						}
						""")).andExpect(status().isOk());
		mockMvc.perform(post("/bank/transaction")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"user1Id": 1,
							"user2Id": 2,
							"summ": 400
						}
						""")).andExpect(status().isOk());
		mockMvc.perform(get("/bank/getStatus")).
				andExpect(status().isOk())
				.equals("""
{
    "summ": 900,
    "fromTransaction": [
        [
            "user1",
            "user2",
            400
        ]
    ],
    "toTransaction": [
        [
            "user3",
            "user1",
            300
        ]
    ]
}
						""");
	}


}
