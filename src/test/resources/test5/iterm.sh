#!/bin/bash
tell application "iTerm"
    activate
    set myterm to (make new terminal)
    tell myterm
        launch session "Default"
        tell the last session
           write text "$@"
        end tell
    end tell
end tell