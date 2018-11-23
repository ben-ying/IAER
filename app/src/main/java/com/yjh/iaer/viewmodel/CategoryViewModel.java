package com.yjh.iaer.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

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
}