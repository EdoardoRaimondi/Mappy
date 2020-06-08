package com.example.app.listeners;

public interface PhoneNumberGetListener {

    /**
     * Called when found a phone number
     * @param phoneNumber String of found phone number
     */
    void onSuccess(String phoneNumber);

    /**
     * Called when no phone number has been found
     * (the json was empty, no place has a phone number...)
     */
    void onFail();
}
