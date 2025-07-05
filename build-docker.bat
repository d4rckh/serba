@echo off
setlocal enabledelayedexpansion

cd frontend
pnpm build

cd ..

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

echo Docker image serba-backend:latest built successfully!

REM Step 3: Tag the image for GitHub Container Registry
echo Tagging Docker image for ghcr.io...
docker tag serba-backend:latest ghcr.io/d4rckh/serba:latest

if errorlevel 1 (
    echo Docker tag failed.
    exit /b 1
)

REM Step 4: Push the image to GitHub Container Registry
REM echo Pushing Docker image to ghcr.io...
REM docker push ghcr.io/d4rckh/serba:latest

REM if errorlevel 1 (
REM     echo Docker push failed.
REM     exit /b 1
REM )

REM echo Docker image pushed to ghcr.io/d4rckh/serba:latest successfully!

endlocal
pause
