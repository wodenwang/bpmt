#!/bin/bash
# ======================================================================
# BPMT Tools 自动升级工具
# ======================================================================

FONTCOLOR="\033[33m"                            # echo 时的 字体颜色
END="\033[0m"                                   # echo 时的 结束标志

CURRENT_DIR="$(cd "$(dirname "$0")" && pwd -P)"

. ${CURRENT_DIR}/common/set_env.sh

echo -ne 			 "*********************************************************\n"
echo -ne "${FONTCOLOR}****************** BPMT Tools Co.,Ltd ®© ****************${END}\n"
echo -ne "${FONTCOLOR}********************** 自动升级工具 ***********************${END}\n"
echo -ne 			 "*********************************************************\n"
echo

ANT=${ANT_HOME}/bin/ant

if [[ $# -ge 1 ]]; then
  echo -ne  "${FONTCOLOR}**********************  使用严格升级模式(非强制替换)  *********************${END}\n"
  ${ANT} -Dstrict=true -buildfile ${CURRENT_DIR}/tools/internal/ant/upgrade.xml -lib ${CURRENT_DIR}/tools/internal/libs -logger com.riversoft.dtask.BuildLogger -q
else
  echo -ne 	"${FONTCOLOR}**********************  使用宽松升级模式(强制替换)  *********************${END}\n"
  ${ANT} -Dstrict=false -buildfile ${CURRENT_DIR}/tools/internal/ant/upgrade.xml -lib ${CURRENT_DIR}/tools/internal/libs -logger com.riversoft.dtask.BuildLogger -q
fi