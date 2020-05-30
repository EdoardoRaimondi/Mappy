package com.example.app.listeners;

public interface OnPhoneNumberGetListener {

    /**
     * Called when found a phone number
     * @param phoneNumber found
     */
    void onSuccess(String phoneNumber);

    /**
     * Called when no phone number has been found
     * (the json was empty, no place has a phone number...)
     */
    void onFail();
}
