
1. 初始化安装
1) 本地安装JDK7环境,安装Eclipse Kepler 4.0+.(官方下载地址如下)
http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html
http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/keplerr
2) eclipse中选择import -> Existing Maven Project -> 选择工程路径,即完成开发环境的准备.
3) 完成以上步骤之后,执行"主动更新"命令,从云端获取到最新的代码信息,完成之后直接"启动tomcat"即可以运行demo系统.
   最后打开您的firefox/chrome浏览器(http://localhost:8080)感受一下RiverSoft的魅力吧.


2. 常用Maven命令:
0) 小技巧
  eclipse中,Run -> Run Configurations -> 左侧m2 Maven Build可以新建固定的maven命令.
  base directory选择当前工程路径,goals填写maven命令(mvn不需要填写)即可在eclipse环境下执行maven命令.

1) 主动更新(从云上获取最新代码)
mvn -Pupdate validate

2) 启动tomcat(默认端口8080)
mvn tomcat7:run

2.1) 使用自定义端口启动tomcat
mvn tomcat7:run -Dmaven.tomcat.port=8083

3) 打包(target中可以获取目标文件)
mvn clean install



任何疑问,请访问 http://www.riversoft.com.cn 或联系 riversoft@126.com .