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

	public List<User> searchUser(String searchQuery) {
		List<User> userLastNameList = userRepo.findByLastNameContaining(searchQuery);
		List<User> userFirstNameList = userRepo.findByFirstNameContaining(searchQuery);
		List<User> userList = Stream.concat(userLastNameList.stream(), userFirstNameList.stream())
				.collect(Collectors.toList());
		return userList;
	}

	public User searchUserById(int userId) {
		User user = userRepo.findByUserId(userId);
		return user;
	}

	public void deleteUserAndHistoryData(int userId) {
		List<History> historyList = historyRepo.findByUserUserId(userId);
		List<Integer> historyIdList = historyList.stream().map(b -> b.getId()).collect(Collectors.toList());
		historyIdList.stream().forEach(id -> historyRepo.deleteById(id.longValue()));
		userRepo.deleteById((long) userId);
	}

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

	public void updateUser(UserUpdateQuery uuq) {
		User user = userRepo.getReferenceById((long) uuq.getUserId());

		user.setLastName(uuq.getLastName());
		user.setFirstName(uuq.getFirstName());
		user.setTel(uuq.getTel());
		user.setMail(uuq.getMail());
		user.setAddress(uuq.getAddress());

		userRepo.save(user);
	}

	public void userRegist(User user, String birth) {
		user.setBirth(ServiceUtility.parseDate(birth));
		user.setUserRegistered(new Date());

		userRepo.save(user);
	}
}
