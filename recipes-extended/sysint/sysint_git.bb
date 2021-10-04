SUMMARY = "Sysint application"
SECTION = "console/utils"

LICENSE = "Apache-2.0 & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=f36198fb804ffbe39b5b2c336ceef9f8"

PV = "${RDK_RELEASE}"

SYSINT_DEVICE ??= "intel-x86-pc/rdk-ri"

SRC_URI = "${RDK_GENERIC_ROOT_GIT}/sysint/generic;module=.;protocol=${RDK_GIT_PROTOCOL};branch=${RDK_GIT_BRANCH};name=sysintgeneric"
SRC_URI += "${RDK_GENERIC_ROOT_GIT}/sysint/devices/${SYSINT_DEVICE};module=.;protocol=${RDK_GIT_PROTOCOL};branch=${RDK_GIT_BRANCH};destsuffix=git/device;name=sysintdevice"
SRCREV_sysintgeneric = "${AUTOREV}"
SRCREV_sysintdevice = "${AUTOREV}"
SRCREV_FORMAT = "sysintgeneric_sysintdevice"

S = "${WORKDIR}/git/device"
inherit systemd
do_compile[noexec] = "1"

DEPENDS += "crashupload"
RDEPENDS_${PN} += "bash"
RDEPENDS_${PN} += "busybox"

RF3CE_CTRLM_ENABLED = "${@bb.utils.contains('DISTRO_FEATURES', 'ctrlm', 'true', 'false', d)}"
STG_TYPE = "${@bb.utils.contains('DISTRO_FEATURES', 'storage_sdc','SDCARD', 'OTHERS',d)}"
MMC_TYPE = "${@bb.utils.contains('DISTRO_FEATURES', 'storage_emmc','EMMC', '',d)}"
BIND_ENABLED = "${@bb.utils.contains('DISTRO_FEATURES', 'bind', 'true', 'false', d)}"
FORCE_MTLS = "${@bb.utils.contains('DISTRO_FEATURES', 'mtls_only', 'true', 'false', d)}"
ENABLE_MAINTENANCE="${@bb.utils.contains('DISTRO_FEATURES', 'enable_maintenance_manager', 'true', 'false', d)}"
WIFI_ENABLED="${@bb.utils.contains('DISTRO_FEATURES', 'wifi', 'true', 'false', d)}"

