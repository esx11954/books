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
		return historyRepo.findById(historyId);
	}
	
	/**
	 * ★貸出履歴検索(期間、利用者、書籍名)
	 * @param searchQuery
	 * @return 貸出履歴特定データ(複数)
	 */
	public List<History> searchHistory(HistorySearchQuery searchQuery) {
		Date fromDate = ServiceUtility.parseDate(searchQuery.getFrom());
		Date toDate = ServiceUtility.parseDate(searchQuery.getTo());
		String book = searchQuery.getBookName();
		String user = searchQuery.getUserName();

		List<History> historyList = null;

		if (book.isBlank() && user.isBlank()) {
			// 日付のみで検索
			historyList = historyRepo.findByCheckoutDateBetweenOrderByCheckoutDateDesc(
					fromDate, toDate);

		} else if (!book.isBlank() && user.isBlank()) {
			// 日付と書籍名で検索
			List<Integer> bookIdList = getBookIdList(book);
			historyList = historyRepo.findByCheckoutDateBetweenAndBooksBookIdInOrderByCheckoutDateDesc(
					fromDate, toDate, bookIdList);

		} else if (book.isBlank() && !user.isBlank()) {
			// 日付とユーザで検索
			List<Integer> userIdList = getUserIdList(user);
			historyList = historyRepo.findByCheckoutDateBetweenAndUserUserIdInOrderByCheckoutDateDesc(
					fromDate, toDate, userIdList);
		} else {
			// 日付、書籍名、ユーザで検索
			List<Integer> bookIdList = getBookIdList(book);
			List<Integer> userIdList = getUserIdList(user);
			historyList = historyRepo.findByCheckoutDateBetweenAndBooksBookIdInAndUserUserIdInOrderByCheckoutDateDesc(
					fromDate, toDate, bookIdList, userIdList);
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
		History history = historyRepo.getReferenceById((long) historyId);
		if (history.getReturned().equals("F")) {
			int bookId = history.getBooks().getBookId();
			this.incrementStock(bookId);
		}
		historyRepo.deleteById((long) historyId);
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
		History history = historyRepo.getReferenceById((long) historyId);
		history.setDueDate(ServiceUtility.parseDate(dueDate));
		historyRepo.save(history);
	}

	/**
	 * ★貸出履歴特定データ返却
	 * @param historyId
	 */
	public void returnBook(int historyId) {
		History history = historyRepo.getReferenceById((long) historyId);
		history.setReturnDate(new Date());
		history.setReturned("T");
		this.incrementStock(history.getBooks().getBookId());
		historyRepo.save(history);
	}

	/**
	 * ★特定書籍貸出
	 * @param bookId
	 * @param userId
	 * @param dueDateStr
	 */
	public void checkoutBook(int bookId, int userId, String dueDateStr) {
		Books book = booksRepo.getReferenceById((long) bookId);
		User user = userRepo.getReferenceById((long) userId);
		Date checkoutDate = new Date();
		Date dueDate = ServiceUtility.parseDate(dueDateStr);

		History history = new History();
		history.setBooks(book);
		history.setUser(user);
		history.setCheckoutDate(checkoutDate);
		history.setDueDate(dueDate);
		history.setReturned("F");
		historyRepo.save(history);
		
		this.decrementStock(bookId);	
	}
}
