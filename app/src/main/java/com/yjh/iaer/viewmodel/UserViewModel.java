package com.yjh.iaer.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.yjh.iaer.network.Resource;
import com.yjh.iaer.repository.UserRepository;
import com.yjh.iaer.room.entity.User;

import java.util.List;

import javax.inject.Inject;

public class UserViewModel extends ViewModel {
    private final UserRepository mRepository;

    @Inject
    public UserViewModel(final UserRepository repository) {
        this.mRepository = repository;
    }

    public LiveData<Resource<User>> login(String username, String password, String token) {
        return mRepository.login(username, password, token);
    }

    public LiveData<Resource<User>> logout() {
        return mRepository.logout();
    }

    public LiveData<Resource<List<User>>> loadAllUsers() {
        return mRepository.loadAllUsers();
    }

    public LiveData<Resource<User>> register(
            String username, String password, String email) {
        return mRepository.register(username, password, email);
    }

    public LiveData<Resource<User>> sendVerifyCode(String email) {
        return mRepository.sendVerifyCode(email);
    }

    public void deleteUserHistory(int userId) {
        mRepository.deleteUserHistory(userId);
    }
}
