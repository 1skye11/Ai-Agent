#!/bin/bash
# 把应用注册成 systemd 服务，开机自启、异常自动拉起
set -e
APP_HOME="/opt/ai-agent"
SERVICE_FILE="/etc/systemd/system/ai-agent.service"

[ "$EUID" -eq 0 ] || { echo "请用 root: sudo bash install-systemd.sh"; exit 1; }

id ai-agent &>/dev/null || useradd -r -s /sbin/nologin ai-agent
mkdir -p "$APP_HOME/app" "$APP_HOME/logs" "$APP_HOME/config"
chown -R ai-agent:ai-agent "$APP_HOME"

cat > "$SERVICE_FILE" <<EOF
[Unit]
Description=AI Agent Station Study
After=network-online.target
Wants=network-online.target

[Service]
Type=simple
User=ai-agent
Group=ai-agent
WorkingDirectory=$APP_HOME/app
ExecStart=/usr/bin/java -Xms2g -Xmx2g -server -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$APP_HOME/logs/heap.hprof -Dspring.profiles.active=prod --spring.config.location=$APP_HOME/config/ -jar $APP_HOME/app/app.jar
SuccessExitStatus=143
Restart=on-failure
RestartSec=10
StandardOutput=append:$APP_HOME/logs/stdout.log
StandardError=append:$APP_HOME/logs/stderr.log
LimitNOFILE=65535

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable ai-agent
systemctl restart ai-agent
systemctl --no-pager status ai-agent | head -n 12
