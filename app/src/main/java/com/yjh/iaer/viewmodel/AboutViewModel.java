package com.yjh.iaer.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.yjh.iaer.network.Resource;
import com.yjh.iaer.repository.AboutRepository;
import com.yjh.iaer.room.entity.About;

import javax.inject.Inject;

public class AboutViewModel extends ViewModel {
    private final AboutRepository mRepository;

    @Inject
    public AboutViewModel(final AboutRepository repository) {
        this.mRepository = repository;
    }

    public LiveData<Resource<About>> loadAbout() {
        return mRepository.loadAbout();
    }
}
