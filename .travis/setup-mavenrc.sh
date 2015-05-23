#!/bin/bash

cat << EOF > ~/.mavenrc
export JAVA_HOME=$JAVA_HOME
export M2_HOME=$MAVEN_HOME

EOF
