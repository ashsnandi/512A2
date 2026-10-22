#!/bin/bash

#TODO set this to where your code and jar file root dir is
BASEDIR=$HOME/comp512/p2

#TODO update your group number here in place of XX
group=xx

#TODO Optional
# this will always generate the same game island. Change the last digits to any number if you want to change it to a different island map. Otherwise leave it as it is.
gameid=game-$group-99

#TODO edit these entries to put the name of the server that you are using and the associated ports.
# Remember to start the script from this host
export autotesthost=tr-open-01.cs.mcgill.ca
# player1 -> process 1, player 2 -> process 2, etc .. add more depending on how many players are playing.
# Script automatically counts the variables to figure out the number of players.
export process1=${autotesthost}:401$group
export process2=${autotesthost}:402$group
export process3=${autotesthost}:403$group
#export process4=${autotesthost}:404$group
#export process5=${autotesthost}:405$group
#export process6=${autotesthost}:406$group
#export process7=${autotesthost}:407$group
#export process8=${autotesthost}:408$group
#export process9=${autotesthost}:409$group

#TODO update these values as needed
maxmoves=100 interval=100 randseed=xxxxxxxxx
#TODO IF (and only if) you want to simulate failures, enable this for corresponding player numbers.
#export failmode_N=RECEIVEPROPOSE
#export failmode_N=AFTERSENDVOTE
#export failmode_N=AFTERSENDPROPOSE
#export failmode_N=AFTERBECOMINGLEADER
#export failmode_N=AFTERVALUEACCEPT
#For example this enabled failmode AFTERBECOMINGLEADER for player/process 2 (only one failmode can be set per process). It is important to have the export.
export failmode_2=AFTERBECOMINGLEADER

# Check if this script is being exectuted on the correct server.
if [[ $autotesthost != $(hostname) ]]
then
	echo "Error !! This script is configured to run from $autotesthost, but you are trying to run this script on $(hostname)."
	exit 10
fi

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

# set your classpath to include the gcl tar files and the parent path to the comp512st directory.
export CLASSPATH=$BASEDIR/comp512p2.jar:$BASEDIR

# Build the process group string.
export processgroup=$(env | grep '^process[1-9]=' | sort | sed -e 's/.*=//')
processgroup=$(echo $processgroup | sed -e 's/ /,/g')

# Total number of players
numplayers=$(echo $processgroup | awk -F',' '{ print NF}')
echo "There are $numplayers players in the setup"

# We do not need colors to track.
export TIMONOCHROME=true
# We do not want the island display to get constantly refreshed.
export UPDATEDISPLAY=false

playernum=1
for process in $(echo $processgroup | sed -e 's/,/ /g')
do
	failmode=$(env | grep '^failmode_'${playernum}'=' | sed -e 's/.*=//')
	echo java comp512st.tests.TreasureIslandAppAuto $process $processgroup $gameid $numplayers $playernum $maxmoves $interval $randseed$playernum $failmode '>' $gameid-$playernum-display.log
	java comp512st.tests.TreasureIslandAppAuto $process $processgroup $gameid $numplayers $playernum $maxmoves $interval $randseed$playernum $failmode  > $gameid-$playernum-display.log 2>&1 &
	playernum=$(expr $playernum + 1);
done
wait
