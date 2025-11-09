#!/bin/bash
# ======================================================================
# Env setting script
# ======================================================================

BASE_DIR="$(cd "$(dirname "$0")" && pwd -P)"

COMMON_DIR=${BASE_DIR}/common

JDK_32_HOME=${COMMON_DIR}/jdk/linux32
JDK_64_HOME=${COMMON_DIR}/jdk/linux64
ANT_HOME=${COMMON_DIR}/ant

unamestr=`uname`
if [[ "$unamestr" == 'Linux' ]]; then
  if [ -d "$JDK_32_HOME" ]; then
        echo "${JDK_32_HOME}"
        export JAVA_HOME=${JDK_32_HOME}
  fi

  if [ -d "$JDK_64_HOME" ]; then
        echo "${JDK_64_HOME}"
        export JAVA_HOME=${JDK_64_HOME}
  fi
elif [[ "$unamestr" == 'Darwin' ]]; then
  echo "你正在使用MAC OS, 请自行准备版本7以上的JDK, 如果已经安装请忽略本信息。"
fi

export ANT_HOME

export LD_LIBRARY_PATH=${COMMON_DIR}/native-libs

export ANT_OPTS="-Djava.library.path=${COMMON_DIR}/native-libs -Xms256m -Xmx1024m"