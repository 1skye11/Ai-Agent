#!/bin/bash
# 启动脚本：以后台方式拉起 Spring Boot 应用，并把 PID 落盘方便 stop。
set -e
APP_HOME="/opt/ai-agent"
JAR_FILE="$APP_HOME/app/app.jar"
LOG_DIR="$APP_HOME/logs"
PID_FILE="$APP_HOME/app/app.pid"

JAVA_OPTS="-Xms2g -Xmx2g -server -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$LOG_DIR/heap.hprof"

mkdir -p "$LOG_DIR" "$APP_HOME/app" "$APP_HOME/config"

if [ -f "$PID_FILE" ] && kill -0 $(cat "$PID_FILE") 2>/dev/null; then
  echo "already running, pid=$(cat $PID_FILE)"
  exit 0
fi

nohup java $JAVA_OPTS \
  -Dspring.profiles.active=prod \
  --spring.config.location=$APP_HOME/config/ \
  -jar "$JAR_FILE" \
  > "$LOG_DIR/stdout.log" 2>&1 &

echo $! > "$PID_FILE"
sleep 2
if kill -0 $(cat "$PID_FILE") 2>/dev/null; then
  echo "started, pid=$(cat $PID_FILE).  tail -f $LOG_DIR/stdout.log"
else
  echo "start failed, see $LOG_DIR/stdout.log"
  exit 1
fi
