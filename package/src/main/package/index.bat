@echo off

set PWD=%~dp0
set INSTALLATION_DIR=%PWD%

set /p tomcat-port=<%INSTALLATION_DIR%\common\CATALINA_BASE\conf\tomcat-port.conf

start http://localhost:%tomcat-port%