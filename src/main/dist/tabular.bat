echo off

set JAVA_PATH=%JAVA_HOME%

if NOT "%TABULAR_JAVA_HOME%"=="" (
	echo Found TABULAR_JAVA_HOME variable, using that as the java path
	set JAVA_PATH=%TABULAR_JAVA_HOME%
)

set "_JAVACMD=%JAVA_PATH%\bin\java.exe"
%_JAVACMD% -Dlog4j.configurationFile=log4j2.xml %* -jar tabular.jar
