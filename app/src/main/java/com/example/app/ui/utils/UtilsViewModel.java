package com.example.app.ui.utils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UtilsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public UtilsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is utils fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}