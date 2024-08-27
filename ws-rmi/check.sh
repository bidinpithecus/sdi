#!/bin/bash

comparar_arquivos() {
    if [[ -f "$1" && -f "$2" ]]; then
        if diff "$1" "$2" > /dev/null; then
            return 1
        else
            return 0
        fi
    else
        echo "arquivos inexistentes"
        return 0
    fi
}

comparar_arquivos "a.txt" "a.txt.bkp"
a=$?
comparar_arquivos "b.txt" "b.txt.bkp"
b=$?
comparar_arquivos "c.txt" "c.txt.bkp"
c=$?

if [ "$a" -eq 1 ] && [ "$b" -eq 1 ] && [ "$c" -eq 1 ]; then
    echo "Deu tudo certo"
else
    echo "Algo deu errado"
fi
