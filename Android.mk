#
# This file is part of trust|me
# Copyright(c) 2013 - 2017 Fraunhofer AISEC
# Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
#
# This program is free software; you can redistribute it and/or modify it
# under the terms and conditions of the GNU General Public License,
# version 2 (GPL 2), as published by the Free Software Foundation.
#
# This program is distributed in the hope it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE. See the GPL 2 license for more details.
#
# You should have received a copy of the GNU General Public License along with
# this program; if not, see <http://www.gnu.org/licenses/>
#
# The full GNU General Public License is included in this distribution in
# the file called "COPYING".
#
# Contact Information:
# Fraunhofer AISEC <trustme@aisec.fraunhofer.de>
#

LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-java-files-under, src)

# use cml communication library
# LOCAL_STATIC_JAVA_LIBRARIES += cml-communication

# use protobuf library for service-specific communication with cmld
LOCAL_STATIC_JAVA_LIBRARIES := \
	trustme.cml.communication \
	trustme.cml.control-proto-java

LOCAL_RESOURCE_DIR = \
        $(LOCAL_PATH)/res

LOCAL_PACKAGE_NAME := TrustmeKeyguard

LOCAL_MODULE_TAG := optional

LOCAL_CERTIFICATE := platform
#LOCAL_PRIVILEGED_MODULE := true

LOCAL_OVERRIDES_PACKAGES := Launcher2
#LOCAL_PROGUARD_FLAG_FILES := proguard.flags

LOCAL_AAPT_FLAGS := \
	--auto-add-overlay	

#LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_PACKAGE)

# Build the test package
include $(call all-makefiles-under,$(LOCAL_PATH))
