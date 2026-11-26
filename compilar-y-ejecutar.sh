#!/usr/bin/env bash
# Uso:  ./compilar-y-ejecutar.sh            -> compila y abre el menú
#       ./compilar-y-ejecutar.sh pruebas     -> compila y ejecuta las pruebas
set -e
cd "$(dirname "$0")"
rm -rf bin && mkdir bin
javac -encoding UTF-8 -d bin $(find src -name "*.java")
if [ "$1" = "pruebas" ]; then
  java -Dfile.encoding=UTF-8 -cp bin pruebas.PruebasSigac
else
  java -Dfile.encoding=UTF-8 -cp bin app.Main
fi
