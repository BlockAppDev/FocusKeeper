import React, { Component } from 'react';
import "inter-ui/inter.css";
import "./settings.css";
import ItemList from "./list";
import $ from "jquery";

class NewBlockList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            items: [],
            adding_item: false
        };
    }

    addListItem() {
        if(this.state.adding_item) {
            return;
        }

        this.state.adding_item = true;
        this.state.items.unshift({
            bgcolor: "rgba(211, 211, 211)",
            active: true,
            icon_size: "23px",
            name: <input id="add-list-input"></input>,
            onCheck: () => {
                this.state.items.shift();
                let index = this.state.items.length;
                let tag = Math.random();
                let new_item = {
                    bgcolor: "#B8E986",
                    tag: tag,
                    active: true,
                    name: document.getElementById("add-list-input").value,
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

                this.forceUpdate();
            }
        })
        
        this.forceUpdate();
    }

    render() {
        return (
            <div id="settings" style={{position: "relative"}}>
                <span id="settings-text">Settings</span>
                <div id="settings-body">
                    <span className="new-list-text">New Block List</span>
                    <input id="new-list-name" placeholder="List Name" style={{marginTop: "10px"}}></input>
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
                                }
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