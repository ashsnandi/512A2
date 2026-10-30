#!/bin/bash
# Compare the move sequences recorded by each player for game-26-99.
# Stop the script if an unhandled command fails.
set -e

# Work in this script's directory so it can be run from any location.
cd -- "$(dirname -- "$0")"
# If no files match, use an empty array instead of the literal wildcard pattern.
shopt -s nullglob
# Collect player move logs (players 1-9), excluding display and process-info logs.
logs=(game-26-99-[1-9].log)

# Comparing sequences requires at least two players' logs.
if ((${#logs[@]} < 2)); then
    echo "Cannot compare: need at least two player move logs."
    exit 1
fi

# The first line is a player-specific header; require moves after that header.
if [[ $(wc -l < "${logs[0]}") -le 1 ]]; then
    echo "Cannot compare: ${logs[0]} has no moves."
    exit 1
fi

# Use the first log as the reference and compare every remaining log against it.
# The array slice :1 skips the first element, which is the reference itself.
for log in "${logs[@]:1}"; do
    # tail -n +2 skips each header; <(...) passes that output to diff as a file.
    # diff -q checks for differences quietly; ! enters this branch if it fails.
    if ! diff -q <(tail -n +2 "${logs[0]}") <(tail -n +2 "$log") > /dev/null; then
        echo "FAIL: ${logs[0]} and $log have different move sequences."
        exit 1
    fi
done

# Reaching this point means every comparison matched (the script exits with 0).
echo "PASS: all ${#logs[@]} player logs have the same move sequence."
