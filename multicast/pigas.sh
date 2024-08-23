#!/bin/bash

file="pigas.results"

declare -A counts

for i in {1..10}; do
  counts[$i]=0
done

numbers=$(cat "$file" | tr -d '\n' | sed 's/,$//')

IFS=',' read -ra nums <<< "$numbers"

for num in "${nums[@]}"; do
  if [[ $num =~ ^[1-9]$|^10$ ]]; then
    ((counts[$num]++))
  fi
done

for i in {1..10}; do
  if [[ $i -lt 10 ]]; then
    echo -n " "
  fi
  echo -n "$i: "
  for ((j=0; j<${counts[$i]}; j++)); do
    echo -n "#"
  done
  echo
done
