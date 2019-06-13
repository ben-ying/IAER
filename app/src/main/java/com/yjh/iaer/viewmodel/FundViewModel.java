package com.yjh.iaer.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.yjh.iaer.network.Resource;
import com.yjh.iaer.repository.FundRepository;
import com.yjh.iaer.room.entity.Fund;

import java.util.List;

import javax.inject.Inject;

public class FundViewModel extends ViewModel {
    private final FundRepository mRepository;

    @Inject
    public FundViewModel(final FundRepository repository) {
        this.mRepository = repository;
    }

    public LiveData<Resource<List<Fund>>> loadAllFunds() {
        return mRepository.loadAllFunds();
    }
}
