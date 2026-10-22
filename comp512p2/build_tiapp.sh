#!/bin/bash

#TODO Edit this dir name to where your comp512.jar and build_tiapp.sh script are.
BASEDIR=$HOME/comp512/p2

if [[ ! -d $BASEDIR ]]
then
	echo "Error $BASEDIR is not a valid dir."
	exit 1
fi

if [[ ! -f $BASEDIR/comp512p2.jar ]]
then
	echo "Error cannot locate $BASEDIR/comp512p2.jar . Make sure it is present."
	exit 1
fi

if [[ ! -d $BASEDIR/comp512st ]]
then
	echo "Error cannot locate $BASEDIR/comp512st directory . Make sure it is present."
	exit 1
fi

export CLASSPATH=$BASEDIR/comp512p2.jar:$BASEDIR
cd $BASEDIR

javac $(find -L comp512st -name '*.java')

