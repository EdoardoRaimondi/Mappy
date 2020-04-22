//
// Created by edoardo raimondi on 20/04/2020.
//

#include <jni.h>
#include <stdio.h>

extern "C" {

JNIEXPORT jint
JNICALL Java_com_example_app_SpinnerActivity_parseRadius(JNIEnv * env, jobject obj, jstring radius){
    const char *nativeString = env->GetStringUTFChars(radius, 0);
    char buffer[10]; //to contain the number
    char flag = 'k'; //the radius is in the form 20km
    int offset= 1000; //one km
    int i;
    for(i=0; nativeString[i]!=flag; i++) buffer[i] = nativeString[i];
    buffer[i] = 0;
    int numericRadius = atoi(buffer);
    return numericRadius * offset;
    }
}

