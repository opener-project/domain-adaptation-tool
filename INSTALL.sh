# This script install the custom libSVM (http://www.csie.ntu.edu.tw/~cjlin/libsvm/)
# and the Domain Adaptation Tool for OpeNER
# Author: Andoni Azpeitia (aazpeitia@vicomtech.org) - Vicomtech-IK4 (http://www.vicomtech.es/)

cd ./lib/libSVM
mvn install
EXEC=$?
if [ $EXEC -ne 0 ]
then
    echo "Error installing libSVM"
    exit
fi
cd ../..
mvn package
EXEC=$?
if [ $EXEC -ne 0 ]
then
    echo "Error installing Domain Adaptation Tool"
    exit
fi
