from datetime import datetime
from time import sleep
import time 

try:
    from AppKit import NSWorkspace, NSEvent, NSKeyDown
except ImportError:
    print "Can't import AppKit -- this script will be unable to run"
    exit(1)

#global variables
inactiveSeconds = 5
emitInterval = 5
seconds = time.time()
workspace = NSWorkspace.sharedWorkspace()
active_app = workspace.activeApplication()['NSApplicationName']

# checks for user's mouse movement
prevMouseLoc = NSEvent.mouseLocation()
def mouseMoved():
    curMouseLoc = NSEvent.mouseLocation()
    if (curMouseLoc.x != prevMouseLoc.x or curMouseLoc.y != prevMouseLoc.y):
            curMouseLoc = prevMouseLoc
            return True
    return False

# checks for user's key presses 
def checkKeyPress() :
    count = 0
    if (NSKeyDown):
        count += 1
    return count

# function to format time 
def hms_string(sec_elapsed):
    h = int(sec_elapsed / (60 * 60))  
    m = int((sec_elapsed % (60 * 60)) / 60)
    s = sec_elapsed % 60.
    return "{}:{:>02}:{:>05.2f}".format(h, m, s)

# track applications used 

def getFocusedApp(seconds, active_app):
    #seconds = time.time()
    print(seconds, active_app)
    appChanged = False
    prev_app = active_app
    #sleep(5)
    active_app = workspace.activeApplication()['NSApplicationName']

    if prev_app != active_app:
        seconds = time.time()
        # need to print a better way to output time in seconds
        print(seconds, active_app)
        appChanged = True
    return appChanged


def main():
    lastAction = time.time()
    windowStart = time.time()
    lastEventEmit = time.time()
    getFocusedApp(seconds,active_app)

    inactive = False
    while True:
        currTime = time.time()
        num = checkKeyPress()
       
       # if movement occured, user is active
        if (num > 0):
            lastAction = currTime
        if (mouseMoved()):
            lastAction = currTime
       
        windowChanged = getFocusedApp(seconds, active_app)
        if (windowChanged):
            if (not inactive):
                getFocusedApp(currTime-windowStart, active_app)
            windowStart = currTime
            lastEventEmit = currTime

        # if user inactive 
        if (currTime - lastAction > inactiveSeconds and not inactive):
            getFocusedApp(currTime-windowStart-inactiveSeconds, active_app)
            inactive = not inactive
            lastEventEmit = currTime

        # user active
        if (currTime - lastAction < inactiveSeconds and inactive):
            inactive = not inactive
            windowStart = currTime

        if (currTime - lastEventEmit >= emitInterval and not inactive):
            getFocusedApp(currTime-windowStart, active_app)
            lastEventEmit   = currTime
            windowStart = currTime

        sleep(0.10)

main()
