#!/bin/sh

CATALINA_OPTS="-server -Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m -XX:-UseGCOverheadLimit -XX:SurvivorRatio=8 -XX:-UseAdaptiveSizePolicy -XX:+UseConcMarkSweepGC -Dfile.encoding=UTF-8";
export CATALINA_OPTS;
