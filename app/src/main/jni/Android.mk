LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := snowDameon

LOCAL_SRC_FILES :=  \
					MyJniTransport.cpp 
LOCAL_LDLIBS    := -llog

LOCAL_CPPFLAGS += -fexceptions

LOCAL_C_INCLUDES := $(LOCAL_PATH) \

include $(BUILD_EXECUTABLE)
LOCAL_SHARED_LIBRARIES := \
    libcutils \
    libdl \
    libstlport

LOCAL_PRELINK_MODULE := false

ifndef NDK_ROOT
include external/stlport/libstlport.mk
endif