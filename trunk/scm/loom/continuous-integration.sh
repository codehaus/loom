#!/bin/bash

mailto=jcontainer-interest@lists.codehaus.org
#mailto=osi@pobox.com
builddir=.

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

cd $builddir

# Delete compiled local copies to start fresh each time
rm -Rf ~/.maven/repository/loom/jars

# Compile and test
rm target/cleanbuild.log
maven | tee target/cleanbuild.log

# See if the "compiling" file is there. If it is, compilation
# failed.
if grep "BUILD SUCCESSFUL" target/cleanbuild.log ; then
      echo "Rebuild passed, emailing list"
      tail target/cleanbuild.log | mail -s "[PASS] Loom compilation." $mailto
else
      echo "Clean failed, emailing list"
      cat target/cleanbuild.log | mail -s "[FAIL] Loom compilation." $mailto
fi
