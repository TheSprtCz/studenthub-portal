#!/bin/bash
#================================================================
# HEADER
#================================================================
#% SYNOPSIS
#+    ${SCRIPT_NAME} start/migrate/run [-c [file]]
#+    ${SCRIPT_NAME} -b args
#%
#% DESCRIPTION
#%    This script provides access to most used features on server 
#%    without having to specify too many things 
#%
#% COMMANDS
#%    run                           Only runs server
#%    migrates                      Migrates Database
#%    start                         Runs and migrates database
#%
#% OPTIONS
#%    -c [file], --config=[file]    Specify config to use
#%    -b, --bypass                  Bypass script and put all arguments to the server, ignore all other arguments
#%    -h, --help                    Print this help
#%
#% EXAMPLES
#%    ${SCRIPT_NAME} -o DEFAULT arg1 arg2
#%
#================================================================
# END_OF_HEADER
#================================================================

# Needed variables
SCRIPT_HEADSIZE=$(head -200 ${0} |grep -n "^# END_OF_HEADER" | cut -f1 -d:)
SCRIPT_NAME="studenthub" # scriptname without path
JAR_FILE="studenthub-portal-1.0-SNAPSHOT.jar"
CONFIG="config.yml"
PASS=false

# Usage function
usage() { head -${SCRIPT_HEADSIZE:-99} ${0} | grep -e "^#[%+-]" | sed -e "s/^#[%+-]//g" -e "s/\${SCRIPT_NAME}/${SCRIPT_NAME}/g" ; }

# 
case $1 in
    -b|--bypass)
    PASS=true
    shift
    ;;
    start)
		if [ -z "$CMD" ]
    then
      CMD="start"
		fi    
		shift
    ;;
    migrate)
		if [ -z "$CMD" ]
    then
      CMD="migrate"
		fi    
    shift 
    ;;
    run)
		if [ -z "$CMD" ]
    then
      CMD="run"
		fi
    shift
    ;;
    *)
    usage 1>&2 && exit 1
    ;;
esac

if $PASS ; then
  # Pass all remaining arguments to JAR_FILE
  java -jar $JAR_FILE ${@:1}
else
  if ! [ $# -eq 0 ] ;  then
    case $1 in
        -c|--config)
        CONFIG="$2"
        shift
        ;;
        *)
        usage 1>&2 && exit 1
        ;;
    esac
  fi

  # Run server
  case $CMD in
    run)
    java -jar $JAR_FILE server $CONFIG
		shift
    ;;
    migrate)
    java -jar $JAR_FILE db migrate $CONFIG
    shift
    ;;
    start)
    java -jar $JAR_FILE db migrate $CONFIG
    java -jar $JAR_FILE server $CONFIG
    shift
    ;;
    *)
    usage 1>&2 && exit 1
    ;;
  esac
fi
