#include <windows.h>
#include <jni.h>

#define JVM_LIBRARY ".\\jre7\\bin\\client\\jvm.dll"
#define USER_CLASSPATH "./build/classes;./lib/sqlite4java.jar;./lib/jcalendar-1.4.jar;./lib/mail.jar;./lib/RXTXcomm.jar;./lib/USBThermometerLib.jar;"
#define CLASS_NAME "Main/Main"

void setWorkingDirectory(void);

int main(int argc, char* argv[]) {
     JNIEnv *env;
     JavaVM *jvm;
     jint res;
     jclass cls;
     jmethodID mid;
     jstring jstr;
     jclass stringClass;
     jobjectArray args;

     typedef UINT (CALLBACK* LPFNDLLFUNC1)(JavaVM **pvm, void **penv, void *args);

     HINSTANCE hDLL;
     LPFNDLLFUNC1 JNI_CreateJavaVM;   
     
     printf("Set working directory...\n");
     setWorkingDirectory();

     printf("Load library...\n");
     hDLL = LoadLibrary(JVM_LIBRARY);

     // Create the Java VM //
     if(hDLL){
        JavaVMInitArgs vm_args;
        JavaVMOption options[1];
        options[0].optionString = "-Djava.class.path=" USER_CLASSPATH;
        vm_args.version = 0x00010006;
        vm_args.options = options;
        vm_args.nOptions = 1;
        vm_args.ignoreUnrecognized = JNI_TRUE;
             
        printf("GetProcAddress...\n");
        JNI_CreateJavaVM = (LPFNDLLFUNC1)GetProcAddress(hDLL,"JNI_CreateJavaVM");
        if(JNI_CreateJavaVM) {
            res = JNI_CreateJavaVM(&jvm, (void**)&env, &vm_args);
        } else {
            printf("Function address not found in DLL");
            FreeLibrary(hDLL);
            return false;
        }
     } else {
        printf("Load JNI library error\n");
        exit(1);
     }

     if (res < 0) {
         printf("Can't create Java VM\n");
         exit(1);
     }
     cls = env->FindClass(CLASS_NAME);
     if (cls == NULL) {
         printf("Can't find Main class\n");
         goto destroy;
     }

     mid = env->GetStaticMethodID( cls, "main", "([Ljava/lang/String;)V" );
     if (mid == NULL) {
         printf("Can't find main() method\n");
         goto destroy;
     }

     TCHAR exepath[MAX_PATH];      
     GetModuleFileName(0, exepath, MAX_PATH);

     jstr = env->NewStringUTF(exepath);
     if (jstr == NULL) {
         printf("Can't create new String\n");
         goto destroy;
     }
     stringClass = env->FindClass("java/lang/String");
     args = env->NewObjectArray(1, stringClass, jstr);
     if (args == NULL) {
         printf("Can't create new Object Array\n");
         goto destroy;
     }
     env->CallStaticVoidMethod(cls, mid, args);

 destroy:
     if (env->ExceptionOccurred()) {
         env->ExceptionDescribe();
     }
     jvm->DestroyJavaVM();

     if(hDLL) {
        FreeLibrary(hDLL);
     }
 }
 
 void setWorkingDirectory(void) {
     TCHAR exepath[MAX_PATH]; 
     
     GetModuleFileName(0, exepath, MAX_PATH);

     int sizeOfPath = 0;
     while( exepath[ sizeOfPath ] != 0 ) 
          sizeOfPath++;
     
     int i = sizeOfPath;
     while( ( exepath[i] != '\\' ) && (i >= 0) )
          exepath[i--] = 0;
          
     printf("Exe path = %s \n", exepath);          
         
     if(SetCurrentDirectory(exepath) == FALSE)   
        printf("Error setting current direcory\n"); 
}

