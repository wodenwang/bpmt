FROM tomcat:7.0.109-jdk8-openjdk

ENV TZ=Asia/Shanghai \
    APP_CLASSES=/usr/local/tomcat/webapps/ROOT/WEB-INF/classes \
    JAVA_OPTS="-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"

RUN rm -rf /usr/local/tomcat/webapps/*

COPY platform.war /tmp/platform.war
COPY ueditor.war /tmp/ueditor.war

RUN mkdir -p /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/ueditor \
    && cd /usr/local/tomcat/webapps/ROOT \
    && jar -xf /tmp/platform.war \
    && cd /usr/local/tomcat/webapps/ueditor \
    && jar -xf /tmp/ueditor.war \
    && rm -f /tmp/platform.war /tmp/ueditor.war

COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh

RUN chmod +x /usr/local/bin/docker-entrypoint.sh \
    && mkdir -p /data/files/attachments /data/files/base64

EXPOSE 8080

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["catalina.sh", "run"]
