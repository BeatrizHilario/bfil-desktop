@echo off
echo =======================================
echo     GERANDO NOVO INSTALADOR BFIL CLOUD 1.0.2
echo =======================================

echo [1/2] Limpando instalador anterior...
if exist dist rmdir /s /q dist

echo [2/2] Criando executavel instalador com jpackage...
"C:\Program Files\Java\jdk-21.0.11\bin\jpackage.exe" ^
  --type exe ^
  --input target/ ^
  --name "BFIL Cloud" ^
  --main-jar sistema-autoeletrica-1.0-SNAPSHOT.jar ^
  --main-class autoeletrica.Launcher ^
  --app-version 1.0.2 ^
  --vendor "BFIL" ^
  --icon "src/main/resources/images/app-icon.ico" ^
  --win-dir-chooser ^
  --win-shortcut ^
  --win-menu ^
  --dest dist/

if %ERRORLEVEL% EQU 0 (
    echo =======================================
    echo   SUCESSO! Instalador pronto em /dist
    echo =======================================
) else (
    echo [ERRO] Ocorreu uma falha no jpackage.
)

pause