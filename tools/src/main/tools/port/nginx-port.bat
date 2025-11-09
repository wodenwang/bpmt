@echo off
title BPMT Tools 应用程序端口配置工具
color 0a

set CURRENT_DIR=%~dp0
set RIVER_INSTALLATION_HOME=%CURRENT_DIR%\..\..
set RIVER_TOOLS_INTERNAL_DIR=%CURRENT_DIR%\..\internal

call %RIVER_INSTALLATION_HOME%\common\set_env.bat

echo *********************************************************
echo ******************** BPMT Tools (C)(R) ******************
echo ******************* 应用程序端口配置工具  ******************
echo *********************************************************
echo.

set ANT=%ANT_HOME%\bin\ant

call %ANT% -buildfile %RIVER_TOOLS_INTERNAL_DIR%\ant\nginx-port.xml -lib %RIVER_TOOLS_INTERNAL_DIR%\libs -logger com.riversoft.dtask.BuildLogger -q 

pause