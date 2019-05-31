import React, { Component } from 'react';
import "inter-ui/inter.css";
import "./settings.css";
import ItemList from "./list";

function minuteValue(value) {
    let split_val = value.split(":");
    return Number(split_val[0]) * 60 + Number(split_val[1]);
}

class NewScheduledBlock extends Component {
    constructor(props) {
        super(props);

        let passed_block_lists = props.location.block_lists;
        let new_block_lists = [];
        for(let block_list of passed_block_lists) {
            let new_list = {
                name: block_list.name,
                active: block_list.active,
                bgcolor: block_list.bgcolor,
                onCheck: () => {

                }
            }

            new_block_lists.push(new_list);
        }

        this.state = {
            days: [false, false, false, false, false, false, false],
            block_lists: new_block_lists
        };
    }

    renderWeekDays() {
        let days = [];
        let weekdays = ["Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"];
        for(let i = 0; i < 7; i++) {
            days.push(
                <div
                    className="weekday-block"
                    style={{
                        backgroundColor: this.state.days[i] ? "#B8E986": "rgb(211, 211, 211)",
                        boxShadow: this.state.days[i] ? "0px 0px 1px 1px rgba(72, 91, 53, 0.6)": "1px 1px 2px 2px rgba(0, 0, 0, 0.4)"
                    }}
                    onClick={() => {
                        this.state.days[i] = !this.state.days[i];
                        this.forceUpdate();
                    }}>
                    <span>{weekdays[i]}</span>
                </div>
            )
        }

        return days;
    }

    render() {
        return (
            <div className="outline" style={{position: "absolute"}}>
                <span id="settings-text">Settings</span>
                <div id="settings-body">
                    <span className="new-list-text" style={{display: "block"}}>Schedule New Block</span>
                    <div style={{display: "inline-block", marginLeft: "22px", paddingRight: "30px", paddingTop: "5px"}}>
                        <span>From</span>
                        <input type="time" className="time-select" id="start-time" required></input>
                    </div>
                    <div style={{display: "inline-block"}}>
                        <span>To</span>
                        <input type="time" className="time-select" id="end-time" required></input>
                    </div>
                    <div style={{marginLeft: "13px", marginTop: "10px"}}>
                        {this.renderWeekDays()}
                    </div>
                    <div style={{marginTop: "10px"}}>
                        <ItemList name="Applicable Block Lists" height="100px" items={this.state.block_lists}></ItemList>
                    </div>
                    <div id="settings-footer" style={{marginLeft: "45px"}}>
                        <span
                            className="footer-text"
                            style={{paddingRight: "30px"}}
                            onClick={() => {
                                this.props.history.push({
                                    pathname: "/settings",
                                    new_scheduled: {
                                        start: minuteValue(document.getElementById("start-time").value),
                                        end: minuteValue(document.getElementById("end-time").value),
                                        lists: this.state.block_lists.filter(elem => elem.active).map(elem => elem.name),
                                        days: this.state.days
                                    }
                                });
                            }}>Apply Changes</span>
                        <span
                            className="footer-text"
                            onClick={() => this.props.history.push("/settings")}>Cancel</span>
                    </div>
                </div>
            </div>
        )
    }
}

export default NewScheduledBlock;