#!/bin/bash

#JAVA_HOME=/usr/local/j2sdk1.4.1
#MAVEN_HOME=~bwalding/maven
#ANT_HOME=$HOME/cvs/ant/dist
PATH=$HOME/bin:$JAVA_HOME/bin:$MAVEN_HOME/bin:$ANT_HOME/bin:$PATH
#CVS_RSH=$HOME/ssh1.sh
#CVSROOT=:ext:$USER@cvs.codehaus.org:/cvsroot/picocontainer

export JAVA_HOME
export MAVEN_HOME
export ANT_HOME
export PATH
export CVS_RSH
export CVSROOT

#mailto=osi@pobox.com
mailto=jcontainer-interest@lists.codehaus.org
builddir=.
logfile=cleanbuild.log
name=Loom

cd $builddir

# Delete compiled local copies to start fresh each time
rm -Rf ~/.maven/repository/loom/jars

# Compile and test
rm $logfile
maven | tee $logfile

# See if the "compiling" file is there. If it is, compilation
# failed.
if grep "BUILD SUCCESSFUL" $logfile ; then
      echo "$name rebuild passed, emailing list"
      tail $logfile | mail -s "[PASS] $name compilation." $mailto
else
      echo "$name clean failed, emailing list"
      cat $logfile | mail -s "[FAIL] $name compilation." $mailto
fi
