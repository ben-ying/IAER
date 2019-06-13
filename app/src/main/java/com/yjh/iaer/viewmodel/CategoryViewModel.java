package com.yjh.iaer.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.yjh.iaer.model.StatisticsDate;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.repository.CategoryRepository;
import com.yjh.iaer.room.entity.Category;

import java.util.List;

import javax.inject.Inject;

public class CategoryViewModel extends ViewModel {
    private final CategoryRepository mRepository;

    @Inject
    public CategoryViewModel(final CategoryRepository repository) {
        this.mRepository = repository;
    }

    public LiveData<Resource<List<Category>>> loadAllCategories() {
        return mRepository.loadAllCategories();
    }

    public LiveData<Resource<List<Category>>> loadStatisticsCategories(String token, int year, int month) {
        return mRepository.loadStatisticsCategories(token, year, month);
    }

    public LiveData<Resource<List<StatisticsDate>>> loadDateCategories(String token, int type) {
        return mRepository.loadDateCategories(token, type);
    }
}
