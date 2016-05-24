#include <jni.h>
#include <iostream>
#include <sstream>
#include <dlfcn.h>
#include <unistd.h>
#include <vector>
#include <stdlib.h>
#include <sys/inotify.h>
#include <android/log.h>
#include <time.h>
#include <signal.h>
#include <sys/param.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <string>

using namespace std;
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "native-activity", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "native-activity", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "native-activity", __VA_ARGS__))


#define DEBUG
static pthread_mutex_t m_mutex;
static pthread_mutex_t m_mutex2;


static bool RUN = true;
static int sdkInt = 15;
static const char *startServiceName = "am startservice  --user 0 ";
static const char *startServiceNameWithSDK = "am startservice ";
static const char *className = "com.miraclesnow.app/com.miraclesnow.app.GrayService";
static string dest1;
static string dest2;

extern "C" {

const char *function(const char *str1, const char *str2) {
    std::string src1(str1);
    std::string src2(str1);
    std::string dest = src1 + src2;
    return dest.c_str();
}
char *ret = NULL;
char *DoSysCmd(const char *cmdline) {

    char *buffer;
    size_t result;
    int lSize;
    FILE *pSCSI;
    ret = NULL;
    buffer = (char *) malloc(1024);
    pSCSI = popen(cmdline, "r");
    if (buffer == NULL) {
        return NULL;
    }
    if (pSCSI == NULL) {
        printf("\nNULL\n");
        return NULL;
    }
    result = fread(buffer, 1, 256, pSCSI);
    fclose(pSCSI);
    printf("%s", buffer);
    ret = (char *) malloc(result);
    memset(ret, 0x00, result);
    memcpy(ret, buffer, result);
    free(buffer);
    return ret;
}

void *thread_system(void *arg) {
    char *cmd;
    char *p;
    cmd = DoSysCmd("ps");
    if (cmd != NULL) {
        p = strstr(cmd, "com.miraclesnow.app");
        if (p == NULL) {
            int flag = 0;
            if (sdkInt == 15) {
                flag = system(dest2.c_str());
            } else {
                flag = system(dest1.c_str());
            }
        }
    }
}

void *thread_servcie(void *arg) {
    int flag = 0;
    pthread_t pt2;
    pthread_create(&pt2, NULL, thread_system, NULL);
}

void startService() {
    pthread_mutex_unlock(&m_mutex2);
    pthread_t pt1;
    pthread_create(&pt1, NULL, thread_servcie, NULL);
}

int init_daemon(void) {

    pid_t pid;
    int i;
    pid = fork();
    if (pid > 0) {          //第一步，结束父进程，使得子进程成为后台
        exit(0);
    } else if (pid < 0) {
        return -1;
    }
// 第二步建立一个新的进程组，在这个新的进程组中，子进程成为这个进程组的首进程，以使该进程脱离所用终端
    setsid();
//  再次新建一个子进程，退出父进程，保证该进程不是进程组长，同时让该进程无法再打开一个新的终端
    pid = fork();
    if (pid > 0) {
        exit(0);
    } else if (pid < 0) {
        return -1;
    }
    //第三步：关闭所有从父进程继承的不再需要的文件描述符
//    for (i = 0; i < NOFILE; close(i++));
    //第四步：改变工作目录，使得进程不与任何文件系统联系
    chdir("/");
    //第五步：将文件屏蔽字设置为0
    umask(0);
    //第六步：忽略SIGCHLD信号
    signal(SIGCHLD, SIG_IGN);
    return 0;
}

int main(int sdk, char *arg[]) /* 无参数形式 */
{

    LOGE("pid=%d", getpid());
    LOGE("arg1=%s", arg[1]);

    sdkInt = atoi(arg[1]);
    string tmp1(startServiceName);
    string tmp2(startServiceNameWithSDK);

    string tmp4(className);

    dest1 = tmp1 + tmp4;
    dest2 = tmp2 + tmp4;
    init_daemon();
    time_t now;
    while (1) {
        sleep(15);
        startService();
        time(&now);
        LOGE("Snow is running,  pid=%d   %s/t/n", getpid() , ctime(&now));
    }
}
}
