@echo off
setlocal

if not exist bin mkdir bin

set "SOURCES=%TEMP%\astrojournal_sources.txt"
dir /s /b src\*.java > "%SOURCES%" 2>nul

for %%F in ("%SOURCES%") do if %%~zF==0 (
    echo No Java sources found under src.
    del "%SOURCES%"
    exit /b 1
)

javac -d bin @"%SOURCES%"
set "STATUS=%ERRORLEVEL%"
del "%SOURCES%"

if not "%STATUS%"=="0" (
    echo Build failed.
    exit /b %STATUS%
)

echo Build succeeded.
endlocal
