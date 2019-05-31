export function minutesToTime(mins) {
    let hours = Math.floor(mins / 60);
    let ampm = "AM";

    if(hours === 0) {
        hours = 12;
    }
    else if(hours === 24) {
        hours = 12;
        mins = 0;
    }
    else if(hours > 11) {
        ampm = "PM";
        hours -= 12;
    }

    let displayMins = mins % 60 + "";
    if(displayMins.length < 2) {
        displayMins = "0" + displayMins;
    }

    return hours + ":" + displayMins + " " + ampm;
}