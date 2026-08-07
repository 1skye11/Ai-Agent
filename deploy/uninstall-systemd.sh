#!/bin/bash
[ "$EUID" -eq 0 ] || { echo "请用 root: sudo bash uninstall-systemd.sh"; exit 1; }
systemctl stop ai-agent 2>/dev/null || true
systemctl disable ai-agent 2>/dev/null || true
rm -f /etc/systemd/system/ai-agent.service
systemctl daemon-reload
echo "uninstalled. 应用文件仍在 $APP_HOME，可手动 rm -rf。"
