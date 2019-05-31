import React, { Component } from 'react';
import "inter-ui/inter.css";
import "./settings.css";
import ItemList from "./list";

class NewBlockList extends Component {
    constructor(props) {
        super(props);

        this.state = {
            name: undefined,
            items: [],
            adding_item: false,
            editing: false
        };

        let edit_list = props.location.edit_list;
        if(edit_list) {
            this.state.editing = true;
            this.state.edit_index = props.location.edit_index;
            edit_list.items.forEach(elem => {
                this.addListItem(elem);
            })
            this.state.name = edit_list.name;
            this.state.edit_name = edit_list.name;
        }
    }

    addListItem(provided_text) {
        if(this.state.adding_item && !provided_text) {
            return;
        }

        this.state.adding_item = true;

        let new_item = {
            bgcolor: "rgba(211, 211, 211)",
            active: true,
            icon_size: "23px",
            name: <input id="add-list-input"></input>,
            onCheck: () => {
                if(!provided_text) {
                    this.state.items.shift();
                }

                let tag = Math.random();
                let new_item = {
                    bgcolor: "#B8E986",
                    tag: tag,
                    active: true,
                    name: provided_text || document.getElementById("add-list-input").value,
                    onRemove: () => {
                        for(let i = 0; i < this.state.items.length; i++) {
                            let remItem = this.state.items[i];
                            if(remItem.tag === tag) {
                                this.state.items.splice(i, 1);
                            }
                        }
                    }
                }
                this.state.items.unshift(new_item);
                this.state.adding_item = false;

                if(!provided_text) {
                    this.forceUpdate();
                }
            }
        }

        if(!provided_text) {
            this.state.items.unshift(new_item);
            this.forceUpdate();
        }
        else {
            new_item.onCheck();
        }
    }

    render() {
        return (
            <div id="settings" style={{position: "relative"}}>
                <span id="settings-text">Settings</span>
                <div id="settings-body">
                    <span className="new-list-text">{this.state.editing ? "Edit": "New"} Block List</span>
                    <input id="new-list-name" defaultValue={this.state.name} placeholder="List Name" style={{marginTop: "10px"}}></input>
                </div>
                <div id="list-container" style={{paddingLeft: "21px", paddingTop: "10px"}}>
                    <ItemList name="List Items" items={this.state.items} height="170px"></ItemList>
                    <span className="add-new-item" onClick={() => this.addListItem()}>+ Add item</span>
                </div>
                <div id="settings-footer">
                    <span
                        className="footer-text"
                        style={{paddingRight: "30px"}}
                        onClick={() => {
                            this.props.history.push({
                                pathname: "/settings",
                                new_list: {
                                    name: document.getElementById("new-list-name").value,
                                    items: this.state.items
                                },
                                edit_name: this.state.edit_name
                            });
                        }}>Apply Changes</span>
                    <span
                        className="footer-text"
                        onClick={() => this.props.history.push("/settings")}>Cancel</span>
                </div>
            </div>
        )
    }
}

export default NewBlockList;