DUNFELL_BUILD = "${@bb.utils.contains('DISTRO_FEATURES', 'dunfell', 'true', 'false', d)}"
do_install() {
	install -d ${D}${base_libdir}/rdk

	install -m 0755 ${S}/../lib/rdk/* ${D}${base_libdir}/rdk

	install -d ${D}${sysconfdir}
	install -m 0644 ${S}/../etc/*.json ${D}${sysconfdir}
	install -m 0644 ${S}/../etc/*.properties ${D}${sysconfdir}
	install -m 0644 ${S}/../etc/*.conf ${D}${sysconfdir}
	install -m 0755 ${S}/../lib/rdk/zcip.script ${D}${sysconfdir}

	install -d ${D}/var/spool/cron

	install -d ${D}${bindir} ${D}${base_bindir}
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/hosts.service ${D}${systemd_unitdir}/system/hosts.service
	install -d ${D}${systemd_unitdir}/system ${D}${sysconfdir}
        if [ "${ENABLE_MAINTENANCE}" = "false" ]; then
	       install -m 0644 ${S}/../systemd_units/dcm-log.service ${D}${systemd_unitdir}/system
               install -m 0644 ${S}/../systemd_units/rfc-config.service ${D}${systemd_unitdir}/system
	       install -m 0644 ${S}/../systemd_units/swupdate.service ${D}${systemd_unitdir}/system
        fi
	install -m 0644 ${S}/../systemd_units/log-rdk-start.service ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/../systemd_units/previous-log-backup.service ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/../systemd_units/dump-log.service ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/../systemd_units/dump-log.timer ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/vitalprocess-info.timer ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/vitalprocess-info.service ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/../systemd_units/logrotate.service ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/../systemd_units/logrotate.timer ${D}${systemd_unitdir}/system
	install -m 0755 ${S}/../systemd_units/update_hosts.sh ${D}${bindir}/update_hosts.sh
	install -m 0644 ${S}/../systemd_units/scheduled-reboot.service ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/../systemd_units/dump-backup.service ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/../systemd_units/disk-check.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/mocastatuslogger.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/mocastatuslogger.timer ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/coredump-upload.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/coredump-secure-upload.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/coredump-upload.path ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/coredump-secure-upload.path ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/minidump-upload.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/minidump-secure-upload.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/minidump-upload.path ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/minidump-secure-upload.path ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/dropbear.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/disk-threshold-check.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/disk-threshold-check.timer ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/reboot-reason-logger.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/reboot-counter.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/reboot-counter.timer ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/reboot-notifier@.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/card-provision-check.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/iptables.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/hddstatuslogger.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/hddstatuslogger.timer ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/update-device-details.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/mount-failure-count.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/update-reboot-info.path ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/update-reboot-info.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/update-rf4ce-details.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/ping-telemetry.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/ping-telemetry.timer ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/gstreamer-cleanup.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/oops-dump.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/path-fail-notifier@.service ${D}${systemd_unitdir}/system
        if [ "${RF4CE_CTRLM_ENABLED}" = "false" ]; then
           sed -i "/^After=/ s/$/ rf4cemgr.service/" ${D}${systemd_unitdir}/system/update-rf4ce-details.service
        else
           sed -i "/^After=/ s/$/ ctrlm-main.service/" ${D}${systemd_unitdir}/system/update-rf4ce-details.service
        fi

	install -m 0755 ${S}/../etc/init.d/dump-backup-service ${D}${sysconfdir}
	install -m 0755 ${S}/../build/config/* ${D}${sysconfdir}
	if [ -d ${S}/devspec/etc ]; then
		install -m 0644 ${S}/devspec/etc/*.properties ${D}${sysconfdir}
		if [ -f ${S}/devspec/etc/warehouseHosts.conf ]; then
			install -m 0644 ${S}/devspec/etc/warehouseHosts.conf ${D}${sysconfdir}
		fi
	fi

	if [ -d ${S}/devspec/lib/rdk ]; then
		install -m 0755 ${S}/devspec/lib/rdk/* ${D}${base_libdir}/rdk
	fi

	if [ -d ${S}/etc ]; then
		if [ -f ${S}/etc/videoPrefs.json ]; then
			install -m 0755 ${S}/etc/videoPrefs.json ${D}${sysconfdir}
		fi
		if [ -f ${S}/etc/socks.conf ]; then
		install -m 0755 ${S}/etc/socks.conf ${D}${sysconfdir}
		fi
		if [ -f ${S}/etc/env_setup.sh ]; then
			install -m 0755 ${S}/etc/env_setup.sh ${D}${sysconfdir}
		fi
		if [ -f ${S}/etc/device.properties.wifi ]; then
			install -m 0755 ${S}/etc/device.properties.wifi ${D}${sysconfdir}
		fi
                if [ -f ${S}/etc/warehouseHosts.conf ]; then
                        install -m 0644 ${S}/etc/warehouseHosts.conf ${D}${sysconfdir}
                fi
		install -m 0755 ${S}/etc/*.properties ${D}${sysconfdir}
	fi

        install -d ${D}${sysconfdir}/rfcdefaults
        install -m 0755 ${S}/../etc/rfcdefaults/sysint.ini ${D}${sysconfdir}/rfcdefaults

        if [ "${BIND_ENABLED}" = "true" ]; then
           echo "BIND_ENABLED=true" >> ${D}${sysconfdir}/device.properties
        fi

        if [ "${MMC_TYPE}" = "EMMC" ]; then
           echo "SD_CARD_TYPE=EMMC" >> ${D}${sysconfdir}/device.properties
           install -m 0644 ${S}/../systemd_units/system-health-check.service ${D}${systemd_unitdir}/system
           install -m 0644 ${S}/../systemd_units/system-health-check.timer ${D}${systemd_unitdir}/system
           install -m 0644 ${S}/../systemd_units/check_wpa_supplicant_conf.service ${D}${systemd_unitdir}/system
        else
           # we dont want to install this script for all platforms yet
           rm -rf ${D}${base_libdir}/rdk/system-health-check.sh
           rm -rf ${D}${base_libdir}/rdk/recover_wpa_supplicant_conf.sh
        fi

        if [ "${FORCE_MTLS}" = "true" ]; then
           echo "FORCE_MTLS=true" >> ${D}${sysconfdir}/device.properties
        fi

	if [ -d ${S}/lib/rdk/install ]; then
		rm -rf ${S}/lib/rdk/install
	fi
	if [ -d ${S}/lib/rdk ]; then
		install -m 0755 ${S}/lib/rdk/* ${D}${base_libdir}/rdk
	fi
	# uploadDumps.sh has to be taken from a different source
	# lighttpd_utility.sh is not required
	rm -rf ${D}${base_libdir}/rdk/uploadDumps.sh
	rm -rf ${D}${base_libdir}/rdk/lighttpd_utility.sh
        rm -rf ${D}${base_libdir}/rdk/runPod.sh
        rm -rf ${D}${base_libdir}/rdk/runSnmp.sh
        rm -rf ${D}${base_libdir}/rdk/runRMFStreamer
        rm -rf ${D}${base_libdir}/rdk/runVodClientApp
	#
        # removing unused files for comcast component
        rm -rf ${D}${base_libdir}/rdk/adddefaultgateway.sh
        rm -rf ${D}${base_libdir}/rdk/checkIpdnl.sh
        rm -rf ${D}${base_libdir}/rdk/pNexus.sh
        rm -rf ${D}${base_libdir}/rdk/stackCalls.sh
        rm -rf ${D}${base_libdir}/rdk/watchdog-starter
        # zram is a machine feature and its script should be removed from
        # generic portion
        rm -f ${D}${base_libdir}/rdk/init-zram.sh
	#
	# The below scripts are installed by xre for emulator so need to
	# delete from sysint generic repo. For now, we will prevent these
	# to be installed by sysint.
	#
	if [ "${MACHINE}" = "qemux86hyb" -o "${MACHINE}" = "qemux86mc" ]; then
		rm ${D}${base_libdir}/rdk/isMocaNetworkUp.sh
		rm ${D}${base_libdir}/rdk/getDeviceDetails.sh
		rm ${D}${base_libdir}/rdk/getNumberOfStartupSteps.sh
		rm ${D}${base_libdir}/rdk/getProgress.sh
        fi
	ln -sf /lib/rdk/rebootSTB.sh ${D}/
	ln -sf /lib/rdk/rebootNow.sh ${D}/
	ln -sf /lib/rdk/timestamp ${D}${base_bindir}/timestamp

        # Samhain can only invoke external utilities present in trusted FHS path
        install -d ${D}${sbindir}
        if [ -f ${S}/../lib/rdk/upload2splunk.sh ]; then
            install -m 0755 ${S}/../lib/rdk/upload2splunk.sh ${D}${sbindir}/
        fi
	if [ -f ${D}/${base_libdir}/rdk/upload2splunk.sh ]; then
	    rm -f ${D}${base_libdir}/rdk/upload2splunk.sh
	fi
        rm -f ${D}${base_libdir}/rdk/isMocaNetworkUp.sh

	# FIXME: Not scalable sd card and non sd card (HDD) devices use different script
        # not perfect but this will have to do for now untill disk
        # checking is actually generic
        if [ "${STG_TYPE}" = "SDCARD" ]; then
            rm -f ${D}${systemd_unitdir}/system/disk-check.service
            install -m 0644 ${S}/../systemd_units/disk-check-sdcard.service ${D}${systemd_unitdir}/system/disk-check.service
        fi

        #Below scripts are required only if lxc is enabled
        rm -f ${D}${base_libdir}/rdk/lxcUsbEventTrigger.sh
        rm -f ${D}${base_libdir}/rdk/xre-dumpLogs.sh
        
        if [ "${MMC_TYPE}" != "EMMC" ]; then
            rm -f ${D}${base_libdir}/rdk/emmc_format.sh
        fi
        if [ "${ENABLE_MAINTENANCE}" = "true" ]; then
           echo "ENABLE_MAINTENANCE=true" >> ${D}${sysconfdir}/device.properties
        fi
}

do_install_append_hybrid() {
        sed -i -e 's/rmfstr_log.txt/ocapri_log.txt/g' ${D}/lib/rdk/dumpLogs.sh

	install -m 0644 ${S}/../systemd_units/rf-status-logger.service ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/../systemd_units/card-entitlement-logger.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/dibbler.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/dibbler.path ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/estb-interface-presetup.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/client-upgrade.path ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/client-upgrade.service ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/../etc/lighttpd_moca.conf ${D}${sysconfdir}
        install -m 0644 ${S}/../systemd_units/network-statistics.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/network-statistics.timer ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/discover-xi-client.path ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/discover-xi-client.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/udhcp.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/udhcp.path ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/power-state-monitor.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/lightsleep.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/lightsleep.path ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/htmldiag-pre.service ${D}${systemd_unitdir}/system

        if [ -f ${D}${base_libdir}/rdk/iptables_init_xi ]; then
            rm -f ${D}${base_libdir}/rdk/iptables_init_xi
        fi
        
        if [ -f ${D}${systemd_unitdir}/system/update-rf4ce-details.service ]; then
            sed -i "/^After=/ s/$/ syssnmpagent.service/" ${D}${systemd_unitdir}/system/update-rf4ce-details.service
        fi

        if [ -f ${S}/etc/xi-xconf-hosts.list ]; then
            install -m 0644 ${S}/etc/xi-xconf-hosts.list ${D}${sysconfdir}
        fi
	if [ -f ${S}/devspec/etc/xi-xconf-hosts.list ]; then
		install -m 0644 ${S}/devspec/etc/xi-xconf-hosts.list ${D}${sysconfdir}
	fi

        rm -f ${D}${base_libdir}/rdk/isMocaNetworkUp.sh
        rm -f ${D}${base_libdir}/rdk/firmwareDwnld.sh
        rm -f ${D}${base_libdir}/rdk/firmwareDwnld_Wifi.sh
        rm -f ${D}${base_libdir}/rdk/tr69FWDnld.sh

        install -D -m 0644 ${S}/../systemd_units/update-device-details.conf ${D}${systemd_unitdir}/system/update-device-details.service.d/update-device-details.conf
        if [ "${STG_TYPE}" != "SDCARD" ]; then
            rm -f ${D}${base_libdir}/rdk/prepare_sdcard
        fi

	sed -i -e 's/After=.*/After=rmfstreamer.service runpod.service runsnmp.service/g' ${D}${systemd_unitdir}/system/logrotate.service
	sed -i -e 's/Requires=.*/Requires=rmfstreamer.service runpod.service runsnmp.service/g' ${D}${systemd_unitdir}/system/logrotate.service
}

