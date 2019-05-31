import React, { Component } from 'react';
import "inter-ui/inter.css";
import "./settings.css";
import ItemList from "./list";
import {minutesToTime} from "./util";
import $ from "jquery";

function capitalize(str) {
    str = str.toLowerCase();
    return str.charAt(0).toUpperCase() + str.slice(1);
}

let WEEKDAYS = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"];

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

        if(this.props.location.new_scheduled) {
            let new_scheduled = this.props.location.new_scheduled;

            for(let i = 0; i < 7; i++) {
                if(new_scheduled.days[i]) {
                    json.schedule.days[WEEKDAYS[i]].push({
                        start: new_scheduled.start,
                        end: new_scheduled.end,
                        lists: new_scheduled.lists
                    })
                }
            }

            this.props.location.new_scheduled = null;
        }

        // Check for passed in new list
        if(this.props.location.new_list) {
            let new_list = this.props.location.new_list;
            new_list.active = true;
            new_list.items = new_list.items.map((e) => e.name);

            if(this.props.location.edit_name) {
                delete json.blockLists[this.props.location.edit_name];
            }

            json.blockLists[new_list.name] = new_list;

            this.props.location.new_list = null;
        }

        let blockLists = Object.values(json.blockLists);
        for(let blockList of blockLists) {
            blockList.bgcolor = "#B8E986";
            // eslint-disable-next-line no-loop-func
            blockList.onEdit = () => {
                this.props.history.push({
                    pathname: "/newblocklist",
                    edit_list: blockList
                });
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
                    <span
                        className="add-new-item"
                        onClick={() => {
                            this.props.history.push({
                                pathname: "/newscheduledblock",
                                block_lists: this.state.block_lists
                            })
                        }}>+ Schedule new block
                    </span>
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