#!/bin/bash
# ======================================================================
# BPMT 激活工具
# ======================================================================

FONTCOLOR="\033[33m"                            # echo 时的 字体颜色
END="\033[0m"                                   # echo 时的 结束标志

CURRENT_DIR="$(cd "$(dirname "$0")" && pwd -P)"

. ${CURRENT_DIR}/common/set_env.sh

echo -ne 			 "*********************************************************\n"
echo -ne "${FONTCOLOR}****************** BPMT Tools Co.,Ltd ®© ****************${END}\n"
echo -ne "${FONTCOLOR}******************** BPMT Tools 激活工具 *****************${END}\n"
echo -ne 			 "*********************************************************\n"
echo

ANT=${ANT_HOME}/bin/ant

${ANT} -buildfile ${CURRENT_DIR}/tools/internal/ant/register.xml -lib ${CURRENT_DIR}/tools/internal/libs -logger com.riversoft.dtask.BuildLogger -q 
