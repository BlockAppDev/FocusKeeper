import React, { Component } from 'react';
import "./list.css";
import "inter-ui/inter.css";

class ItemList extends Component {
    constructor(props) {
        super(props);
        this.state = {items: []};
        if(props.items) {
            this.state.items = props.items;
        }
    }

    render() {
        return <div style={{width: "300px"}}>
            <span style={{fontSize: "22px", whiteSpace: "nowrap"}}><b>{this.props.name}</b></span>
            <div style={{maxHeight: this.props.height}}className="list-container">
                { this.renderListElements(this.props.items) }
            </div>
        </div>
    }

    renderListElements(render_items) {
        let items = [];
        for(let i = 0; i < render_items.length; i++) {
            let item = render_items[i];
            let bgcolor = item.bgcolor;
            if(!item.active) {
                bgcolor = "rgba(211, 211, 211)";
            }

            let checkIconClass = "fas fa-times list-icon inactive-icon"
            if(item.active && !item.onRemove) {
                checkIconClass = "fas fa-check list-icon active-icon"
            }

            items.push(
            <div className="list-item" style={{backgroundColor: bgcolor}} key={i}>
                <span style={{paddingLeft: "5px"}}>{item.name}</span>
                <i title="Edit"
                   className="far fa-edit list-icon"
                   style={{display: item.onEdit ? "inline-block": "none"}}
                   onClick={() => item.onEdit()}></i>
                <i title="Active"
                   className={checkIconClass}
                   style={{fontSize: item.icon_size}}
                   onClick={() => {
                       item.active = !item.active;
                       if(item.onRemove) {
                           item.onRemove();
                       }
                       if(item.onCheck) {
                           item.onCheck();
                       }
                       this.forceUpdate();
                   }}></i>
            </div>)
        }

        if(!items.length) {
            return <span>None yet!</span>
        }

        return items;
    }
}

export default ItemList;