do_install_append_client() {
        install -m 0644 ${S}/../systemd_units/ip-setup-monitor.service ${D}${systemd_unitdir}/system
        install -d ${D}/HrvInitScripts
        ln -sf /lib/rdk/hrvInitCleanup.sh ${D}/HrvInitScripts/
        install -m 0644 ${S}/../systemd_units/dibbler_client.service ${D}${systemd_unitdir}/system/dibbler.service
	install -m 0644 ${S}/../systemd_units/virtual-moca-iface.service ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/../systemd_units/restart-timesyncd.service ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/../systemd_units/restart-timesyncd.path ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/../systemd_units/ntp-event.service ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/../systemd_units/ntp-event.path ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/ntp-time-checker.service ${D}${systemd_unitdir}/system
        if [ "${WIFI_ENABLED}" = "false" ] ; then
          install -m 0644 ${S}/../systemd_units/eth-connection-stats.service ${D}${systemd_unitdir}/system
          install -m 0644 ${S}/../systemd_units/eth-connection-stats.timer ${D}${systemd_unitdir}/system
        else
          install -m 0644 ${S}/../systemd_units/network-connection-stats.service ${D}${systemd_unitdir}/system
          install -m 0644 ${S}/../systemd_units/network-connection-stats.timer ${D}${systemd_unitdir}/system
        fi
        install -m 0644 ${S}/../systemd_units/rdnssd.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/dnsmerge-udhcpc.path ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/dnsmerge-upnp.path ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/dnsmerge.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/schedule_maintenance.service ${D}${systemd_unitdir}/system

        ## The systemd-timesyncd version 244 and above doesn't have access to /tmp directory as it is launched by 
        ## an unprivileged user(systemd-timesync). So trigger ntp-event when /run/systemd/timesync/synchronized flag is created instead of /tmp/clock-event.
        if [ "${DUNFELL_BUILD}" = "true" ]; then
            sed -i -e 's|.*PathExists=.*|PathExists=/run/systemd/timesync/synchronized|g' ${D}${systemd_unitdir}/system/ntp-event.path
        fi
	# override default disk check
        rm -f ${D}${systemd_unitdir}/system/disk-check.service
        install -m 0644 ${S}/../systemd_units/disk-check-sdcard.service ${D}${systemd_unitdir}/system/disk-check.service 
        if [ -f ${D}${systemd_unitdir}/system/update-rf4ce-details.service ]; then
            sed -i "/^After=/ s/$/ tr69agent.service/" ${D}${systemd_unitdir}/system/update-rf4ce-details.service
        fi

        #Remove moca telemetry script on client devices as the snmp is not supported 
        rm -f ${D}${base_libdir}/rdk/moca_telemetry.sh

        # XI clients use SLAAC configuration for IPv6 address
        rm -f ${D}${base_libdir}/prepare_dhcpv6_config.sh
        rm -f ${D}${base_libdir}/dibbler_start_pre.sh
        rm -f ${D}${base_libdir}/dibbler_start_post.sh
        rm -f ${D}${base_libdir}/getv6ip.sh

        #Remove it for client devices as dibbler is not bind to any specific ip
        rm -f ${D}${base_libdir}/estb_address_change.sh

        if [ -f ${D}${base_libdir}/rdk/iptables_init_xi ]; then
            mv ${D}${base_libdir}/rdk/iptables_init_xi ${D}${base_libdir}/rdk/iptables_init
        fi
        sed -i '/moca.service/d' ${D}${systemd_unitdir}/system/iptables.service

	install -m 0755 ${S}/../etc/init.d/tr69-agent ${D}${sysconfdir}

	# Remove script to get mocaIndex for syssnmpagent
        rm -f ${D}${base_libdir}/rdk/getMocaIndex.sh

        # donot run this script if device is wifi enabled.
        if [ "${WIFI_ENABLED}" = "true" ]; then
           sed -i "/\#\!\/bin\/sh/a exit 0" ${D}/lib/rdk/triggerDhcpLease.sh
        fi
}

