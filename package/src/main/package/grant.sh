#!/bin/bash
# ======================================================================
# BPMT Tools 授权工具
# ======================================================================

FONTCOLOR="\033[33m"                            # echo 时的 字体颜色
END="\033[0m"                                   # echo 时的 结束标志

CURRENT_DIR="$(cd "$(dirname "$0")" && pwd -P)"

. ${CURRENT_DIR}/common/set_env.sh

echo -ne             "*********************************************************\n"
echo -ne "${FONTCOLOR}****************** BPMT Tools Co.,Ltd ®© ****************${END}\n"
echo -ne "${FONTCOLOR}************************* 授权工具 ***********************${END}\n"
echo -ne             "*********************************************************\n"
echo

chmod +x ${CURRENT_DIR}/*.sh
chmod +x ${CURRENT_DIR}/**/**/bin/*
chmod +x ${CURRENT_DIR}/**/**/*.sh
chmod +x ${CURRENT_DIR}/**/**/**/bin/*
chmod +x ${CURRENT_DIR}/**/**/**/**/bin/*

echo -ne             "*********************************************************\n"
echo -ne "${FONTCOLOR}********************* BPMT Tools 授权完成 ****************${END}\n"
