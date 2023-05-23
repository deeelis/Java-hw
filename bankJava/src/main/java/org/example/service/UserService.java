package org.example.service;

import org.example.model.*;
import org.example.repository.RoleRepository;
import org.example.repository.TransactionRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;


@Service
public class UserService {
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;

	@Transactional
	public Long addRole(AddRole addRole){
		Role role=new Role();
		role.setName(addRole.getName());
		roleRepository.save(role);
		return role.getId();
	}

	@Transactional
	public Long addUser(AddUser addUser) {
		User user = new User();
		user.setLogin(addUser.getLogin());
		user.setPassword(addUser.getPassword());
		user.setSumm(addUser.getSumm());
		if(addUser.getRole()==null || addUser.getRole().equals("")){
			user.setRole(roleRepository.findByName("ROLE_USER"));
		}else {
			user.setRole(roleRepository.findById(addUser.getRole()).orElseThrow());
		}
		if(addUser.getDeposit()==null){
			addUser.setDeposit(false);
		}
		if(addUser.getDeposit()){
			user.setTimestamp(OffsetDateTime.now());
			user.setRole(roleRepository.findByName("ROLE_DEPOSIT"));
		}
		userRepository.save(user);
		return user.getId();
	}

	@Transactional
	 synchronized public Long transaction(RequestTransaction requestTransaction) {
		Transaction transaction = new Transaction();
			User user1 = userRepository.findById(requestTransaction.getUser1Id()).orElseThrow();
			User user2 = userRepository.findById(requestTransaction.getUser2Id()).orElseThrow();
			if (user1.getSumm() >= requestTransaction.getSumm()) {
				transaction.setUser1(user1);
				transaction.setUser2(user2);
				transaction.setSumm(requestTransaction.getSumm());
				user1.setSumm(user1.getSumm() - requestTransaction.getSumm());
				user2.setSumm(user2.getSumm() + requestTransaction.getSumm());
				transactionRepository.save(transaction);
				return transaction.getId();
			}else {
				return -1L;
			}


	}

	@Transactional
	synchronized public int cancelTransaction(Long idTransaction) {

		Transaction transaction = transactionRepository.findById(idTransaction).orElseThrow();

			User user1 = transaction.getUser1();
			User user2 = transaction.getUser2();
			if (user2.getSumm() >= transaction.getSumm()) {
				user2.setSumm(user2.getSumm() - transaction.getSumm());
				user1.setSumm(user1.getSumm() + transaction.getSumm());
				Transaction transaction1=new Transaction();
				transaction1.setUser1(user2);
				transaction1.setUser2(user1);
				transaction1.setSumm(transaction.getSumm());
				transactionRepository.save(transaction1);
				return 0;
			}else {
				return -1;
			}


	}
	@Transactional
	synchronized public void updateBalance(Long id){
		User user=userRepository.findById(id).orElseThrow();
		int year;
		int month;
		int total;
		int procent=1;
		if(user.getTimestamp()!=null){
			OffsetDateTime offsetDateTime=OffsetDateTime.now().plusMonths(3);
			year=offsetDateTime.getYear()-user.getTimestamp().getYear();
			month=offsetDateTime.getMonthValue()-user.getTimestamp().getMonthValue();
			if(year==0){
				total=month;
			}else{
					total=year*12+offsetDateTime.getMonthValue();
			}
			user.setSumm(user.getSumm()+user.getSumm()*(procent+(procent*total)));
			user.setTimestamp(offsetDateTime);
		}
	}
	@Transactional
	public GetStatus getStatus(Long id){
		GetStatus getStatus=new GetStatus();
		User user=userRepository.findById(id).orElseThrow();
		if(user.getRole().getName().equals("ROLE_DEPOSIT")){
			updateBalance(user.getId());
		}
		getStatus.setSumm(user.getSumm());
		getStatus.setFromTransaction(transactionRepository.getStatusFrom(id));
		getStatus.setToTransaction(transactionRepository.getStatusTo(id));
		return  getStatus;
	}
	@Transactional
	public User getUser(String login){
		return userRepository.findByLogin(login);
	}


}
