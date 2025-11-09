@echo off

title BPMT Tools 激活工具
color 0a

set PWD=%~dp0

echo *********************************************************
echo ******************** BPMT Tools (C)(R) ******************
echo ************************* 激活工具 ***********************
echo *********************************************************
echo.

set INSTALLATION_DIR=%PWD%
call %INSTALLATION_DIR%\common\set_env.bat
                                             
call %ANT_HOME%\bin\ant -buildfile %INSTALLATION_DIR%\tools\internal\ant\register.xml -lib %INSTALLATION_DIR%\tools\internal\libs -logger com.riversoft.dtask.BuildLogger -q

pause