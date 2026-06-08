package com.yjh.iaer.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.yjh.iaer.model.StatisticsDate;
import com.yjh.iaer.network.Resource;
import com.yjh.iaer.repository.CategoryRepository;
import com.yjh.iaer.room.entity.Category;
import com.yjh.iaer.room.entity.Transaction;

import java.util.List;

import javax.inject.Inject;

public class CategoryViewModel extends ViewModel {
    private final CategoryRepository mRepository;
    private final MutableLiveData<StatisticsParams> mStatisticsParams = new MutableLiveData<>();
    private final MutableLiveData<StatisticsTypeParams> mStatisticsTypeParams = new MutableLiveData<>();
    private final MutableLiveData<TopListParams> mTopListParams = new MutableLiveData<>();
    private final LiveData<Resource<List<Category>>> mStatisticsCategories;
    private final LiveData<Resource<List<StatisticsDate>>> mStatisticsDates;
    private final LiveData<Resource<List<Transaction>>> mTopList;

    @Inject
    public CategoryViewModel(final CategoryRepository repository) {
        this.mRepository = repository;
        mStatisticsCategories = Transformations.switchMap(mStatisticsParams, params -> {
            if (params == null) {
                return new MutableLiveData<>();
            }
            return mRepository.loadStatisticsCategories(params.token, params.year, params.month);
        });
        mStatisticsDates = Transformations.switchMap(mStatisticsTypeParams, params -> {
            if (params == null) {
                return new MutableLiveData<>();
            }
            return mRepository.loadDateCategories(params.token, params.type);
        });
        mTopList = Transformations.switchMap(mTopListParams, params -> {
            if (params == null) {
                return new MutableLiveData<>();
            }
            return mRepository.loadTopList(params.userId, params.years, params.months,
                    params.categories, params.minMoney);
        });
    }

    public LiveData<Resource<List<Category>>> loadAllCategories() {
        return mRepository.loadAllCategories();
    }

    public LiveData<Resource<List<Category>>> getStatisticsCategories() {
        return mStatisticsCategories;
    }

    public LiveData<Resource<List<StatisticsDate>>> getStatisticsDates() {
        return mStatisticsDates;
    }

    public LiveData<Resource<List<Transaction>>> getTopList() {
        return mTopList;
    }

    public void requestStatisticsCategories(String token, int year, int month) {
        mStatisticsParams.setValue(new StatisticsParams(token, year, month));
    }

    public void requestDateCategories(String token, int type) {
        mStatisticsTypeParams.setValue(new StatisticsTypeParams(token, type));
    }

    public void requestTopList(final int userId, final String years, final String months,
                               final String categories, final int minMoney) {
        mTopListParams.setValue(new TopListParams(userId, years, months, categories, minMoney));
    }

    private static final class StatisticsParams {
        final String token;
        final int year;
        final int month;

        StatisticsParams(String token, int year, int month) {
            this.token = token;
            this.year = year;
            this.month = month;
        }
    }

    private static final class StatisticsTypeParams {
        final String token;
        final int type;

        StatisticsTypeParams(String token, int type) {
            this.token = token;
            this.type = type;
        }
    }

    private static final class TopListParams {
        final int userId;
        final String years;
        final String months;
        final String categories;
        final int minMoney;

        TopListParams(int userId, String years, String months, String categories, int minMoney) {
            this.userId = userId;
            this.years = years;
            this.months = months;
            this.categories = categories;
            this.minMoney = minMoney;
        }
    }
}
