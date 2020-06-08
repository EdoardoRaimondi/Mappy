package com.example.app.factories;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.app.ui.saved.SavedViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;

    public ViewModelFactory(Application application) {
        mApplication = application;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new SavedViewModel(mApplication);
    }
}