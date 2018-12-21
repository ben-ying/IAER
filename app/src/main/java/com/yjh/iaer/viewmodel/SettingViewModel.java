package com.yjh.iaer.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.yjh.iaer.network.Resource;
import com.yjh.iaer.repository.SettingRepository;
import com.yjh.iaer.room.entity.Setting;

import javax.inject.Inject;

public class SettingViewModel extends ViewModel {
    private final SettingRepository mRepository;

    @Inject
    public SettingViewModel(final SettingRepository repository) {
        this.mRepository = repository;
    }

    public LiveData<Resource<Setting>> loadUserSetting(String token, int userId) {
        return mRepository.loadUserSetting(token, userId);
    }

    public LiveData<Resource<Setting>> updateUserSetting(String token, int userId, Setting setting) {
        return mRepository.updateUserSetting(token, userId, setting);
    }
}
