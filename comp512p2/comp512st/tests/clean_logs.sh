#!/bin/bash
set -e

cd -- "$(dirname -- "$0")"
shopt -s nullglob
logs=(game-*.log)

if ((${#logs[@]})); then
    rm -- "${logs[@]}"
fi
echo "Deleted ${#logs[@]} game log files."
