//
// Created by Edoardo Raimondi on 20/04/2020.
// Modified by Jacopo Pellizzari on 19/05/2020
//

#include <jni.h>
#include <stdio.h>

extern "C" {

JNIEXPORT jint
JNICALL Java_com_example_app_ui_utils_UtilsFragment_parseRadius(JNIEnv * env, jobject obj, jstring radius){
    // constants
    #define KM_SEPARATOR 'k'
    #define KM_TO_M 1000
    // local variables
    const char *nativeString = env->GetStringUTFChars(radius, 0);
    int decodedRadius = 0;
    char buffer[10]; // to contain the number
    int i;
    // script
    for(i=0; nativeString[i]!=KM_SEPARATOR; i++){
        buffer[i] = nativeString[i];
    }
    buffer[i] = 0;
    decodedRadius = atoi(buffer) * KM_TO_M;
    return decodedRadius;
    }
}

