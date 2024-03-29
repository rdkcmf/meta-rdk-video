DESCRIPTION = "Control Manager component"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SECTION = "base"
DEPENDS = "sqlite3 curl rdkversion jansson glib-2.0 systemd iarmbus iarmmgrs breakpad util-linux devicesettings nopoll rfc libarchive safec-common-wrapper gperf-native"

DEPENDS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'ctrlm_voice_sdk', ' xr-voice-sdk xr-timestamp', '', d)}"
DEPENDS_append = "${@bb.utils.contains('DISTRO_FEATURES_RDK', 'comcast-gperftools-heapcheck-wp', ' xr-fdc', '', d)}"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"
RDEPENDS_${PN}_append = " devicesettings"

PROVIDES = "ctrlm"
RPROVIDES_${PN} = "ctrlm"


inherit autotools pkgconfig pythonnative syslog-ng-config-gen
SYSLOG-NG_FILTER = "ctrlm"
SYSLOG-NG_SERVICE_ctrlm = "ctrlm-hal-rf4ce.service ctrlm-main.service"
SYSLOG-NG_DESTINATION_ctrlm = "ctrlm_log.txt"
SYSLOG-NG_LOGRATE_ctrlm = "medium"

PV = "${RDK_RELEASE}"
SRC_URI        = "${CMF_GIT_ROOT}/rdk/components/generic/control;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=ctrlm-main"
SRC_URI_append = " file://ctrlm-main.service"
SRC_URI_append = " file://1_rf4ce.conf"
SRC_URI_append = " file://ctrlm-hal-rf4ce.service"
SRC_URI_append = " file://restart_ctrlm.sh"
SRC_URI_append = " file://ctrlm_db_dump.sh"
SRC_URI_append = " file://ctrlm_sig_quit.sh"

RECIPEDIR := "${THISDIR}/files"

SRCREV_ctrlm-main = "${AUTOREV}"
SRCREV_FORMAT = "ctrlm-main"

S = "${WORKDIR}/git"

FILES_${PN} += "${systemd_unitdir}/system/ctrlm-main.service \
                /usr/local/bin/restart_ctrlm.sh \
                /usr/local/bin/ctrlm_db_dump.sh \
                /usr/local/bin/ctrlm_sig_quit.sh \
               "
FILES_${PN} += "${@bb.utils.contains('EXTRA_OECONF', '--enable-rf4ce', '${systemd_unitdir}/system/ctrlm-main.service.d/1_rf4ce.conf', '', d)}"

SYSTEMD_PACKAGES += " ctrlm-main"
SYSTEMD_SERVICE_ctrlm-main  = "ctrlm-main.service"
SYSTEMD_SERVICE_ctrlm-main += "${@bb.utils.contains('DISTRO_FEATURES', 'ctrlm_generic', 'ctrlm-hal-rf4ce.service', '', d)}"

ENABLE_GPERFTOOLS_HEAPCHECK_WP_DISTRO = "1"

GPERFTOOLS_HEAPCHECK_CLASS := "${@bb.utils.contains('BBLAYERS', '${RDKROOT}/meta-rdk-comcast', 'comcast-gperftools-heapcheck-wp', '', d)}"
VSDK_UTILS_CLASS           := "${@bb.utils.contains('DISTRO_FEATURES', 'ctrlm_voice_sdk', 'vsdk-utils', '', d)}"

inherit systemd coverity ${VSDK_UTILS_CLASS} ${GPERFTOOLS_HEAPCHECK_CLASS}

BREAKPAD_BIN = "controlMgr"
BREAKPAD_SHARED_LIBS ="libstdc++.so libffi.so libglib-2.0.so ld-2.22.so libc-2.22.so libpthread-2.22.so libgobject-2.0.so libcurl.so librdkversion.so librdkx-logger.so libbsd.so libxr-timestamp.so libxr-timer.so libxr_mq.so libxraudio-eos.so libxraudio-adpcm.so libxraudio.so libxraudio-hal.so libxrsv.so libxrsr.so libcrypto.so libssl.so libnopoll.so libjansson.so libxr-voice-sdk.so"

INCLUDE_DIRS = " \
    -I=${includedir}/glib-2.0 \
    -I=${libdir}/glib-2.0/include \
    -I=${includedir}/gio-unix-2.0 \
    -I=${includedir}/rdk/iarmbus \
    -I=${includedir}/rdk/iarmmgrs-hal \
    -I=${includedir}/wdmp-c \
    -I=${includedir}/rdk/ds \
    -I=${includedir}/rdk/ds-rpc \
    -I=${includedir}/rdk/ds-hal \
    -I=${includedir}/nopoll \
    "

