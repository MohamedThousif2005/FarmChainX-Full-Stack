@echo off
echo ====================================
echo FarmChainX SIMPLE AI Installer
echo ====================================
echo.

cd /d "C:\Users\PC\OneDrive\Desktop\FarmChainX\back-end\backend\python-ai-service"

echo Step 1: Checking Python...
python --version
if errorlevel 1 (
    echo ERROR: Python not found!
    echo Please install Python from python.org
    pause
    exit /b 1
)

echo Step 2: Installing simple packages...
pip install flask==2.3.3 flask-cors==4.0.0 numpy==1.24.3 opencv-python==4.8.1.78 pillow==10.0.0

echo Step 3: Starting ENHANCED AI Service...
echo.
echo ✅ ENHANCED Service will start at http://localhost:5000
echo 🔬 Using improved feature-based analysis
echo 🦠 Better disease detection for rotten crops
echo.
python app.py