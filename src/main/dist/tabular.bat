echo off

setlocal

set JAVA_PATH=%JAVA_HOME%

if NOT "%TABULAR_JAVA_HOME%"=="" (
	echo Found TABULAR_JAVA_HOME variable, using that as the java path
	set JAVA_PATH=%TABULAR_JAVA_HOME%
)

set "_JAVACMD=%JAVA_PATH%\bin\java.exe"


rem Parses x out of 1.x; for example 8 out of java version 1.8.0_xx
rem Otherwise, parses the major version; 9 out of java version 9-ea
set JAVA_VERSION=0
for /f "tokens=3" %%g in ('%_JAVACMD% -Xms32M -Xmx32M -version 2^>^&1 ^| findstr /i "version"') do (
  set JAVA_VERSION=%%g
)
set JAVA_VERSION=%JAVA_VERSION:"=%
for /f "delims=.-_ tokens=1-2" %%v in ("%JAVA_VERSION%") do (
  if /I "%%v" EQU "1" (
    set JAVA_VERSION=%%w
  ) else (
    set JAVA_VERSION=%%v
  )
)

if NOT "%JAVA_VERSION%"=="8" (
    echo Incompatible Java version found %JAVA_VERSION%, must be Java 8
	echo Either set your JAVA_HOME variable to a version of Java 8
	echo Or, set a TABULAR_JAVA_HOME variable to a version of Java 8
) else (
	%_JAVACMD% -Dlog4j.configuration=file:log4j.xml %* -jar tabular.jar
)	

endlocal