CFLAGS_append = " -std=c11 -fPIC -D_REENTRANT -rdynamic -Wall -Werror ${INCLUDE_DIRS}"

CXXFLAGS_append = " -std=c++11 -fPIC -D_REENTRANT -rdynamic -Wall -Werror ${INCLUDE_DIRS}"

CXXFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES_RDK', 'comcast-gperftools-heapcheck-wp', ' -DFDC_ENABLED', '', d)}"

CXXFLAGS_append = " -DSYSTEMD_NOTIFY"
CXXFLAGS_append = " -DXR15_704"

CXXFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"
CXXFLAGS_append_client = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"

CXXFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"

#CXXFLAGS_append = " -DCTRLM_ASSERT_ON_WRONG_THREAD"
#CXXFLAGS_append = " -DMEM_DEBUG"
#CXXFLAGS_append = " -DANSI_CODES_DISABLED"

LDFLAGS=" -lrt -lpthread -lstdc++ -lsqlite3 -lcurl -lgio-2.0 -lgobject-2.0 -lglib-2.0 -lz -lpthread -ljansson -ldbus-1 -luuid -lIARMBus -lds -lnopoll -lssl -lcrypto -lsystemd -ldshalcli -lrfcapi -lrdkversion"

# Default to STB Platform
PLATFORM ?= "STB"
EXTRA_OECONF_append = "${@bb.utils.contains('PLATFORM', 'STB', ' --enable-platform=stb', '', d)}"
EXTRA_OECONF_append = "${@bb.utils.contains('PLATFORM', 'TV', ' --enable-platform=tv', '', d)}"

# Default to HDMI / CEC discovery for IRDB
# Options: NONE / HDMI / CEC / ALL
IRDB_DISCOVERY_DEFAULT = "ALL"
IRDB_DISCOVERY ?= "${IRDB_DISCOVERY_DEFAULT}"
CXXFLAGS_append = " ${@bb.utils.contains('IRDB_DISCOVERY', 'ALL', ' -DIRDB_HDMI_DISCOVERY -DIRDB_CEC_DISCOVERY', '', d)}"
CXXFLAGS_append = " ${@bb.utils.contains('IRDB_DISCOVERY', 'HDMI', ' -DIRDB_HDMI_DISCOVERY', '', d)}"
CXXFLAGS_append = " ${@bb.utils.contains('IRDB_DISCOVERY', 'CEC', ' -DIRDB_CEC_DISCOVERY', '', d)}"

# Thunder Dependency
THUNDER            ?= "false"
CXXFLAGS_append     = "${@bb.utils.contains('THUNDER', 'true', ' -I=${includedir}/WPEFramework/ -I=${includedir}/WPEFramework/core/', '', d)}"
LDFLAGS_append      = "${@bb.utils.contains('THUNDER', 'true', ' -lWPEFrameworkCore -lWPEFrameworkProtocols -lWPEFrameworkPlugins -lWPEFrameworkTracing', '', d)}"
DEPENDS_append      = "${@bb.utils.contains('THUNDER', 'true', ' wpeframework', '', d)}"
EXTRA_OECONF_append = "${@bb.utils.contains('THUNDER', 'true', ' --enable-thunder', '', d)}"

THUNDER_SECURITY   ?= "true"
EXTRA_OECONF_append = "${@bb.utils.contains('THUNDER_SECURITY', 'true', ' --enable-thunder-security', '', d)}"

# Memory Lock Implementation
MEMORY_LOCK        ?= "false"
CXXFLAGS_append     = "${@bb.utils.contains('MEMORY_LOCK', 'true', ' -DMEMORY_LOCK', '', d)}"
LDFLAGS_append      = "${@bb.utils.contains('MEMORY_LOCK', 'true', ' -lclnl', '', d)}"
DEPENDS_append      = "${@bb.utils.contains('MEMORY_LOCK', 'true', ' clnl', '', d)}"

LDFLAGS_append =" -lxr-voice-sdk -lxrsr -lxrsv -lxraudio-hal -lrdkx-logger -lxr-timestamp"
LDFLAGS_append ="${@bb.utils.contains('DISTRO_FEATURES_RDK', 'comcast-gperftools-heapcheck-wp', ' -lxr-fdc', '', d)}"
LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"

# Telemetry Support
TELEMETRY_SUPPORT  ?= "false"
DEPENDS_append      = "${@bb.utils.contains('TELEMETRY_SUPPORT', 'true', ' telemetry', '', d)}"
LDFLAGS_append      = "${@bb.utils.contains('TELEMETRY_SUPPORT', 'true', ' -ltelemetry_msgsender', '', d)}"
EXTRA_OECONF_append = "${@bb.utils.contains('TELEMETRY_SUPPORT', 'true', ' --enable-telemetry-support', '', d)}"

