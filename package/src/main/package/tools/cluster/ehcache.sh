#!/bin/bash
# ======================================================================
# BPMT ehcache 设置工具
# ======================================================================

FONTCOLOR="\033[33m"                            # echo 时的 字体颜色
END="\033[0m"                                   # echo 时的 结束标志

CURRENT_DIR="$(cd "$(dirname "$0")" && pwd -P)"

RIVER_INSTALLATION_HOME=${CURRENT_DIR}/../..
RIVER_TOOLS_INTERNAL_DIR=${CURRENT_DIR}/../internal

cd ${RIVER_INSTALLATION_HOME}
. ${RIVER_INSTALLATION_HOME}/common/set_env.sh
cd ${CURRENT_DIR}

echo -ne 			 "*********************************************************\n"
echo -ne "${FONTCOLOR}********************** BPMT Tools ®© ********************${END}\n"
echo -ne "${FONTCOLOR}******************* BPMT ehcache 设置工具 ****************${END}\n"
echo -ne 			 "*********************************************************\n"

ANT=${ANT_HOME}/bin/ant

${ANT} -buildfile ${RIVER_TOOLS_INTERNAL_DIR}/ant/ehcache-cluster.xml -lib ${RIVER_TOOLS_INTERNAL_DIR}/libs -logger com.riversoft.dtask.BuildLogger -q

