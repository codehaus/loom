#! /bin/sh
#
# -----------------------------------------------------------------------------
# Loom start script.
#
# Author: Alexis Agahi
#         Peter Donald
#                                                                         
# Environment Variable Prequisites
#
#   LOOM_OPTS       (Optional) Java runtime options used when the command is
#                      executed.
#
#   LOOM_TMPDIR     (Optional) Directory path location of temporary directory
#                      the JVM should use (java.io.tmpdir).  Defaults to
#                      $LOOM_BASE/temp.
#
#   JAVA_HOME          Must point at your Java Development Kit installation.
#
#   LOOM_JVM_OPTS   (Optional) Java runtime options used when the command is
#                       executed.
#
#   LOOM_KILLDELAY  (Optional) When shutting the server this script sends s
#                      SIGTERM signal then delays for a time before forcefully
#                      shutting down the process if it is still alive. This
#                      variable controls the delay and defaults to 5 (seconds)
#
# -----------------------------------------------------------------------------

usage()
{
    echo "Usage: $0 {start|stop|run|restart|check}"
    exit 1
}

[ $# -gt 0 ] || usage

##################################################
# Get the action & configs
##################################################

ACTION=$1
shift
ARGS="$*"

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
case "`uname`" in
CYGWIN*) cygwin=true;;
esac

# resolve links - $0 may be a softlink
THIS_PROG="$0"

