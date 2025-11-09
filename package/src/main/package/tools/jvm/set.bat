@echo off
title BPMT JVM Settings
color 0a

set CURRENT_DIR=%~dp0
set RIVER_INSTALLATION_HOME=%CURRENT_DIR%\..\..
set RIVER_TOOLS_INTERNAL_DIR=%CURRENT_DIR%\..\internal

call %RIVER_INSTALLATION_HOME%\common\set_env.bat

echo *********************************************************
echo ***************** BPMT Tools Co.,Ltd ой  ****************
echo *****************   BPMT JVM Settings   *****************
echo *********************************************************
echo.

set ANT=%ANT_HOME%\bin\ant

call %ANT% -buildfile %RIVER_TOOLS_INTERNAL_DIR%\ant\jvm-set.xml -lib %RIVER_TOOLS_INTERNAL_DIR%\libs -logger com.riversoft.dtask.BuildLogger -q 

pause