#!/usr/bin/env bash

mkdir -p tmp

inkscape -w 16 -h 16 --export-filename tmp/16.png src/logo.svg
inkscape -w 24 -h 24 --export-filename tmp/24.png src/logo.svg
inkscape -w 32 -h 32 --export-filename tmp/32.png src/logo.svg
inkscape -w 64 -h 64 --export-filename tmp/64.png src/logo.svg
inkscape -w 192 -h 192 --export-filename tmp/192.png src/logo.svg
inkscape -w 512 -h 512 --export-filename tmp/512.png src/logo.svg

convert tmp/16.png tmp/24.png tmp/32.png tmp/64.png public/favicon.ico
cp tmp/192.png public/logo192.png
cp tmp/512.png public/logo512.png

rm -r tmp
