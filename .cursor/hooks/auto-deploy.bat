@echo off
setlocal

set "PROJECT_DIR=e:\my_work_new\kotlin-app"
set "ADB=C:\Users\Administrator\AppData\Local\Android\Sdk\platform-tools\adb.exe"
set "DEPLOY_MARKER=%PROJECT_DIR%\.last_deploy"

cd /d "%PROJECT_DIR%" || exit /b 0

:: 检查模拟器是否在运行
"%ADB%" devices 2>nul | findstr /r "device$" >nul 2>&1
if errorlevel 1 exit /b 0

:: 检查是否有源文件比上次部署更新
if exist "%DEPLOY_MARKER%" (
    set "HAS_CHANGES="
    for /r "app\src" %%f in (*.kt *.xml) do (
        if "%%~tf" gtr "" (
            xcopy /d /y /l "%DEPLOY_MARKER%" "%%f" 2>nul | findstr /c:"0" >nul 2>&1
            if errorlevel 1 (
                set "HAS_CHANGES=1"
                goto :do_deploy
            )
        )
    )
    if not defined HAS_CHANGES exit /b 0
)

:do_deploy
:: 记录部署时间
echo %date% %time% > "%DEPLOY_MARKER%"

:: 编译并部署
call gradlew.bat installDebug >nul 2>&1
if errorlevel 1 exit /b 0

:: 启动 App
"%ADB%" shell am start -n com.example.kotlin_app_study/.MainActivity >nul 2>&1

exit /b 0
