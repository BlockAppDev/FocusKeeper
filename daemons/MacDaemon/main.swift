#!/usr/bin/swift
import Cocoa
let INACTIVE_SECONDS = 5.0 * 60
let EMIT_INTERVAL = 5.0
var d = Keylogger()
d.start()

var prev_count: Int = count
var prevLoc = NSEvent.mouseLocation
var inactive = false
var lastWindow = activeApp
var last_action = NSDate().timeIntervalSince1970
var window_start = NSDate().timeIntervalSince1970
var last_event_emit = NSDate().timeIntervalSince1970

DispatchQueue.global(qos: .background).async {
    
    while(true) {
        let curr_time = NSDate().timeIntervalSince1970
        
        // when user presses keys
        if (prev_count != count) {
            last_action = curr_time
            prev_count = count
        }
        
        // when user moves mouse
        let currLoc = NSEvent.mouseLocation
        if (currLoc.x != prevLoc.x || currLoc.y != prevLoc.y) {
            last_action = curr_time;
            prevLoc = currLoc
        }
        
        // when active application is changed
        if (lastWindow != activeApp) {
            if (!inactive) {
                print(String(Int(curr_time - window_start)) + ":",activeApp)
            }
            window_start = curr_time
            last_event_emit = curr_time
            lastWindow = activeApp
        }
        if (curr_time - last_action > INACTIVE_SECONDS && !inactive) {
            print(String(Int(curr_time - window_start - INACTIVE_SECONDS)) + ":",activeApp)
            inactive = !inactive
            last_event_emit = curr_time
        }
        
        if (curr_time - last_action < INACTIVE_SECONDS && inactive) {
            inactive = !inactive
            window_start = curr_time
        }
        
        if (curr_time - last_event_emit >= EMIT_INTERVAL && !inactive) {
            print(String(Int(curr_time - window_start)) + ":", activeApp)
            last_event_emit = curr_time
            window_start = curr_time
        }
        usleep(10000)
    }
}

RunLoop.main.run()