do_install_append_mediaclient() {
	install -m 0644 ${S}/../systemd_units/ip-setup-monitor.service ${D}${systemd_unitdir}/system
}
do_install_append_container() {
        install -m 0755 ${S}/../lib/rdk/lxcUsbEventTrigger.sh ${D}${base_libdir}/rdk
        install -m 0755 ${S}/../lib/rdk/xre-dumpLogs.sh ${D}${base_libdir}/rdk

        install -m 0644 ${S}/../systemd_units/xre-dump-log.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/xre-dump-log.timer ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/xre-dump-log.path ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/lxc-usb-event-trigger.service ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/../systemd_units/lxc-usb-event-trigger.path ${D}${systemd_unitdir}/system
}

do_install_append_rdkzram() {
        install -m 0755 ${S}/../lib/rdk/init-zram.sh ${D}${base_libdir}/rdk
        install -m 0644 ${S}/../systemd_units/zram.service ${D}${systemd_unitdir}/system
}

SYSTEMD_SERVICE_${PN} = "hosts.service"
SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'enable_maintenance_manager', '', 'dcm-log.service',d)}"
SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'enable_maintenance_manager', '', 'rfc-config.service',d)}"
SYSTEMD_SERVICE_${PN} += "log-rdk-start.service"
SYSTEMD_SERVICE_${PN} += "previous-log-backup.service"
SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'enable_maintenance_manager', '', 'swupdate.service',d)}"
SYSTEMD_SERVICE_${PN} += "dump-log.timer"
SYSTEMD_SERVICE_${PN} += "vitalprocess-info.timer"
SYSTEMD_SERVICE_${PN} += "logrotate.timer"
SYSTEMD_SERVICE_${PN} += "scheduled-reboot.service"
SYSTEMD_SERVICE_${PN} += "dump-backup.service"
SYSTEMD_SERVICE_${PN} += "disk-check.service"
SYSTEMD_SERVICE_${PN} += "mocastatuslogger.service"
SYSTEMD_SERVICE_${PN} += "mocastatuslogger.timer"
SYSTEMD_SERVICE_${PN} += "coredump-upload.service"
SYSTEMD_SERVICE_${PN} += "coredump-secure-upload.service"
SYSTEMD_SERVICE_${PN} += "coredump-upload.path"
SYSTEMD_SERVICE_${PN} += "coredump-secure-upload.path"
SYSTEMD_SERVICE_${PN} += "minidump-upload.service"
SYSTEMD_SERVICE_${PN} += "minidump-secure-upload.service"
SYSTEMD_SERVICE_${PN} += "minidump-upload.path"
SYSTEMD_SERVICE_${PN} += "minidump-secure-upload.path"
SYSTEMD_SERVICE_${PN} += "dropbear.service"
SYSTEMD_SERVICE_${PN} += "disk-threshold-check.timer"
SYSTEMD_SERVICE_${PN} += "reboot-reason-logger.service"
SYSTEMD_SERVICE_${PN} += "reboot-counter.service"
SYSTEMD_SERVICE_${PN} += "reboot-counter.timer"
SYSTEMD_SERVICE_${PN} += "reboot-notifier@.service"
SYSTEMD_SERVICE_${PN} += "card-provision-check.service"
SYSTEMD_SERVICE_${PN} += "iptables.service"
SYSTEMD_SERVICE_${PN} += "hddstatuslogger.service"
SYSTEMD_SERVICE_${PN} += "hddstatuslogger.timer"
SYSTEMD_SERVICE_${PN} += "update-device-details.service"
SYSTEMD_SERVICE_${PN} += "mount-failure-count.service"
SYSTEMD_SERVICE_${PN} += "update-rf4ce-details.service"
SYSTEMD_SERVICE_${PN} += "ping-telemetry.service"
SYSTEMD_SERVICE_${PN} += "ping-telemetry.timer"
SYSTEMD_SERVICE_${PN} += "oops-dump.service"
SYSTEMD_SERVICE_${PN} += "update-reboot-info.path"
SYSTEMD_SERVICE_${PN} += "update-reboot-info.service"
SYSTEMD_SERVICE_${PN} += "gstreamer-cleanup.service"
SYSTEMD_SERVICE_${PN} += "path-fail-notifier@.service"
SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'storage_emmc', 'check_wpa_supplicant_conf.service', '',d)}"

