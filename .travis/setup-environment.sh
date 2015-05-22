#!/bin/bash

# ###########################################################################
# Locations
# ###########################################################################
BASEDIR=`cd $(dirname $0); pwd`
WORKINGDIR=`pwd`
SCRIPT_NAME=`basename $0`

# Use TEST_ROOT for local testing
ROOT_DIR=${HOME}
[ ! -z ${TEST_ROOT} ] && ROOT_DIR=${TEST_ROOT}
DOWNLOAD_DIR=${ROOT_DIR}/download
INSTALL_DIR=${ROOT_DIR}/bin


# ###########################################################################
# Variables
# ###########################################################################
ORACLE_DOWNLOAD_COOKIE="Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie"
AZUL_DOWNLOAD_REFERER="Referer: http://www.azulsystems.com/products/zulu/downloads"

print_env() {
  cat << EOF
Build Environment Setup
-----------------------
Script                 $SCRIPT_NAME
Base directory         $BASEDIR
Working directory      $WORKINGDIR
Installation Root      $ROOT_DIR
Download directory     $DOWNLOAD_DIR
Installation directory $INSTALL_DIR

Requested Installations
-----------------------
Java runtimes:  $REQUIRED_JAVA_RUNTIMES
Maven versions: $REQUIRED_MAVEN_VERSIONS

EOF
}

init() {
  echo "Creating directories"
  mkdir -vp ${DOWNLOAD_DIR}
  mkdir -vp ${INSTALL_DIR}

  echo "Initialization complete"
}

install_oracle_java() {
  # jdk, server-jre or jre
  java_runtime=$1
  jdk_major_version=$2
  jdk_minor_version=$3
  jdk_build_number=$4

  # JDK full version string as it appears in the download URL, e.g. 8u40-b26
  jdk_full_version_string=${jdk_major_version}u${jdk_minor_version}-b${jdk_build_number}

  # JDK version string as it appears in the archive name
  jdk_version_string=${jdk_major_version}u${jdk_minor_version}

  # JDK version number as it appears within the archive, e.g. 1.8.0_40
  archive_version="1.${jdk_major_version}.0_${jdk_minor_version}"
  
  
  # Folder name in the downloaded archive
  case $java_runtime in
    jdk|server-jre) archive_content="jdk";;
    jre) archive_content="jre";;
  esac

  # Final name of the downloaded file and of the installation folder
  final_name="oracle-${java_runtime}-${archive_version}"

  download_url=http://download.oracle.com/otn-pub/java/jdk/${jdk_full_version_string}/${java_runtime}-${jdk_version_string}-linux-x64.tar.gz
  download_target=${DOWNLOAD_DIR}/oracle-${java_runtime}-${jdk_version_string}.tar.gz

  if [ ! -d $INSTALL_DIR/${final_name} ]; then

    # Download if necessary
    if [ ! -f ${download_target} ]; then
      echo "Downloading ${final_name}"
      curl --create-dirs -o ${download_target} -LH "${ORACLE_DOWNLOAD_COOKIE}" "${download_url}"
    else
      echo "${download_target} does already exist. No need to download."
    fi

    # Extract and install
    echo "Extracting ${download_target} to ${INSTALL_DIR}"
    tar --overwrite -zxf ${download_target} -C ${INSTALL_DIR}

    echo "Rename to ${final_name}"
    mv -f ${INSTALL_DIR}/${archive_content}${archive_version} ${INSTALL_DIR}/${final_name}
  else
    echo "${INSTALL_DIR}/${final_name} does already exist. Nothing to install."
  fi

  echo "Installation of ${final_name} completed."

}

