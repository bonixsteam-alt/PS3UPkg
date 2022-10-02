@echo off

cd ../..
call mvn clean compile assembly:single

set mainDir=%cd%
cd target
setlocal enabledelayedexpansion
for %%A in (*.jar) do (
    set file=%%~nA.jar

    echo %mainDir%
    if exist !file! set "str=<launch4jConfig><dontWrapJar>false</dontWrapJar><headerType>console</headerType><jar>%mainDir%\target\!file!</jar><outfile>%mainDir%\ps3upkg.exe</outfile><errTitle></errTitle><cmdLine></cmdLine><chdir>.</chdir><priority>normal</priority><downloadUrl>http://java.com/download</downloadUrl><supportUrl></supportUrl><stayAlive>false</stayAlive><restartOnCrash>false</restartOnCrash><manifest></manifest><icon></icon><jre><path></path><bundledJre64Bit>false</bundledJre64Bit><bundledJreAsFallback>false</bundledJreAsFallback><minVersion>8</minVersion><maxVersion></maxVersion><jdkPreference>preferJre</jdkPreference><runtimeBits>64/32</runtimeBits></jre></launch4jConfig>"

    cd ..\build-files\win\
    echo !str! > ps3upkg.xml

    echo Output saved to ps3upkg.xml
)
endlocal

cd ..\build-files\win