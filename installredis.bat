@echo off
REM
 mvn install:install-file ^
    -Dfile=libs/RedisCache-1.0.jar ^
    -DgroupId=com.Ashutosh ^
    -DartifactId=RedisCache ^
    -Dversion=1.0 ^
    -Dpackaging=jar
 REM
 echo Installing complete
 REM