SYSTEMD_SERVICE_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'storage_emmc', 'system-health-check.timer system-health-check.service', '',d)}"

SYSTEMD_SERVICE_${PN}_append_hybrid = " dibbler.service"
SYSTEMD_SERVICE_${PN}_append_hybrid = " dibbler.path"
SYSTEMD_SERVICE_${PN}_append_hybrid = " rf-status-logger.service"
SYSTEMD_SERVICE_${PN}_append_hybrid = " card-entitlement-logger.service"
SYSTEMD_SERVICE_${PN}_append_hybrid = " estb-interface-presetup.service"
SYSTEMD_SERVICE_${PN}_append_hybrid = " client-upgrade.service"
SYSTEMD_SERVICE_${PN}_append_hybrid = " client-upgrade.path"
SYSTEMD_SERVICE_${PN}_append_hybrid = " network-statistics.service"
SYSTEMD_SERVICE_${PN}_append_hybrid = " network-statistics.timer"
SYSTEMD_SERVICE_${PN}_append_hybrid = " discover-xi-client.service"
SYSTEMD_SERVICE_${PN}_append_hybrid = " discover-xi-client.path"
SYSTEMD_SERVICE_${PN}_append_hybrid = " udhcp.service"
SYSTEMD_SERVICE_${PN}_append_hybrid = " udhcp.path"
SYSTEMD_SERVICE_${PN}_append_hybrid = " power-state-monitor.service"
SYSTEMD_SERVICE_${PN}_append_hybrid = " lightsleep.service"
SYSTEMD_SERVICE_${PN}_append_hybrid = " lightsleep.path"
SYSTEMD_SERVICE_${PN}_append_client = " dibbler.service"
SYSTEMD_SERVICE_${PN}_append_client = " ip-setup-monitor.service"
SYSTEMD_SERVICE_${PN}_append_client = " virtual-moca-iface.service"
SYSTEMD_SERVICE_${PN}_append_client = " rdnssd.service"
SYSTEMD_SERVICE_${PN}_append_client = " restart-timesyncd.path"
SYSTEMD_SERVICE_${PN}_append_client = " ntp-event.service"
SYSTEMD_SERVICE_${PN}_append_client = " ntp-event.path"
SYSTEMD_SERVICE_${PN}_append_client = " ntp-time-checker.service"
SYSTEMD_SERVICE_${PN}_append_client = " ${@bb.utils.contains('DISTRO_FEATURES', 'wifi', 'network-connection-stats.service', 'eth-connection-stats.service', d)}"
SYSTEMD_SERVICE_${PN}_append_client = " ${@bb.utils.contains('DISTRO_FEATURES', 'wifi', 'network-connection-stats.timer', 'eth-connection-stats.timer', d)}"
SYSTEMD_SERVICE_${PN}_append_client = " dnsmerge-udhcpc.path"
SYSTEMD_SERVICE_${PN}_append_client = " dnsmerge-upnp.path"
SYSTEMD_SERVICE_${PN}_append_client = " dnsmerge.service"
SYSTEMD_SERVICE_${PN}_append_client = " schedule_maintenance.service"

