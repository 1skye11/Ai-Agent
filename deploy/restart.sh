#!/bin/bash
set -e
APP_HOME="/opt/ai-agent"
bash "$APP_HOME/stop.sh"  || true
sleep 2
bash "$APP_HOME/start.sh"
