#!/bin/bash

$JAVA_HOME/bin/java -Dlog4j.configuration=file:log4j.xml "$@" -jar tabular.jar