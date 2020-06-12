#include <jni.h>
#include <stdio.h>

#define SPACE ' '

extern "C" {

    JNIEXPORT jstring
    JNICALL Java_com_example_app_ui_saved_SavedFragment_capitalizeFirstChars(JNIEnv *env, jobject obj, jstring str){
        // local variables
        const char *nativeString = env -> GetStringUTFChars(str, NULL);
        unsigned int length;
        unsigned int i;
        // getting string length
        for(length = 0; nativeString[length]; length++){
        }
        // copying str in string
        char string[length];
        for(i = 0; nativeString[i]; i++){
            string[i] = nativeString[i];
        }
        string[i] = '\0';
        // free memory space for str
        env -> ReleaseStringUTFChars(str, nativeString);
        // script
        if(length > 0 && string[0] >= 'a' && string[0] <= 'z'){
            string[0] = (string[0] - 'a') + 'A';
        }
        for(i = 1; string[i]; i++){
            if(string[i] == SPACE && i < length - 1 && (string[i+1] >= 'a' && string[i+1] <= 'z')){
                string[i+1] = (string[i+1] - 'a') + 'A';
            }
        }
        return env -> NewStringUTF(string);
    }

}
