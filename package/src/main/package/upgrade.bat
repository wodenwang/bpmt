@echo off

title  BPMT Tools 升级工具
color 0a

set PWD=%~dp0

echo *********************************************************
echo ******************** BPMT Tools (C)(R) ******************
echo ***********************  升级工具  ***********************
echo *********************************************************
echo.

set INSTALLATION_DIR=%PWD%
call %INSTALLATION_DIR%\common\set_env.bat

if not ""%1"" == ""false"" goto unstrictmode
if ""%1"" == ""false"" goto strictmode

:strictmode
echo *********************************************************
echo **************   使用严格升级模式(非强制替换)  **************                    
call %ANT_HOME%\bin\ant -Dstrict=true -buildfile %INSTALLATION_DIR%\tools\internal\ant\upgrade.xml -lib %INSTALLATION_DIR%\tools\internal\libs -logger com.riversoft.dtask.BuildLogger -q
goto end

:unstrictmode
echo *********************************************************
echo **************   使用宽松升级模式(强制替换)  ****************                   
call %ANT_HOME%\bin\ant -Dstrict=false -buildfile %INSTALLATION_DIR%\tools\internal\ant\upgrade.xml -lib %INSTALLATION_DIR%\tools\internal\libs -logger com.riversoft.dtask.BuildLogger -q
goto end

:end
pause