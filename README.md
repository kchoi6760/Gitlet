javac gitlet/*.java
rm -r .gitlet
java gitlet.Main init
echo "commit 1" > a.txt
cat a.txt
java gitlet.Main add a.txt
java gitlet.Main commit cm1
echo "commit 2" > a.txt
cat a.txt
java gitlet.Main add a.txt
java gitlet.Main commit cm2
java gitlet.Main log
echo "commit 3" > a.txt
java gitlet.Main add a.txt
java gitlet.Main commit cm3
java gitlet.Main log
java gitlet.Main global-log

java gitlet.Main checkout -- a.txt
cat a.txt

javac gitlet/*.java
rm -r .gitlet
java gitlet.Main init
echo "commit 1" > a.txt
cat a.txt
java gitlet.Main add a.txt
java gitlet.Main commit cm1
echo "commit 2" > a.txt
cat a.txt
java gitlet.Main add a.txt
java gitlet.Main commit cm2
java gitlet.Main log
echo "commit 3" > a.txt


java gitlet.Main checkout -- a.txt
cat a.txt