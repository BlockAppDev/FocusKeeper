import React, { Component } from 'react';
import "inter-ui/inter.css";
import "./settings.css";
import ItemList from "./list";
import $ from "jquery";

function capitalize(str) {
    str = str.toLowerCase();
    return str.charAt(0).toUpperCase() + str.slice(1);
}

function minutesToTime(mins) {
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

class Settings extends Component {
    constructor(props) {
        super(props);

        this.state = {
            block_lists: [],
            scheduled_blocks: [],
            settings: null
        };


        $.get("http://localhost:8000/settings", this.loadSettings.bind(this));
    }

    updateSettings() {
        $.post("http://localhost:8000/settings", JSON.stringify(this.state.settings), function(response) {
            $.get("http://localhost:8000/settings", this.loadSettings.bind(this));
        }.bind(this))
    }

    loadSettings(json) {
        if(json === undefined) {
            json = this.state.settings;
        }
        else {
            this.state.settings = json;
        }

        // Check for passed in new list
        if(this.props.location.new_list) {
            let new_list = this.props.location.new_list;
            new_list.active = true;
            new_list.items = new_list.items.map((e) => e.name);
            json.blockLists[new_list.name] = new_list;
            this.props.location.new_list = null;
            console.log(new_list);
        }

        let blockLists = Object.values(json.blockLists);
        for(let blockList of blockLists) {
            blockList.bgcolor = "#B8E986";
            blockList.onEdit = function() {
                console.log(blockList);
            };
            blockList.onCheck = () => {
                this.state.settings.blockLists[blockList.name].active = !!blockList.active;
                this.loadSettings();
            }
        }

        let scheduled_blocks = [];
        for(let day in json.schedule.days) {
            for(let i = 0; i < json.schedule.days[day].length; i++) {
                let block = json.schedule.days[day][i];
                scheduled_blocks.push({
                    name: `${capitalize(day)} ${minutesToTime(block.start)}-${minutesToTime(block.end)}`,
                    bgcolor: "#B8E986",
                    active: true,
                    day: day,
                    onRemove: () => {
                        this.state.settings.schedule.days[day].pop(i);
                        this.loadSettings();
                    }
                })
            }
        }

        this.setState({block_lists: blockLists, scheduled_blocks: scheduled_blocks});
    }

    render() {
        return (
            <div id="settings" style={{position: "relative"}}>
                <span id="settings-text">Settings</span>
                <div id="list-container" style={{paddingLeft: "21px"}}>
                    <ItemList name="Block Lists" items={this.state.block_lists} height="100px"></ItemList>
                    <span className="add-new-item" onClick={() => this.props.history.push("/newblocklist")}>+ Create new list</span>
                </div>
                <div id="list-container" style={{paddingLeft: "21px", paddingTop: "30px"}}>
                    <ItemList name="Scheduled Blocks" items={this.state.scheduled_blocks} height="100px"></ItemList>
                    <span className="add-new-item">+ Schedule new block</span>
                </div>
                <div id="settings-footer">
                    <span
                        className="footer-text"
                        style={{paddingRight: "30px"}}
                        onClick={() => {
                            this.updateSettings();
                            this.props.history.push("/");
                        }}>Apply Changes</span>
                    <span
                        className="footer-text"
                        onClick={() => this.props.history.push("/")}>Cancel</span>
                </div>
            </div>
        )
    }
}

export default Settings;