install_azul_zulu() {
  dir=$1
  java_version=$2
  zulu_version=$3

  download_url=http://cdn.azulsystems.com/zulu/${dir}/zulu${java_version}-${zulu_version}-x86lx64.zip
  download_target=${DOWNLOAD_DIR}/azul-zulu-${java_version}-x86lx64.zip
  archive_content=zulu${java_version}-${zulu_version}-x86lx64
  final_name=azul-zulu-${java_version}

  if [ ! -d $INSTALL_DIR/${final_name} ]; then

    # Download if necessary
    if [ ! -f ${download_target} ]; then
      echo "Downloading ${final_name}"
      curl --create-dirs -o ${download_target} -LH "${AZUL_DOWNLOAD_REFERER}" "${download_url}"
    else
      echo "${download_target} does already exist. No need to download."
    fi

    # Extract and install
    echo "Extracting ${download_target} to ${INSTALL_DIR}"
    unzip -q -o -d ${INSTALL_DIR} ${download_target} 

    echo "Rename to ${final_name}"
    mv -f ${INSTALL_DIR}/${archive_content} ${INSTALL_DIR}/${final_name}
  else
    echo "${INSTALL_DIR}/${final_name} does already exist. Nothing to install."
  fi


}

install_maven() {
  maven_version=$1
  final_name="apache-maven-${maven_version}"
  download_url=http://repo1.maven.org/maven2/org/apache/maven/apache-maven/${maven_version}/apache-maven-${maven_version}-bin.tar.gz
  download_target=${DOWNLOAD_DIR}/${final_name}-bin.tar.gz

  if [ ! -d $INSTALL_DIR/${final_name} ]; then

    # Download if necessary
    if [ ! -f ${download_target} ]; then
      echo "Downloading ${final_name}"
      curl --create-dirs -o ${download_target} "${download_url}"
    else
      echo "${download_target} does already exist. No need to download."
    fi

    # Extract and install
    echo "Extracting ${download_target} to ${INSTALL_DIR}"
    tar --overwrite -zxf ${download_target} -C ${INSTALL_DIR}
  else
    echo "${INSTALL_DIR}/${final_name} does already exist. Nothing to install."
  fi

  echo "Installation of ${final_name} completed."

}

