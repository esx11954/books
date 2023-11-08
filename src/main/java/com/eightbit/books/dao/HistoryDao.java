package com.eightbit.books.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class HistoryDao {

	@Autowired
	EntityManager entityManager;

	public List getAllHistory() {

		Query query = entityManager.createNativeQuery(
				"select h.id, book_id, user_id, checkout_date, due_date, return_date, returned, last_name, first_name, tel, title "
						+ "from history h " + "join user u on h.user_id = u.id " + "join books b on h.book_id = b.id");
		return query.getResultList();
	}

}
