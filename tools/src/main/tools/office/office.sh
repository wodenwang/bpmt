#!/bin/bash
# ======================================================================
# BPMT Tools Office 服务管理工具
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
echo -ne "${FONTCOLOR}****************** BPMT Tools Co.,Ltd ®© ****************${END}\n"
echo -ne "${FONTCOLOR}******************* Office服务管理工具 ********************${END}\n"
echo -ne 			 "*********************************************************\n"
echo

ANT=${ANT_HOME}/bin/ant

usage() {
  echo
  echo -ne 	"使用方式: ./office.sh start|stop|status|restart\n"
  echo -ne 	"例如:\n"
  echo -ne  "./office.sh start 启动Office服务\n"
  echo -ne  "./office.sh stop 停止Office服务\n"
  echo -ne  "./office.sh status 查看服务状态\n"
  echo
  echo
}

if [[ $# -ge 1 ]]; then
  officecommand="$1"
  if [[ "$officecommand" = "start" || $officecommand = "stop" || $officecommand = "restart" || $officecommand = "status" ]]; then
	${ANT} -Doffice-command=$officecommand -buildfile ${RIVER_TOOLS_INTERNAL_DIR}/ant/office.xml -lib ${RIVER_TOOLS_INTERNAL_DIR}/libs -logger com.riversoft.dtask.BuildLogger -q
  else
    usage
	exit 0
  fi
else 
  usage
  exit 0
fi
