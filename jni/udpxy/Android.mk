LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := udpxy
LOCAL_SRC_FILES := udpxy.c sloop.c rparse.c util.c prbuf.c ifaddr.c ctx.c mkpg.c \
                   rtp.c uopt.c dpkt.c netop.c extrn.c main.c

include $(BUILD_EXECUTABLE)
