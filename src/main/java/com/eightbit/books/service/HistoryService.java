package com.eightbit.books.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eightbit.books.entity.Books;
import com.eightbit.books.entity.History;
import com.eightbit.books.entity.User;
import com.eightbit.books.model.HistorySearchQuery;
import com.eightbit.books.repository.BooksRepository;
import com.eightbit.books.repository.HistoryRepository;
import com.eightbit.books.repository.StatusRepository;
import com.eightbit.books.repository.UserRepository;

@Service
public class HistoryService {

	@Autowired
	private HistoryRepository historyRepo;
	@Autowired
	private BooksRepository booksRepo;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private StatusRepository statusRepo;

	
	public List<History> findAll(){
		return historyRepo.findAllByOrderByCheckoutDateDesc();
	}
	
	/**
	 * ★貸出履歴検索(特定データ)
	 * @param historyId
	 * @return 貸出履歴特定データ
	 */
	public History findOne(int historyId) {

		return null;
	}
	
	/**
	 * ★貸出履歴検索(期間、利用者、書籍名)
	 * @param searchQuery
	 * @return 貸出履歴特定データ(複数)
	 */
	public List<History> searchHistory(HistorySearchQuery searchQuery) {
		String fromDate = searchQuery.getFrom();
		String toDate = searchQuery.getTo();
		String book = searchQuery.getBookName();
		String user = searchQuery.getUserName();

		Date fd = ServiceUtility.parseDate(fromDate);
		Date td = ServiceUtility.parseDate(toDate);
		
		List<History> historyList = null;

		if (book.isBlank() && user.isBlank()) {
			// 日付のみで検索

		} else if (!book.isBlank() && user.isBlank()) {
			// 日付と書籍名で検索

		} else if (book.isBlank() && !user.isBlank()) {
			// 日付とユーザで検索

		} else {
			// 日付、書籍名、ユーザで検索

		}
		return historyList;

	}

	/**
	 * 受け取ったパラメータで該当するデータをbooksテーブルから抽出し、 該当データのIDのみをリストにまとめる
	 * 
	 * @param 検索書籍名(曖昧検索)
	 * @return 該当データのIDリスト
	 */
	private List<Integer> getBookIdList(String book) {
		List<Books> bookList = booksRepo.findByTitleContaining(book);
		return bookList.stream().map(b -> b.getBookId()).collect(Collectors.toList());
	}

	/**
	 * 受け取ったパラメータで該当するデータをuserテーブルから抽出し、 該当データのIDのみをリストにまとめる
	 * 
	 * @param 検索ユーザ名(曖昧検索)
	 * @return 該当データのIDリスト
	 */
	private List<Integer> getUserIdList(String user) {
		List<User> userLastNameList = userRepo.findByLastNameContaining(user);
		List<User> userFirstNameList = userRepo.findByFirstNameContaining(user);
		List<User> UserObjList = Stream.concat(userLastNameList.stream(), userFirstNameList.stream())
				.collect(Collectors.toList());
		return UserObjList.stream().map(u -> u.getUserId()).collect(Collectors.toList());
	}

	/**
	 * ★貸出履歴特定データ削除
	 * @param historyId
	 */
	public void deleteHistory(int historyId) {

	}

	private void incrementStock(int bookId) {
		Books book = booksRepo.getReferenceById((long) bookId);
		int stock = book.getStock();
		book.setStock(stock + 1);
		if (stock == 0) {
			book.setStatus(statusRepo.getReferenceById((long) 1));
		}
		booksRepo.save(book);
	}

	private void decrementStock(int bookId) {
		Books book = booksRepo.getReferenceById((long) bookId);
		int stock = book.getStock();
		book.setStock(stock - 1);
		if (stock == 1) {
			book.setStatus(statusRepo.getReferenceById((long) 2));
		}
		booksRepo.save(book);
	}

	/**
	 * ★貸出履歴特定データ返却期限更新
	 * @param historyId
	 * @param dueDate
	 */
	public void updateDueDate(int historyId, String dueDate) {

	}

	/**
	 * ★貸出履歴特定データ返却
	 * @param historyId
	 */
	public void returnBook(int historyId) {

	}

	/**
	 * ★特定書籍貸出
	 * @param bookId
	 * @param userId
	 * @param dueDateStr
	 */
	public void checkoutBook(int bookId, int userId, String dueDateStr) {

	}
}
