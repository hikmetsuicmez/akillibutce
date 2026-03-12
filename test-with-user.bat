@echo off
REM ============================================================
REM test-with-user.bat  (Windows icin)
REM Belirli kullanici profiliyle Spring Boot testlerini calistirir.
REM
REM Kullanim:
REM   test-with-user.bat PREMIUM
REM   test-with-user.bat FREE
REM   test-with-user.bat ALL
REM ============================================================

setlocal EnableDelayedExpansion

set REPO_ROOT=%~dp0
set BACKEND_DIR=%REPO_ROOT%backend

REM -------------------------------------------------------
REM Parametre kontrolu
REM -------------------------------------------------------
if "%~1"=="" (
    echo [HATA] Kullanici profili belirtilmedi.
    echo.
    echo Kullanim:
    echo   test-with-user.bat PREMIUM
    echo   test-with-user.bat FREE
    echo   test-with-user.bat ALL
    exit /b 1
)

set PROFIL=%~1
call :TO_UPPER PROFIL

REM Profil dogrulama
if /I "%PROFIL%"=="PREMIUM" goto :GECERLI_PROFIL
if /I "%PROFIL%"=="FREE"    goto :GECERLI_PROFIL
if /I "%PROFIL%"=="ALL"     goto :GECERLI_PROFIL

echo [HATA] Gecersiz profil: %PROFIL%
echo Gecerli degerler: PREMIUM, FREE, ALL
exit /b 1

:GECERLI_PROFIL

if not exist "%BACKEND_DIR%\pom.xml" (
    echo [HATA] pom.xml bulunamadi: %BACKEND_DIR%\pom.xml
    exit /b 1
)

REM -------------------------------------------------------
REM Profil testi
REM -------------------------------------------------------
set GENEL_BASARISIZ=0

if /I "%PROFIL%"=="ALL" (
    call :TEST_CALISTIR PREMIUM
    if !errorlevel! neq 0 set GENEL_BASARISIZ=1
    call :TEST_CALISTIR FREE
    if !errorlevel! neq 0 set GENEL_BASARISIZ=1
) else (
    call :TEST_CALISTIR %PROFIL%
    if !errorlevel! neq 0 set GENEL_BASARISIZ=1
)

if %GENEL_BASARISIZ%==0 (
    echo.
    echo [BASARILI] Tum testler tamamlandi.
    exit /b 0
) else (
    echo.
    echo [BASARISIZ] Bazi testler basarisiz. Yukaridaki ciktiyi inceleyin.
    exit /b 1
)

:TEST_CALISTIR
set _PROFIL=%~1
if /I "%_PROFIL%"=="PREMIUM" set SPRING_PROFIL=test-premium
if /I "%_PROFIL%"=="FREE"    set SPRING_PROFIL=test-free

echo.
echo ==========================================
echo  Profil: %_PROFIL%
echo  Spring Profil: %SPRING_PROFIL%
echo ==========================================
echo.

set TEST_USER_PROFIL=%_PROFIL%
cd /d "%BACKEND_DIR%"

mvn test -Dspring.profiles.active=%SPRING_PROFIL% --no-transfer-progress
set MVN_EXIT=%errorlevel%
cd /d "%REPO_ROOT%"

if %MVN_EXIT% equ 0 (
    echo.
    echo [%_PROFIL%] Testler BASARILI
) else (
    echo.
    echo [%_PROFIL%] Testler BASARISIZ
)
exit /b %MVN_EXIT%

:TO_UPPER
for %%i in (A B C D E F G H I J K L M N O P Q R S T U V W X Y Z) do (
    set %1=!%1:%%i=%%i!
)
goto :eof

endlocal
