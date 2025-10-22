@echo off
setlocal

:: ==========================================================================
:: This script sets up your environment file (.env) for the project.
::
:: How it works:
::   - Prompts you to choose between Cassandra ("c") or Postgres ("p").
::   - Copies the corresponding environment file from the root folder to
::     the working directory as ".env".
::
:: Required files:
::   - c.env  : Cassandra environment variables
::   - p.env  : Postgres environment variables
::   Both files must exist in the parent folder of this script.
::
:: File naming:
::   - c.env -> used when you type "c"
::   - p.env -> used when you type "p"
::
:: Benefits:
::   - Ensures you always have the correct environment configuration
::     for the database you are using.
::   - Reduces the chance of errors from manually renaming or copying
::     environment files.
::   - Makes it easier to switch between databases during development.
::
:: Example usage:
::   Run the script and enter "c" or "p" when prompted.
:: ==========================================================================


:: Ask user for input
:ask
set /p choice=Enter "c" for Cassandra or "p" for Postgres:

:: Convert to lowercase just in case
set choice=%choice:~0,1%
set choice=%choice:"=%

:: Determine which file to copy
if /i "%choice%"=="c" (
    if exist "..\c.env" (
        copy /y "..\c.env" "..\.env"
        echo Copied ..\c.env to ..\.env
    ) else (
        echo ..\c.env not found!
    )
    goto end
)

if /i "%choice%"=="p" (
    if exist "..\p.env" (
        copy /y "..\p.env" "..\.env"
        echo Copied ..\p.env to ..\.env
    ) else (
        echo ..\p.env not found!
    )
    goto end
)

echo Invalid input. Please enter "c" or "p".
goto ask

:end
