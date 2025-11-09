@echo off

title BPMT HTTP Server 工具
color 0a

set PWD=%~dp0

set INSTALLATION_DIR=%PWD%
set NGINX_DIR=%INSTALLATION_DIR%\common\nginx\win

cd %NGINX_DIR%>nul

echo 正在检查nginx进程...
echo.

if exist logs\nginx.pid (
	echo nginx进程已经存在，将重新加载配置.
	echo.
	call nginx -s reload
	echo nginx 重启成功.
	echo.
) else (
	echo 正在启动nginx...
	call start nginx.exe
	echo nginx 成功启动.
)

echo.

pause
exit
