@echo off
setlocal enabledelayedexpansion

REM Step 1: Copy frontend/dist to Micronaut public folder
echo Copying frontend/dist to src/main/resources/public...
if exist src\main\resources\public (
    rmdir /s /q src\main\resources\public
)
mkdir src\main\resources\public

xcopy frontend\dist src\main\resources\public /s /e /y /i >nul
if errorlevel 1 (
    echo Failed to copy frontend/dist to src\main\resources\public
    exit /b 1
)

REM Step 2: Build Docker image (which builds the backend inside Docker)
echo Building Docker image...

docker build -t serba-backend:latest -f Dockerfile .

if errorlevel 1 (
    echo Docker build failed.
    exit /b 1
)

echo Docker image serba-jvm:latest built successfully!
endlocal
pause
