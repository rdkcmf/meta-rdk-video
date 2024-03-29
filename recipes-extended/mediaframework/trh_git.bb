SUMMARY = "This recipe compiles rmfcore code base. This has interface clasess that would be necessary for all the mediaplayers"
SECTION = "console/utils"

LICENSE = "Apache-2.0 & BSD-3-Clause & MIT"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=1a7c83d1696f84e35c7f8efcc4ca9d72"

PV = "${RDK_RELEASE}+git${SRCPV}"

SRC_URI = "${CMF_GIT_ROOT}/rdk/components/generic/mediaframework;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH}"

export RDK_FSROOT_PATH = "${STAGING_DIR_TARGET}"
export FSROOT = '='
SELECTED_OPTIMIZATION_append = " -O1"
S = "${WORKDIR}/git/trh"


DEPENDS = "trm-common rmfosal glib-2.0"


inherit autotools pkgconfig coverity
