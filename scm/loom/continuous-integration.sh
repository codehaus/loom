#!/bin/bash

PATH=$HOME/bin:$JAVA_HOME/bin:$MAVEN_HOME/bin:$ANT_HOME/bin:$PATH

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
mavenRepo=loom

cd $builddir

# Delete compiled local copies to start fresh each time
rm -Rf ~/.maven/repository/$mavenRepo/jars
rm -Rf ~/.maven/repository/$mavenRepo/plugins
rm -Rf ~/.maven/plugins/maven-sar-plugin-i*

# Compile and test
rm -f $logfile
maven 2>&1 | tee $logfile

if grep "BUILD SUCCESSFUL" $logfile ; then
      echo "$name build successful"
else
      echo "$name clean failed, emailing list"
      cat $logfile | mail -s "[FAIL] $name compilation." $mailto
fi
