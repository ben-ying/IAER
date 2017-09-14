package com.yjh.iaer.injection;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.yjh.iaer.viewmodel.TransactionViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(TransactionViewModel.class)
    abstract ViewModel bindTransactionViewModel(TransactionViewModel transactionViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(TransactionViewModelFactory factory);
}
