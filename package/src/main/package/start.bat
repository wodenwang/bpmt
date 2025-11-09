@echo off
title BPMT Tools 服务器管理工具
color 0a

set PWD=%~dp0
set INSTALLATION_DIR=%PWD%

call %INSTALLATION_DIR%\common\set_env.bat

echo *********************************************************
echo ******************** BPMT Tools (C)(R) ******************
echo **********************  服务器管理工具  *******************
echo *********************************************************
echo.
	
set COMMON_DIR=%INSTALLATION_DIR%\common
set CATALINA_HOME=%COMMON_DIR%\tomcat

set CATALINA_BASE=%COMMON_DIR%\CATALINA_BASE

echo JAVA_HOME:		%JAVA_HOME%
echo ANT_HOME:		%ANT_HOME% 
echo CATALINA_BASE: %CATALINA_BASE%
echo CATALINA_HOME: %CATALINA_HOME%

call %CATALINA_HOME%\bin\catalina.bat start

pause