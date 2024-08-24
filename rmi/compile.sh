mkdir target 2> /dev/null
find . -name *.java | xargs javac -d target
