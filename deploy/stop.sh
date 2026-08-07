#!/bin/bash
set -e
APP_HOME="/opt/ai-agent"
PID_FILE="$APP_HOME/app/app.pid"
[ -f "$PID_FILE" ] || { echo "no pid file"; exit 0; }
PID=$(cat "$PID_FILE")
kill "$PID" 2>/dev/null || true
for i in $(seq 1 30); do
  kill -0 "$PID" 2>/dev/null || { rm -f "$PID_FILE"; echo "stopped"; exit 0; }
  sleep 1
done
echo "force kill"
kill -9 "$PID" 2>/dev/null || true
rm -f "$PID_FILE"