CTRLM_GENERIC = "${@bb.utils.contains('DISTRO_FEATURES', 'ctrlm_generic', 'true', 'false', d)}"

XLOG_MODULE_NAME="CTRLM"
LOGGER:="${@bb.utils.contains('DISTRO_FEATURES', 'ctrlm_voice_sdk', 'rdkx-logger', '', d)}"
inherit ${LOGGER}

# if/when ctrlm only uses vsdk, remove the duplicated script files in the repository and always use VSDK_UTILS
VSDK_UTILS_JSON_TO_HEADER ?= ""
VSDK_UTILS_JSON_COMBINE   ?= ""

SUPPORT_VOICE_DEST_ALSA   ?= "false"

export CTRLM_UTILS_JSON_TO_HEADER  = "${@bb.utils.contains('DISTRO_FEATURES', 'ctrlm_voice_sdk', '${VSDK_UTILS_JSON_TO_HEADER}', '${S}/scripts/json_to_header.py', d)}"
export CTRLM_UTILS_JSON_COMBINE    = "${@bb.utils.contains('DISTRO_FEATURES', 'ctrlm_voice_sdk', '${VSDK_UTILS_JSON_COMBINE}', '${S}/scripts/json_combine.py', d)}"

export STAGING_BINDIR_NATIVE

CTRLM_CONFIG_VSDK    = "${PKG_CONFIG_SYSROOT_DIR}/usr/include/xrsr_config.json"
CTRLM_CONFIG_OEM_ADD = "${S}/../ctrlm_config_oem.add.json"
CTRLM_CONFIG_OEM_SUB = "${S}/../ctrlm_config_oem.sub.json"
CTRLM_CONFIG_CPC_ADD = "${S}/../ctrlm_config_cpc.add.json"
CTRLM_CONFIG_CPC_SUB = "${S}/../ctrlm_config_cpc.sub.json"

EXTRA_OECONF_append = " CTRLM_CONFIG_JSON_VSDK=${CTRLM_CONFIG_VSDK} CTRLM_CONFIG_JSON_OEM_SUB=${CTRLM_CONFIG_OEM_SUB} CTRLM_CONFIG_JSON_OEM_ADD=${CTRLM_CONFIG_OEM_ADD} CTRLM_CONFIG_JSON_CPC_SUB=${CTRLM_CONFIG_CPC_SUB} CTRLM_CONFIG_JSON_CPC_ADD=${CTRLM_CONFIG_CPC_ADD}"
EXTRA_OECONF_append = "${@ ' --enable-xrsr_sdt' if (d.getVar('SUPPORT_VOICE_DEST_ALSA', expand=False) == "true") else ''}"

DEPENDS_append   = "${@ ' virtual-mic' if (d.getVar('SUPPORT_VOICE_DEST_ALSA',   expand=False) == "true") else ''}"

EXTRA_OECONF_append = " GIT_BRANCH=${CMF_GIT_BRANCH}"

EXTRA_OECONF_append = "${@bb.utils.contains('DISTRO_FEATURES', 'ctrlm_mic_tap', ' --enable-mic_tap', '', d)}"

do_install_append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${RECIPEDIR}/ctrlm-main.service ${D}${systemd_unitdir}/system/

    if ${@bb.utils.contains('EXTRA_OECONF', '--enable-rf4ce', 'true', 'false', d)}; then
       install -d ${D}${systemd_unitdir}/system/ctrlm-main.service.d/
       install -m 0644 ${RECIPEDIR}/1_rf4ce.conf ${D}${systemd_unitdir}/system/ctrlm-main.service.d/
    fi

    if [ "${CTRLM_GENERIC}" = "true" ]; then
       install -m 0644 ${RECIPEDIR}/ctrlm-hal-rf4ce.service ${D}${systemd_unitdir}/system/
    fi

    install -d ${D}/usr/local/bin
    install -m 0744 ${RECIPEDIR}/restart_ctrlm.sh ${D}/usr/local/bin
    install -m 0744 ${RECIPEDIR}/ctrlm_db_dump.sh ${D}/usr/local/bin
    install -m 0744 ${RECIPEDIR}/ctrlm_sig_quit.sh ${D}/usr/local/bin
}

addtask clean_oem_config before do_configure

do_clean_oem_config() {
    rm -f ${CTRLM_CONFIG_CPC_ADD} ${CTRLM_CONFIG_CPC_SUB} ${CTRLM_CONFIG_OEM_ADD} ${CTRLM_CONFIG_OEM_SUB}
}
