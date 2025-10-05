#!/bin/bash

echo

tests=("basic" "edge" "property" "performance" "memory" "integration" "distribution" "context" "cross-validation" "iterator" "verify")

for test in "${tests[@]}"; do
    java -jar target/bmmv-algorithm-1.0-SNAPSHOT.jar "$test" -c "${test}-test-results.csv"
    echo
done

ls -1 *-test-results.csv 2>/dev/null