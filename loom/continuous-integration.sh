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

# Clean old builds and make the target folder. Logs go here too.
rm -Rf target
mkdir target

# Delete compiled local copies to start fresh each time
rm -Rf ~/.maven/repository/loom/jars

# Compile and test
maven clean-all &> target/cleanbuild.log
maven build &> target/cleanbuild.log

# See if the "compiling" file is there. If it is, compilation
# failed.
if grep "BUILD FAILED" target/cleanbuild.log ; then
  # Mail Maven's output to the dev list.
  echo "Build failed, emailing list"
  cat target/cleanbuild.log | mutt -s "[BUILD] Clean build failed" $mailto
else
  # See if the "testfailure" file is there. If it is, tests failed.
  if [ -e "target/testfailure" ] ; then
    # Mail Maven's output to the dev list.
    cat target/cleanbuild.log | mutt -s "[BUILD] Test failure - see http://loom.jcontainer.org/junit-report.html" $mailto
  # else
    # Deploy site only if compile and tests pass. Logs currently not used.
    # Must be run separately to get the files uploaded in the proper dir
    # on the server.
    # maven jar:deploy &> target/jardeploy.log
    # maven dist:deploy &> target/distdeploy.log
  fi
  # We'll deploy the site even if the tests fail. Log currently not used.
  # maven site:deploy &> target/sitedeploy.log
fi
