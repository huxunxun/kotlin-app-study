#!/usr/bin/env bash
# Cursor 'stop' hook: 编译并自动部署到所有连接的 Android 设备/模拟器。
# 仅在 app/src 下 .kt/.xml 自上次部署后有变更时才执行。
# 任何失败都静默 exit 0，避免阻塞 agent。

set -u

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"
ANDROID_HOME="${ANDROID_HOME:-$HOME/Library/Android/sdk}"
ADB="$ANDROID_HOME/platform-tools/adb"
JAVA_HOME_DEFAULT="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export JAVA_HOME="${JAVA_HOME:-$JAVA_HOME_DEFAULT}"

DEPLOY_MARKER="$PROJECT_DIR/.last_deploy"
LOG_FILE="$PROJECT_DIR/.cursor/hooks/auto-deploy.log"
PACKAGE="com.example.kotlin_app_study"
ACTIVITY="$PACKAGE/.MainActivity"
APK="$PROJECT_DIR/app/build/outputs/apk/debug/app-debug.apk"

cd "$PROJECT_DIR" 2>/dev/null || exit 0

mkdir -p "$(dirname "$LOG_FILE")"
{
  echo "==== $(date '+%F %T') auto-deploy ===="
} >> "$LOG_FILE" 2>&1

if [ ! -x "$ADB" ]; then
  echo "adb not found at $ADB, skip." >> "$LOG_FILE" 2>&1
  exit 0
fi

DEVICES=$("$ADB" devices 2>/dev/null | awk 'NR>1 && $2=="device"{print $1}')
if [ -z "$DEVICES" ]; then
  echo "no device connected, skip." >> "$LOG_FILE" 2>&1
  exit 0
fi

if [ -f "$DEPLOY_MARKER" ]; then
  CHANGED=$(find app/src -type f \( -name '*.kt' -o -name '*.xml' \) -newer "$DEPLOY_MARKER" -print -quit 2>/dev/null)
  if [ -z "$CHANGED" ]; then
    echo "no source change since last deploy, skip." >> "$LOG_FILE" 2>&1
    exit 0
  fi
fi

echo "building..." >> "$LOG_FILE" 2>&1
./gradlew :app:assembleDebug >> "$LOG_FILE" 2>&1
BUILD_RC=$?
if [ $BUILD_RC -ne 0 ]; then
  echo "build failed (rc=$BUILD_RC), keep marker untouched." >> "$LOG_FILE" 2>&1
  exit 0
fi

if [ ! -f "$APK" ]; then
  echo "apk not found at $APK, skip." >> "$LOG_FILE" 2>&1
  exit 0
fi

date '+%F %T' > "$DEPLOY_MARKER"

for DEV in $DEVICES; do
  echo "deploying to $DEV ..." >> "$LOG_FILE" 2>&1
  "$ADB" -s "$DEV" install -r "$APK" >> "$LOG_FILE" 2>&1
  "$ADB" -s "$DEV" shell am start -n "$ACTIVITY" >> "$LOG_FILE" 2>&1
done

echo "done." >> "$LOG_FILE" 2>&1
exit 0
