@echo off

title BPMT HTTP Server 工具
color 0a

set PWD=%~dp0

set INSTALLATION_DIR=%PWD%
set NGINX_DIR=%INSTALLATION_DIR%\common\nginx\win

cd %NGINX_DIR%

echo 正在检查nginx进程...
echo.

if exist logs\nginx.pid (
	echo nginx进程已经找到，已经发出停止信号...
	echo.
	call nginx -s quit
	echo nginx 已经停止.
) else (
	echo nginx 进程没有找到，请检查是否启动?
)

echo.

pause