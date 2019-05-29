import React, { Component } from 'react';

class ThingyList extends Component {
    constructor(props) {
        super(props);
        this.state = {items: []};
    }

    render() {
        this.state.items.push({text: "Work", bgcolor: "green"});
        this.state.items.push({text: "Play", bgcolor: "green"});
        this.state.items.push({text: "Fun", bgcolor: "green"});

        return <div>
            <span><b>Block Lists</b></span>
            <div style={{"width": "300px"}}>
                { this.renderListElements() }
            </div>
        </div>
    }

    renderListElements() {
        let items = [];
        for(let i = 0; i < this.state.items.length; i++) {
            let item = this.state.items[i];
            items.push(
            <div style={{backgroundColor: "rgba(0.5, 0.5, 0.5, 0.1)", borderRadius: "5px", padding: "3px 3px 3px 3px", marginBottom: "5px"}}>
                <span>{item.text}</span>
            </div>)
        }

        return items;
    }
}

export default ThingyList;