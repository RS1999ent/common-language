#!/usr/bin/awk -f

$1 < 65536 && $2 < 65536 {
    print;
}
