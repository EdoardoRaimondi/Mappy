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

    JNIEXPORT jstring
    JNICALL Java_com_example_app_ui_saved_SavedFragment_capitalizeFirstChars(JNIEnv *env, jobject obj, jstring str){
        // local variables
        const char *nativeString = env->GetStringUTFChars(str, 0);
        unsigned int length = 0;
        for(length; nativeString[length]; length++){
        }
        char string[length];
        for(length; nativeString[length]; length++){
            string[length] = nativeString[length];
        }
        for(length; string[length]; length++){
            if(string[length] == ' ' && length > 0 && (string[length-1] >= 'a' && string[length-1] <= 'z')){
                string[length-1] = (string[length-1] - 'a') + 'A';
            }
        }
        return env->NewStringUTF(string);
    }
}

