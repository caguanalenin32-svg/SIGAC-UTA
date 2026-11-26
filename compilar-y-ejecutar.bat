@echo off
REM Uso:  compilar-y-ejecutar.bat            -> compila y abre el menu
REM       compilar-y-ejecutar.bat pruebas     -> compila y ejecuta las pruebas
chcp 65001 >nul
cd /d "%~dp0"
if exist bin rmdir /s /q bin
mkdir bin
dir /s /b src\*.java > fuentes.txt
javac -encoding UTF-8 -d bin @fuentes.txt
if errorlevel 1 (
  del fuentes.txt
  echo ERROR de compilacion. Verifique que tiene el JDK 11 o superior instalado.
  exit /b 1
)
del fuentes.txt
if "%1"=="pruebas" (
  java -Dfile.encoding=UTF-8 -cp bin pruebas.PruebasSigac
) else (
  java -Dfile.encoding=UTF-8 -cp bin app.Main
)
