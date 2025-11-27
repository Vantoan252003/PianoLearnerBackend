@echo off
REM Load environment variables from .env file
for /f "tokens=*" %%i in (.env) do set %%i

REM Run Spring Boot application
mvn spring-boot:run