#!/bin/bash
APP_HOME="/opt/ai-agent"
tail -n 200 -f "$APP_HOME/logs/stdout.log"
