@echo off
rem
rem Loom start script.
rem
rem Author: Peter Donald [peter at realityforge.org]
rem
rem Environment Variable Prequisites
rem
rem   LOOM_OPTS       (Optional) Java runtime options used when the command is
rem                      executed.
rem
rem   LOOM_TMPDIR     (Optional) Directory path location of temporary directory
rem                      the JVM should use (java.io.tmpdir).  Defaults to
rem                      $LOOM_BASE/temp.
rem
rem   JAVA_HOME          Must point at your Java Development Kit installation.
rem
rem   LOOM_JVM_OPTS   (Optional) Java runtime options used when the command is
rem                       executed.
rem
rem -----------------------------------------------------------------------------

rem
rem Determine if JAVA_HOME is set and if so then use it
rem
if not "%JAVA_HOME%"=="" goto found_java

set LOOM_JAVACMD=java
goto file_locate

:found_java
set LOOM_JAVACMD=%JAVA_HOME%\bin\java

:file_locate

rem
rem Locate where loom is in filesystem
rem
if not "%OS%"=="Windows_NT" goto start

rem %~dp0 is name of current script under NT
set LOOM_HOME=%~fp0

rem : operator works similar to make : operator
set LOOM_HOME=%LOOM_HOME:\bin\run.bat=%

:start

if not "%LOOM_HOME%" == "" goto LOOM_home

echo.
echo Error: LOOM_HOME environment variable is not set.
echo   This needs to be set manually for Win9x as its command
echo   prompt scripting does not allow it to be set automatically.
echo.
goto end

:LOOM_home

if not "%LOOM_TMPDIR%"=="" goto afterTmpDir
set LOOM_TMPDIR=%LOOM_HOME%\temp
if not exist "%LOOM_TMPDIR%" mkdir "%LOOM_TMPDIR%"

:afterTmpDir

echo Using LOOM_HOME:   %LOOM_HOME%
echo Using LOOM_TMPDIR: %LOOM_TMPDIR%
echo Using JAVA_HOME:      %JAVA_HOME%

set LOOM_SM=

if "%LOOM_SECURE%" == "false" goto postSecure

rem Make Loom run with security Manager enabled
set LOOM_SM="-Djava.security.manager"

:postSecure

rem
rem -Djava.ext.dirs= is needed as some JVM vendors do foolish things
rem like placing jaxp/jaas/xml-parser jars in ext dir
rem thus breaking Loom
rem

rem uncomment to get enable remote debugging
rem set DEBUG=-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y

rem Kicking the tires and lighting the fires!!!
"%LOOM_JAVACMD%" %DEBUG% "-Djava.ext.dirs=%LOOM_HOME%\lib;%LOOM_HOME%\tools\lib" "-Dloom.home=%LOOM_HOME%" "-Djava.security.policy=jar:file:%LOOM_HOME%/bin/loom-launcher.jar!/META-INF/java.policy" %LOOM_JVM_OPTS% %LOOM_SM% -jar "%LOOM_HOME%\bin\loom-launcher.jar" %1 %2 %3 %4 %5 %6 %7 %8 %9

:end
