#! /bin/sh
#
# -----------------------------------------------------------------------------
# Loom start script.
#

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

unset THIS_PROG

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$LOOM_HOME" ] && LOOM_HOME=`cygpath --unix "$LOOM_HOME"`
fi

$LOOM_HOME/bin/loom.sh run $*