SYSTEMD_SERVICE_${PN}_append_mediaclient = " ip-setup-monitor.service"

SYSTEMD_SERVICE_${PN}_append_container = " xre-dump-log.path"
SYSTEMD_SERVICE_${PN}_append_container = " lxc-usb-event-trigger.service"
SYSTEMD_SERVICE_${PN}_append_container = " lxc-usb-event-trigger.path"

SYSTEMD_SERVICE_${PN}_append_hybrid = " htmldiag-pre.service"

SYSTEMD_SERVICE_${PN}_append_rdkzram = " zram.service"

PACKAGE_BEFORE_PN += "${PN}-conf"

FILES_${PN} += "${bindir}/*"
FILES_${PN} += "${systemd_unitdir}/system/*"
FILES_${PN} += "${base_libdir}/rdk/*"
FILES_${PN} += "${sysconfdir}/rdk/*"
FILES_${PN} += "/rebootSTB.sh"
FILES_${PN} += "/rebootNow.sh"
FILES_${PN} += "${sysconfdir}/*"
FILES_${PN} += "${base_bindir}/timestamp"
FILES_${PN} += "${sbindir}/upload2splunk.sh"
FILES_${PN}_append_hybrid = " ${systemd_unitdir}/system/update-device-details.service.d/update-device-details.conf"
FILES_${PN}_append_client = " /HrvInitScripts/*"
FILES_${PN}-conf = "${sysconfdir}/rfcdefaults/sysint.ini"