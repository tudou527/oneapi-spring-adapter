#!/usr/bin/env bash

dependency=(org.mozilla.intl:chardet:1.0 info.monitorenter.cpdetector:cpdetector:1.0.10)

for element in ${dependency[@]}
do
    array=(${element//:/ })
    mvn install:install-file -Dfile=./lib/${array[1]}-${array[2]}.jar -DgroupId=${array[0]} -DartifactId=${array[1]} -Dversion=${array[02]} -Dpackaging=jar -DgeneratePom=true -DcreateChecksum=true
done