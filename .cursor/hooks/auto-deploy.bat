@echo off
setlocal enabledelayedexpansion

set "PROJECT_DIR=e:\my_work_new\kotlin-app"
set "ADB=C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe"
set "DEPLOY_MARKER=%PROJECT_DIR%\.last_deploy"
set "PACKAGE=com.example.kotlin_app_study"

cd /d "%PROJECT_DIR%" || exit /b 0

:: 检查是否有连接的设备
"%ADB%" devices 2>nul | findstr /r "device$" >nul 2>&1
if errorlevel 1 exit /b 0

:: 检查源文件是否有变更
if exist "%DEPLOY_MARKER%" (
    set "HAS_CHANGES="
    for /r "app\src" %%f in (*.kt *.xml) do (
        xcopy /d /y /l "%DEPLOY_MARKER%" "%%f" 2>nul | findstr /c:"0" >nul 2>&1
        if errorlevel 1 (
            set "HAS_CHANGES=1"
            goto :do_deploy
        )
    )
    if not defined HAS_CHANGES exit /b 0
)

:do_deploy
echo %date% %time% > "%DEPLOY_MARKER%"

:: 编译一次
call gradlew.bat assembleDebug >nul 2>&1
if errorlevel 1 exit /b 0

:: 遍历所有连接的设备，逐个部署
for /f "tokens=1" %%d in ('"%ADB%" devices ^| findstr /r "device$"') do (
    "%ADB%" -s %%d install -r app\build\outputs\apk\debug\app-debug.apk >nul 2>&1
    "%ADB%" -s %%d shell am start -n %PACKAGE%/.MainActivity >nul 2>&1
)

exit /b 0
