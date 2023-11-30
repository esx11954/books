package com.eightbit.books.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eightbit.books.entity.History;
import com.eightbit.books.entity.User;
import com.eightbit.books.model.UserUpdateQuery;
import com.eightbit.books.repository.HistoryRepository;
import com.eightbit.books.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private HistoryRepository historyRepo;

	public List<User> findAll(){
		return userRepo.findAll();
	}
	
	/**
	 * ★ユーザ名検索
	 * @param searchQuery
	 * @return 特定ユーザデータ(複数)
	 */
	public List<User> searchUser(String searchQuery) {
		List<User> userLastNameList = userRepo.findByLastNameContaining(searchQuery);
		List<User> userFirstNameList = userRepo.findByFirstNameContaining(searchQuery);
		List<User> userList = Stream.concat(userLastNameList.stream(), userFirstNameList.stream())
				.collect(Collectors.toList());
		return userList;
	}

	/**
	 * ★特定ユーザ検索
	 * @param userId
	 * @return 特定ユーザデータ
	 */
	public User searchUserById(int userId) {
		return userRepo.findByUserId(userId);
	}

	/**
	 * ★特定のユーザデータを削除する 該当ユーザIDをもつHistoryテーブルのデータも全て削除する
	 * @param userId
	 */
	public void deleteUserAndHistoryData(int userId) {
		List<History> historyList = historyRepo.findByUserUserId(userId);
		List<Integer> historyIdList = historyList.stream().map(b -> b.getId()).collect(Collectors.toList());
		historyIdList.stream().forEach(id -> historyRepo.deleteById(id.longValue()));
		userRepo.deleteById((long) userId);
	}

	/**
	 * 登録情報変更時パラメータ受け取り用
	 * @param user
	 * @return パラメータ格納モデル
	 */
	public UserUpdateQuery getUserDto(User user) {
		UserUpdateQuery uuq = new UserUpdateQuery();

		uuq.setUserId(user.getUserId());
		uuq.setLastName(user.getLastName());
		uuq.setFirstName(user.getFirstName());
		uuq.setTel(user.getTel());
		uuq.setMail(user.getMail());
		uuq.setAddress(user.getAddress());

		return uuq;
	}

	/**
	 * ★特定ユーザデータ情報更新
	 * @param uuq
	 */
	@Transactional
	public void updateUser(UserUpdateQuery uuq) {
		User user = userRepo.getReferenceById((long) uuq.getUserId());

		user.setLastName(uuq.getLastName());
		user.setFirstName(uuq.getFirstName());
		user.setTel(uuq.getTel());
		user.setMail(uuq.getMail());
		user.setAddress(uuq.getAddress());

		userRepo.save(user);
	}

	/**
	 * ★ユーザ情報新規登録
	 * @param user
	 * @param birth
	 * @throws ParseException 
	 */
	public void userRegist(User user, String birth) {
		user.setBirth(ServiceUtility.parseDate(birth));
		user.setUserRegistered(new Date());

		userRepo.save(user);
	}
}
