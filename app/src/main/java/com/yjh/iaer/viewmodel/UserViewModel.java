package com.yjh.iaer.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

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

    public LiveData<Resource<User>> login(String username, String password) {
        return mRepository.login(username, password);
    }

    public LiveData<Resource<User>> logout() {
        return mRepository.logout();
    }

    public LiveData<Resource<List<User>>> loadAllUsers() {
        return mRepository.loadAllUsers();
    }
}
