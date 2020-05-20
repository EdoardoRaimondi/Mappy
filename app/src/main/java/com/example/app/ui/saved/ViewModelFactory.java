package com.example.app.ui.saved;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


class ViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;


    public ViewModelFactory(Application application) {
        mApplication = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SavedViewModel(mApplication);
    }
}