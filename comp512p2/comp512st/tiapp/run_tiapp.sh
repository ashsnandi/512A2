#!/bin/bash

if [[ $# -ne 1 ]]
then
	echo "Please pass the player number as the argument"
	echo "  $0 <playernum>"
	echo "  $0 3"
	exit 2
fi

# The player number is passed as the argument.
playernum=$1

#TODO set this to where your code and jar file root dir is
BASEDIR=$HOME/comp512/p2

#TODO update your group number here in place of XX
group=xx

#TODO Optional
# this will always generate the same game island. Change the last digits to any number if you want to change it to a different island map. Otherwise leave it as it is.
# MAKE SURE that every player has the same gameid.
gameid=game-$group-99

#TODO edit these entries to put the names of the servers (e.g., tr-open-01.cs.mcgill.ca) and port numbers that you are using.
# player1 -> process 1, player 2 -> process 2, etc .. add more depending on how many players are playing.
# Remember to start the scripts of corresponding players from the corresponding servers.
# comment out process3 if you are only playing 2 players, etc.
export process1=server1:401$group
export process2=server2:402$group
#export process3=server3:403$group
#export process4=server4:404$group
#export process5=server5:405$group
#export process6=server6:406$group
#export process7=server7:407$group
#export process8=server8:408$group
#export process9=server9:409$group

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

# set the classpath to include the gcl tar files and the parent path to the comp512st directory.
export CLASSPATH=$BASEDIR/comp512p2.jar:$BASEDIR

# Build the process group string.
export processgroup=$(env | grep '^process[1-9]=' | sort | sed -e 's/.*=//')
processgroup=$(echo $processgroup | sed -e 's/ /,/g')

# Total number of players
numplayers=$(echo $processgroup | awk -F',' '{ print NF}')

if [[ $playernum -gt $numplayers ]]
then
	echo "Error, you have only $numplayers processes configured for your group. Cannot allocate a process from $processgroup to player number $playernum"
	exit 3
fi

# Find out the process mapped to THIS player.
myprocess=$(echo $processgroup | awk -F',' -v playernum=$playernum '{ print $playernum }')

if [[ -z $myprocess ]]
then
	echo "Error, unable to allocate a process to this player from the group $processgroup"
	exit 4
fi

# Check if this script is being exectuted on the correct server.
myhost=${myprocess%:*}
if [[ $myhost != $(hostname) ]]
then
	echo "Error !! your player's process [$myprocess] is set to run from $myhost, but you are trying to run this script on $(hostname)."
	exit 10
fi

set -x
# Start the game instance for this player.
java comp512st.tiapp.TreasureIslandApp $myprocess $processgroup $gameid $numplayers $playernum
