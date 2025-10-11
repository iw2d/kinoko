@echo off
setlocal

REM Kills the default ports on Windows cuz wallahi I'm done trying to find out which processes are taking up my ports.

REM List of ports
set PORTS=8282 8585 8586 8587 8588 8589

for %%P in (%PORTS%) do (
    echo Checking port %%P...
    for /f "tokens=5" %%A in ('netstat -aon ^| findstr :%%P') do (
        echo Killing PID %%A on port %%P
        taskkill /F /PID %%A
    )
)

echo Done!
pause
