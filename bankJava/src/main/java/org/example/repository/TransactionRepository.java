package org.example.repository;

import org.example.model.StatusTransactions;
import org.example.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
	@Query("select u1.login, u2.login, t.summ " +
			"from User as u1 join Transaction as t on u1.id=t.user1.id " +
			"join User as u2 on u2.id=t.user2.id " +
			"where u1.id=:id")
	ArrayList<?> getStatusFrom(@Param("id") Long id);
	@Query("select u1.login, u2.login, t.summ " +
			"from User as u1 join Transaction as t on u1.id=t.user1.id " +
			"join User as u2 on u2.id=t.user2.id " +
			"where u2.id=:id")
	ArrayList<?> getStatusTo(@Param("id") Long id);
}
