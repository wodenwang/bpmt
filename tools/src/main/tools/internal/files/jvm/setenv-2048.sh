#!/bin/sh

CATALINA_OPTS="-server -Xms512m -Xmx2048m -XX:PermSize=256m -XX:MaxPermSize=512m -XX:-UseGCOverheadLimit -XX:SurvivorRatio=8 -XX:-UseAdaptiveSizePolicy -XX:+UseConcMarkSweepGC -Dfile.encoding=UTF-8";
export CATALINA_OPTS;
