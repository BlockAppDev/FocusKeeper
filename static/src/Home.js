import React, { Component } from 'react';
import { BrowserRouter, Route, Switch, withRouter} from 'react';
import "./home.css";
import "@fortawesome/fontawesome-free/css/all.css"

class Home extends Component {
  constuctor() {
    this.routeChange = this.routeChange.bind(this);
  }

	handleOnClick = () => {
    let path = '/NewBlockList';
    this.props.history.push(path);
  }

  render() {
    let data = [
      {"seconds": 100 * 300, "focused": true, "name": "VSCode"},
      {"seconds": 23 * 300, "focused": false, "name": "Netflix"},
      {"seconds": 32 * 300, "focused": true, "name": "Piazza"}
    ];

    colorizeData(data);
    sortData(data);

    return (
      <div id="home">
        <div id="home-header">
          <input type="checkbox" id="focus-box"></input>
          <span id="focus-mode-text">Focus Mode</span>
          <i className="fas fa-cog icon" id="settings-cog" onClick={this.handleOnClick}></i>
          <hr></hr>
        </div>
        <div id="icon-holder">
          <i className="fas fa-chart-pie icon"></i>
          <i className="fas fa-chart-bar icon"></i>
        </div>
        <div id="date-selector">
          <span>Today</span>
          <span>Past Week</span>
          <span>Custom</span>
        </div>
        <div id="data-vis">
          <div id="pie-chart-container">
            { renderPieChart(250, 250, data) }
          </div>
        </div>

        <div id="recently-used">
          <span id="recently-used-text">Recently Used</span>
          { renderRecents(data) }
        </div>
      </div>
    )
  }
}

function renderRecents(data) {
  let recents = [];

  for(let item of data) {
    recents.push(getRecentText(item));
  }

  return recents;
}

function getRecentText(item) {
  return (
    <span key={Math.random()} className="recent-items">
      <i className="fas fa-circle" style={{color: item.color}} key={Math.random()}></i>
      <span>{item.name}</span>
      <span className="recent-items-time">{secondsToHours(item.seconds)}</span>
    </span>)
}

function colorizeData(data) {
  let colors = ["#00FF92", "#0076FF"];
  for(let i = 0; i < data.length; i++) {
    let item = data[i];
    if(item.focused) {
      data[i].color = colors.pop();
    }
    else {
      data[i].color = "#F0F0F0"
    }
  }

  return data;
}

function sortData(data) {
  data.sort((a, b) => {
    if(a.focused === b.focused) {
      return b.seconds - a.seconds;
    }
    return b.seconds - a.seconds;
  });
}

function polarToCartesian(centerX, centerY, radius, angleInDegrees) {
  var angleInRadians = (angleInDegrees-90) * Math.PI / 180.0;

  return {
    x: centerX + (radius * Math.cos(angleInRadians)),
    y: centerY + (radius * Math.sin(angleInRadians))
  };
}

function describeArc(x, y, radius, startAngle, endAngle){

  var start = polarToCartesian(x, y, radius, endAngle);
  var end = polarToCartesian(x, y, radius, startAngle);

  var largeArcFlag = endAngle - startAngle <= 180 ? "0" : "1";

  var d = [
      "M", start.x, start.y, 
      "A", radius, radius, 0, largeArcFlag, 0, end.x, end.y
  ].join(" ");

  return d;       
}

function secondsToHours(seconds) {
  let text = "";
  let hours = Math.floor(seconds / 3600);
  let minutes = Math.floor((seconds % 3600) / 60);
  if(hours > 0) {
    text += hours + "h ";
  }
  if(minutes > 0) {
    text += minutes + "m";
  }

  return text;
}

function getCircle(diam, total, previous, item) {
  let radius = diam / 2;
  let stroke_width = diam / 10;

  let degree_width = item.seconds / total * 360;

  return [previous + degree_width, <path d={describeArc(radius, radius, radius - stroke_width, previous, previous + degree_width)} fill="none" strokeWidth={stroke_width} stroke={item.color} key={Math.random()}></path>]
}

function renderPieChart(height, width, data) {
  let total_time = 0;
  let total_focus = 0;
  
  for(let item of data) {
    total_time += item.seconds;
    if(item.focused) {
      total_focus += item.seconds;
    }
  }

  let circles = [];
  let previous = 0;
  for(let item of data) {
    let result = getCircle(height, total_time, previous, item);
    previous = result[0];
    circles.push(result[1]);
  }

  let start_y = 80;
  let text_x = width / 2 - 60;
  return (<div>
    <svg height={height} width={width}>
      {circles}
      <text x={text_x} y={start_y}>In Focus</text>
      <text x={text_x} y={start_y + 30} fontSize="2em" fontWeight="bold">{ secondsToHours(total_focus) }</text>

      <text x={text_x} y={start_y + 60}>Total Screentime</text>
      <text x={text_x} y={start_y + 90} fontSize="2em" fontWeight="bold">{ secondsToHours(total_time) }</text>
    </svg>
  </div>);
}

export default Home;