install_java_runtimes() {
if [ -z $REQUIRED_JAVA_RUNTIMES ]; then
  echo "No Java runtimes specified."
fi

tmp_ifs=$IFS
IFS=","
for rt in $REQUIRED_JAVA_RUNTIMES; do
  case $rt in
     oracle-jdk-1.8.0_45) install_oracle_java "jdk"  "8" "45" "14";;
     oracle-server-jre-1.8.0_45) install_oracle_java "server-jre"  "8" "45" "14";;
     oracle-jre-1.8.0_45) install_oracle_java "jre"  "8" "45" "14";;
     
     oracle-jdk-1.8.0_40) install_oracle_java "jdk"  "8" "40" "26";;
     oracle-server-jre-1.8.0_40) install_oracle_java "server-jre"  "8" "40" "26";;
     oracle-jre-1.8.0_40) install_oracle_java "jre"  "8" "40" "26";;
     
     oracle-jdk-1.8.0_31) install_oracle_java "jdk"  "8" "31" "13";;
     oracle-server-jre-1.8.0_31) install_oracle_java "server-jre"  "8" "31" "13";;
     oracle-jre-1.8.0_31) install_oracle_java "jre"  "8" "31" "13";;
     
     oracle-jdk-1.8.0_25) install_oracle_java "jdk"  "8" "25" "17";;
     oracle-server-jre-1.8.0_25) install_oracle_java "server-jre"  "8" "25" "17";;
     oracle-jre-1.8.0_25) install_oracle_java "jre"  "8" "25" "17";;
     
     oracle-jdk-1.8.0_20) install_oracle_java "jdk"  "8" "20" "26";;
     oracle-server-jre-1.8.0_20) install_oracle_java "server-jre"  "8" "20" "26";;
     oracle-jre-1.8.0_20) install_oracle_java "jre"  "8" "20" "26";;
     
     oracle-jdk-1.8.0_11) install_oracle_java "jdk"  "8" "11" "12";;
     oracle-server-jre-1.8.0_11) install_oracle_java "server-jre"  "8" "11" "12";;
     oracle-jre-1.8.0_11) install_oracle_java "jre"  "8" "11" "12";;
     
     oracle-jdk-1.8.0_05) install_oracle_java "jdk"  "8" "5" "13";;
     oracle-server-jre-1.8.0_05) install_oracle_java "server-jre"  "8" "5" "13";;
     oracle-jre-1.8.0_05) install_oracle_java "jre"  "8" "5" "13";;
     
     oracle-jdk-1.8.0) install_oracle_java "jdk"  "8" "" "132";;
     oracle-server-jre-1.8.0) install_oracle_java "server-jre"  "8" "" "132";;
     oracle-jre-1.8.0) install_oracle_java "jre"  "8" "" "132";;
     
     oracle-jdk-1.7.0_80) install_oracle_java "jdk"  "7" "80" "15";;
     oracle-server-jre-1.7.0_80) install_oracle_java "server-jre"  "7" "80" "15";;
     oracle-jre-1.7.0_80) install_oracle_java "jre"  "7" "80" "15";;
     
     oracle-jdk-1.7.0_79) install_oracle_java "jdk"  "7" "79" "15";;
     oracle-server-jre-1.7.0_79) install_oracle_java "server-jre"  "7" "79" "15";;
     oracle-jre-1.7.0_79) install_oracle_java "jre"  "7" "79" "15";;
     
     oracle-jdk-1.7.0_75) install_oracle_java "jdk"  "7" "75" "13";;
     oracle-server-jre-1.7.0_75) install_oracle_java "server-jre"  "7" "75" "13";;
     oracle-jre-1.7.0_75) install_oracle_java "jre"  "7" "75" "13";;
     
     oracle-jdk-1.7.0_72) install_oracle_java "jdk"  "7" "72" "14";;
     oracle-server-jre-1.7.0_72) install_oracle_java "server-jre"  "7" "72" "14";;
     oracle-jre-1.7.0_72) install_oracle_java "jre"  "7" "72" "14";;
     
     oracle-jdk-1.7.0_71) install_oracle_java "jdk"  "7" "71" "14";;
     oracle-server-jre-1.7.0_71) install_oracle_java "server-jre"  "7" "71" "14";;
     oracle-jre-1.7.0_71) install_oracle_java "jre"  "7" "71" "14";;
     
     oracle-jdk-1.7.0_67) install_oracle_java "jdk"  "7" "67" "01";;
     oracle-server-jre-1.7.0_67) install_oracle_java "server-jre"  "7" "67" "01";;
     oracle-jre-1.7.0_67) install_oracle_java "jre"  "7" "67" "01";;
     
     oracle-jdk-1.7.0_65) install_oracle_java "jdk"  "7" "65" "17";;
     oracle-server-jre-1.7.0_65) install_oracle_java "server-jre"  "7" "65" "17";;
     oracle-jre-1.7.0_65) install_oracle_java "jre"  "7" "65" "17";;
     
     oracle-jdk-1.7.0_60) install_oracle_java "jdk"  "7" "60" "19";;
     oracle-server-jre-1.7.0_60) install_oracle_java "server-jre"  "7" "60" "19";;
     oracle-jre-1.7.0_60) install_oracle_java "jre"  "7" "60" "19";;
     
     oracle-jdk-1.7.0_55) install_oracle_java "jdk"  "7" "55" "13";;
     oracle-server-jre-1.7.0_55) install_oracle_java "server-jre"  "7" "55" "13";;
     oracle-jre-1.7.0_55) install_oracle_java "jre"  "7" "55" "13";;
     
     oracle-jdk-1.7.0_51) install_oracle_java "jdk"  "7" "51" "13";;
     oracle-server-jre-1.7.0_51) install_oracle_java "server-jre"  "7" "51" "13";;
     oracle-jre-1.7.0_51) install_oracle_java "jre"  "7" "51" "13";;
     
     oracle-jdk-1.7.0_45) install_oracle_java "jdk"  "7" "45" "18";;
     oracle-server-jre-1.7.0_45) install_oracle_java "server-jre"  "7" "45" "18";;
     oracle-jre-1.7.0_45) install_oracle_java "jre"  "7" "45" "18";;
     
     oracle-jdk-1.7.0_40) install_oracle_java "jdk"  "7" "40" "43";;
     oracle-server-jre-1.7.0_40) install_oracle_java "server-jre"  "7" "40" "43";;
     oracle-jre-1.7.0_40) install_oracle_java "jre"  "7" "40" "43";;
     
     oracle-jdk-1.7.0_25) install_oracle_java "jdk"  "7" "25" "15";;
     oracle-jre-1.7.0_25) install_oracle_java "jre"  "7" "25" "15";;

     oracle-jdk-1.7.0_21) install_oracle_java "jdk"  "7" "21" "11";;
     oracle-jre-1.7.0_21) install_oracle_java "jre"  "7" "21" "11";;
     
     oracle-jdk-1.7.0_17) install_oracle_java "jdk"  "7" "17" "02";;
     oracle-jre-1.7.0_17) install_oracle_java "jre"  "7" "17" "02";;
     
     oracle-jdk-1.7.0_15) install_oracle_java "jdk"  "7" "15" "03";;
     oracle-jre-1.7.0_15) install_oracle_java "jre"  "7" "15" "03";;
     
     oracle-jdk-1.7.0_13) install_oracle_java "jdk"  "7" "13" "20";;
     oracle-jre-1.7.0_13) install_oracle_java "jre"  "7" "13" "20";;
     
     oracle-jdk-1.7.0_11) install_oracle_java "jdk"  "7" "11" "21";;
     oracle-jre-1.7.0_11) install_oracle_java "jre"  "7" "11" "21";;
     
     oracle-jdk-1.7.0_10) install_oracle_java "jdk"  "7" "10" "18";;
     oracle-jre-1.7.0_10) install_oracle_java "jre"  "7" "10" "18";;
     
     oracle-jdk-1.7.0_9) install_oracle_java "jdk"  "7" "9" "05";;
     oracle-jre-1.7.0_9) install_oracle_java "jre"  "7" "9" "05";;
     
     oracle-jdk-1.7.0_7) install_oracle_java "jdk"  "7" "7" "10";;
     oracle-jre-1.7.0_7) install_oracle_java "jre"  "7" "7" "10";;
     
     oracle-jdk-1.7.0_6) install_oracle_java "jdk"  "7" "6" "24";;
     oracle-jre-1.7.0_6) install_oracle_java "jre"  "7" "6" "24";;
     
     oracle-jdk-1.7.0_5) install_oracle_java "jdk"  "7" "5" "06";;
     oracle-jre-1.7.0_5) install_oracle_java "jre"  "7" "5" "06";;
     
     oracle-jdk-1.7.0_4) install_oracle_java "jdk"  "7" "4" "20";;
     oracle-jre-1.7.0_4) install_oracle_java "jre"  "7" "4" "20";;
     
     oracle-jdk-1.7.0_3) install_oracle_java "jdk"  "7" "3" "04";;
     oracle-jre-1.7.0_3) install_oracle_java "jre"  "7" "3" "04";;
     
     oracle-jdk-1.7.0_2) install_oracle_java "jdk"  "7" "2" "13";;
     oracle-jre-1.7.0_2) install_oracle_java "jre"  "7" "2" "13";;
     
     oracle-jdk-1.7.0_1) install_oracle_java "jdk"  "7" "1" "08";;
     oracle-jre-1.7.0_1) install_oracle_java "jre"  "7" "1" "08";;
     
     oracle-jdk-1.7.0) install_oracle_java "jdk"  "7" "" "";;
     oracle-jre-1.7.0) install_oracle_java "jre"  "7" "" "";;

     azul-zulu-1.8.0_45) install_azul_zulu "2015-04-8.7-bin" "1.8.0_45" "8.7.0.5";;
     azul-zulu-1.8.0_40) install_azul_zulu "2015-03-8.6-bin" "1.8.0_40" "8.6.0.1";;
     azul-zulu-1.8.0_31) install_azul_zulu "2015-01-8.5-bin" "1.8.0_31" "8.5.0.1";;
     azul-zulu-1.8.0_25) install_azul_zulu "2014-10-8.4-bin" "1.8.0_25" "8.4.0.1";;
     azul-zulu-1.8.0_20) install_azul_zulu "2014-09-8.3-bin" "1.8.0_20" "8.3.0.1";;
     azul-zulu-1.8.0_20) install_azul_zulu "2014-09-8.3-bin" "1.8.0_20" "8.3.0.1";;
     azul-zulu-1.8.0_11) install_azul_zulu "2014-07-8.2-bin" "1.8.0_11" "8.2.0.1";;
     azul-zulu-1.8.0_05) install_azul_zulu "2014-05-8.1-bin" "1.8.0_05" "8.1.0.6";;
     azul-zulu-1.8.0) install_azul_zulu "2014-04-8.0-bin" "1.8.0" "8.0.0.3";;
     azul-zulu-1.7.0_80) install_azul_zulu "2015-04-8.7-bin" "1.7.0_80" "7.10.0.1";;
     azul-zulu-1.7.0_79) install_azul_zulu "2015-04-8.7-bin" "1.7.0_79" "7.9.0.2";;
     azul-zulu-1.7.0_76) install_azul_zulu "2015-01-8.5-bin" "1.7.0_76" "7.8.0.3";;
     azul-zulu-1.7.0_72) install_azul_zulu "2014-10-8.4-bin" "1.7.0_72" "7.7.0.1";;
     azul-zulu-1.7.0_65) install_azul_zulu "2014-07-8.2-bin" "1.7.0_65" "7.6.0.1";;
     azul-zulu-1.7.0_60) install_azul_zulu "2014-06-7.5-bin" "1.7.0_60" "7.5.0.1";;
     azul-zulu-1.7.0_55) install_azul_zulu "2014-05-8.1-bin" "1.7.0_55" "7.4.0.5";;
     azul-zulu-1.7.0_51) install_azul_zulu "2014-03-7.3-bin" "1.7.0_51" "7.3.0.4";;
     azul-zulu-1.6.0_59) install_azul_zulu "2015-01-8.5-bin" "1.6.0_59" "6.7.0.2";;
     azul-zulu-1.6.0_56) install_azul_zulu "2014-10-8.4-bin" "1.6.0_56" "6.6.0.1";;
     azul-zulu-1.6.0_53) install_azul_zulu "2014-07-8.2-bin" "1.6.0_53" "6.5.0.2";;
     azul-zulu-1.6.0_53) install_azul_zulu "2014-07-8.2-bin" "1.6.0_53" "6.5.0.2";;
     azul-zulu-1.6.0_49) install_azul_zulu "2014-05-8.1-bin" "1.6.0_49" "6.4.0.6";;
     azul-zulu-1.6.0_47) install_azul_zulu "2014-03-7.3-bin" "1.6.0_47" "6.3.0.3";;
     azul-zulu-1.6.0_42) install_azul_zulu "2014-01-7.2-bin" "1.6.0_42" "6.2.0.9";;
   esac
done
IFS=$tmp_ifs

}

install_maven_versions() {
if [ -z $REQUIRED_MAVEN_VERSIONS ]; then
  echo "No Maven versions specified."
fi

tmp_ifs=$IFS
IFS=","
for maven_version in $REQUIRED_MAVEN_VERSIONS; do
  install_maven "${maven_version}"
done

IFS=$tmp_ifs
}

# ###########################################################################
# Main
# ###########################################################################

print_env
init
install_java_runtimes
install_maven_versions
