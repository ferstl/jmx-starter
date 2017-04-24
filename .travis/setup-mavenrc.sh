#!/bin/bash

cat << EOF > ~/.mavenrc
export JAVA_HOME=$JAVA_HOME
export MAVEN_HOME=$MAVEN_HOME
# Maven versions < 3.5.0
export M2_HOME=$MAVEN_HOME

EOF
