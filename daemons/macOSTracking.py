from datetime import datetime
from time import sleep

try:
    from AppKit import NSWorkspace, NSEvent, NSKeyDown
except ImportError:
    print "Can't import AppKit -- this script will be unable to run"
    exit(1)

# checks for mouse movement
prevMouseLoc = NSEvent.mouseLocation()
def mouseMoved():
    curMouseLoc = NSEvent.mouseLocation()
    if (curMouseLoc.x != prevMouseLoc.x or curMouseLoc.y != prevMouseLoc.y):
            curMouseLoc = prevMouseLoc
            return True
    return False

# checks for key presses 
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
workspace = NSWorkspace.sharedWorkspace()
active_app = workspace.activeApplication()['NSApplicationName']
print('Active focus: ' + active_app)
currTime = datetime.now()
while True:
    sleep(1)
    
    prev_app = active_app
    active_app = workspace.activeApplication()['NSApplicationName']
    
    if prev_app != active_app:
        elapsedTime = currTime - datetime.now()
        seconds_elapsed = (elapsedTime).total_seconds()
        print('Time Elapsed: {}'.format(hms_string(seconds_elapsed)) + ' Focus changed to: ' + active_app)
        