while [ -h "$THIS_PROG" ]; do
  ls=`ls -ld "$THIS_PROG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    THIS_PROG="$link"
  else
    THIS_PROG=`dirname "$THIS_PROG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$THIS_PROG"`
LOOM_HOME=`cd "$PRGDIR/.." ; pwd`

#setup time between signals to kill loom
if [ -z "$LOOM_KILLDELAY" ] ; then
  LOOM_KILLDELAY=5
fi

unset THIS_PROG

if [ -r "$LOOM_HOME"/bin/setenv.sh ]; then
  . "$LOOM_HOME"/bin/setenv.sh
fi

# Checking for JAVA_HOME is required on *nix due
# to some distributions stupidly including kaffe in /usr/bin
if [ "$JAVA_HOME" = "" ] ; then
  echo "ERROR: JAVA_HOME not found in your environment."
  echo
  echo "Please, set the JAVA_HOME variable in your environment to match the"
  echo "location of the Java Virtual Machine you want to use."
  exit 1
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$LOOM_TMPDIR" ] && LOOM_TMPDIR=`cygpath --unix "$LOOM_TMPDIR"`
fi

if [ -z "$LOOM_TMPDIR" ] ; then
  # Define the java.io.tmpdir to use for Loom
  LOOM_TMPDIR="$LOOM_HOME"/temp
  mkdir -p "$LOOM_TMPDIR"
fi

# Uncomment to get enable remote debugging
# DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y"
#
# Command to overide JVM ext dir
#
# This is needed as some JVM vendors do foolish things
# like placing jaxp/jaas/xml-parser jars in ext dir
# thus breaking Loom
#
JVM_EXT_DIRS="$LOOM_HOME/lib:$LOOM_HOME/tools/lib"

LOOM_ENDORSED="$LOOM_HOME/lib/endorsed"
LOOM_LAUNCHER="$LOOM_HOME/bin/loom-launcher.jar"

#####################################################
# Find a PID for the pid file
#####################################################
if [  -z "$LOOM_PID" ]
then
  LOOM_PID="$LOOM_TMPDIR/loom.pid"
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  LOOM_HOME=`cygpath --path --windows "$LOOM_HOME"`
  LOOM_TMPDIR=`cygpath --path --windows "$LOOM_TMPDIR"`
  LOOM_ENDORSED=`cygpath --path --windows "$LOOM_ENDORSED"`
  JVM_EXT_DIRS=`cygpath --path --windows "$JVM_EXT_DIRS"`
  LOOM_LAUNCHER=`cygpath --path --windows "$LOOM_LAUNCHER"`
  LOOM_TMPDIR=`cygpath --windows "$LOOM_TMPDIR"`
fi

# ----- Execute The Requested Command -----------------------------------------

echo "Using LOOM_HOME:      $LOOM_HOME"
echo "Using LOOM_TMPDIR:    $LOOM_TMPDIR"
echo "Using LOOM_ENDORSED:  $LOOM_ENDORSED"
echo "Using JAVA_HOME:      $JAVA_HOME"
echo "Using JVM_EXT_DIRS:   $JVM_EXT_DIRS"

JVM_OPTS="-Djava.ext.dirs=$JVM_EXT_DIRS -Djava.endorsed.dirs=$LOOM_ENDORSED"

if [ "$LOOM_SECURE" != "false" ] ; then
  # Make loom run with security manager enabled
  JVM_OPTS="$JVM_OPTS -Djava.security.manager"
fi

# Get the run cmd
RUN_CMD="$JAVA_HOME/bin/java \
    $JVM_OPTS \
    $DEBUG \
    -Djava.security.policy=jar:file:$LOOM_LAUNCHER!/META-INF/java.policy \
    $LOOM_JVM_OPTS \
    -Dloom.home="$LOOM_HOME" \
    -Djava.io.tmpdir="$LOOM_TMPDIR" \
    -jar "$LOOM_LAUNCHER" $*"

#####################################################
# Find a location for the loom console
#####################################################
LOOM_CONSOLE="$LOOM_TMPDIR/loom.console"
if [  -z "$LOOM_CONSOLE" ]
then
  if [ -w /dev/console ]
  then
    LOOM_CONSOLE=/dev/console
  else
    LOOM_CONSOLE=/dev/tty
  fi
fi

#####################################################
# Action!
#####################################################

case "$ACTION" in
  start)
        echo "Starting Loom: "

        if [ -f $LOOM_PID ]
        then
            echo "Already Running!!"
            exit 1
        fi

        echo "STARTED Loom `date`" >> $LOOM_CONSOLE

        nohup sh -c "exec $RUN_CMD >>$LOOM_CONSOLE 2>&1" >/dev/null &
        echo $! > $LOOM_PID
        echo "Loom running pid="`cat $LOOM_PID`
        ;;

  stop)
        PID=`cat $LOOM_PID 2>/dev/null`
        echo "Shutting down Loom: $PID"
        kill $PID 2>/dev/null
        sleep $LOOM_KILLDELAY
        kill -9 $PID 2>/dev/null
        rm -f $LOOM_PID
        echo "STOPPED `date`" >>$LOOM_CONSOLE
        ;;

  restart)
        $0 stop $*
        sleep 5
        $0 start $*
        ;;

  supervise)
       #
       # Under control of daemontools supervise monitor which
       # handles restarts and shutdowns via the svc program.
       #
         exec $RUN_CMD
         ;;

  run|demo)
        echo "Running Loom: "

        if [ -f $LOOM_PID ]
        then
            echo "Already Running!!"
            exit 1
        fi

        exec $RUN_CMD
        ;;

  check)
        echo "Checking arguments to Loom: "
        echo "LOOM_HOME:     $LOOM_HOME"
        echo "LOOM_TMPDIR:   $LOOM_TMPDIR"
        echo "LOOM_ENDORSED: $LOOM_ENDORSED"
        echo "LOOM_LAUNCHER: $LOOM_LAUNCHER"
        echo "LOOM_JVM_OPTS: $LOOM_JVM_OPTS"
        echo "LOOM_PID:      $LOOM_PID"
        echo "JAVA_HOME:        $JAVA_HOME"
        echo "JVM_OPTS:         $JVM_OPTS"
        echo "CLASSPATH:        $CLASSPATH"
        echo "RUN_CMD:          $RUN_CMD"
        echo

        if [ -f $LOOM_PID ]
        then
            echo "Loom running pid="`cat $LOOM_PID`
            exit 0
        fi
        exit 1
        ;;

*)
        usage
        ;;
esac

exit 0
