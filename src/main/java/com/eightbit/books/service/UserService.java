package com.eightbit.books.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

		return null;
	}

	/**
	 * ★特定ユーザ検索
	 * @param userId
	 * @return 特定ユーザデータ
	 */
	public User searchUserById(int userId) {

		return null;
	}

	/**
	 * ★特定のユーザデータを削除する 該当ユーザIDをもつHistoryテーブルのデータも全て削除する
	 * @param userId
	 */
	public void deleteUserAndHistoryData(int userId) {

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
	public void updateUser(UserUpdateQuery uuq) {

	}

	/**
	 * ★ユーザ情報新規登録
	 * @param user
	 * @param birth
	 */
	public void userRegist(User user, String birth) {

	}
}
