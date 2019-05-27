import React, { Component } from 'react';
import "./home.css";
import "@fortawesome/fontawesome-free/css/all.css";
import "inter-ui/inter.css";
import $ from "jquery";

let SERVER_ADDR = "http://localhost:8000"
let endpoints = {
  stats: SERVER_ADDR + "/stats"
};

let mouse = {x: 0, y: 0};

class HoverBox extends Component {
  render() {
    if (this.props.item) {
      return (<div id="hover-info" className="gtext" style={{top: mouse.y + 10, left: mouse.x + 10}}>
        <b><span>{this.props.item ? this.props.item.name : ""}</span></b>
        <div></div>
        <span>{this.props.item ? secondsToHours(this.props.item.seconds) : ""}</span>
      </div>)
    }
    return null;
  }
}

class Home extends Component {
  constructor(props) {
    super(props);
    this.state = {
      focused: false,
      hover_item: null,
      chart_type: "pie",
      data: [{"name": "none", "seconds": 1}]
    }

    this.colors = {};

    this.refreshData();
    window.setInterval(() => this.refreshData(), 5000);

    window.addEventListener("mousemove", (event) => {
      mouse.x = event.clientX;
      mouse.y = event.clientY;

      let hover_div = document.getElementById("hover-info");
      if(hover_div) {
        hover_div.style.top = mouse.y + 10 + "px";
        hover_div.style.left = mouse.x + 10 + "px";
      }
    });

    window.setInterval(function() {
      let elem = document.elementFromPoint(mouse.x, mouse.y);
      if(elem.tagName !== "rect" && elem.tagName !== "path" && this.state.hover_item != null) {
        this.setState({hover_item: null});
      }
    }.bind(this), 50);
  }

  refreshData() {
    $.getJSON(endpoints.stats, {start: getDate(), end: getDate()}, (response) => {
      this.setState({data: response});
    })
  }

	handleOnClick = () => {
    let path = '/NewBlockList';
    this.props.history.push(path);
  }

  handleChartClick = (data) => {
    if (this.state.chart_type === "pie") {
        return this.renderPieChart(250, 250, data);
    } else if (this.state.chart_type === "bar") {
        return this.renderBarChart(250, 250, data);
      }
  }

  getBar(maxWidth, maxHeight, numItems, max_time, previous, item) {
    let width = maxWidth / (numItems * 1.2);
    let startX = 0;
    let leftAlign = maxWidth/2 - (numItems*width/2);
    let rect_height = item.seconds / max_time * (maxHeight - 80);

    if (previous === 0) {
      startX = leftAlign;
    } else {
      startX = previous + 3;
    }

    return [startX + width, <rect
      key = {Math.random()}
      rx = {5}
      ry = {5}
      onMouseEnter={() => {this.setState({hover_item: item})}}
      onMouseLeave={() => {this.setState({hover_item: null})}}
      x={startX}
      y={maxHeight - rect_height}
      width={width}
      height={rect_height}
      style={{fill: item.color}} />]
  }

  renderBarChart(height, width, data) {
    let total_time = 0;
    let total_focus = 0;
    let bars = [];
    let previous = 0;
  
    let results = getTimeDetails(total_time, total_focus, data);
    total_time = results[0];
    total_focus = results[1];
  

    let len = Math.min(data.length, 20);
  
    for (let i = 0; i < len; i++) {
      let result = this.getBar(width, height, len, data[0].seconds, previous, data[i]);
      previous = result[0];
      bars.push(result[1]);
    }
    let start_y = 40;
    let text_x = 0;
    return (<div>
      <svg height={height} width={width}>
        {bars}
        <text x={text_x} y={start_y} className="gtext">In Focus</text>
        <text x={text_x} y={start_y + 30} fontSize="24px" fontWeight="bold">{ secondsToHours(total_focus) }</text>
  
        <text x={text_x + 120} y={start_y} className="gtext">Total Screentime</text>
        <text x={text_x + 120} y={start_y + 30} fontSize="24px" fontWeight="bold">{ secondsToHours(total_time) }</text>
      </svg>
    </div>);
  
  }

  getCircle(diam, total, previous, item, max_circle) {
    let radius = diam / 2;
    let stroke_width = diam / 10;

    let degree_width = item.seconds / total * max_circle;

    return [previous + degree_width, <path onMouseEnter={() => {this.setState({hover_item: item})}} 
      onMouseLeave={() => {this.setState({hover_item: null})}} 
      d={describeArc(radius, radius, radius - stroke_width, previous, previous + degree_width)} 
      fill="none" strokeWidth={stroke_width} stroke={item.color} key={Math.random()}></path>]
  }

  renderPieChart(height, width, data) {
    let total_time = 0;
    let total_focus = 0;
    let spacing = 1.5;

    let results = getTimeDetails(total_time, total_focus, data);
    total_time = results[0];
    total_focus = results[1];

    let max_circle = 360 - spacing * data.length;
    let circles = [];
    let previous = 0;
    for(let item of data) {
      let result = this.getCircle(height, total_time, previous, item, max_circle);
      previous = result[0] + 1.5;
      circles.push(result[1]);
    }
  
    let start_y = 80;
    let text_x = width / 2 - 60;
    return (<div>
      <svg height={height} width={width}>
        {circles}
        <text x={text_x} y={start_y} className="gtext">In Focus</text>
        <text x={text_x} y={start_y + 30} fontSize="33px" fontWeight="bold">{ secondsToHours(total_focus) }</text>

        <text x={text_x} y={start_y + 60} className="gtext">Total Screentime</text>
        <text x={text_x} y={start_y + 90} fontSize="33px" fontWeight="bold">{ secondsToHours(total_time) }</text>
      </svg>
    </div>);
  }

  colorizeData(data) {
    let colors = ["#00FF92", "#0076FF"];

    for(let i = 0; i < data.length; i++) {
      let item = data[i];
      if(item.focused) {
        let curr_color = colors.pop();
        data[i].color = curr_color;
        colors.splice(0, 0, curr_color);
      }
      else {
        data[i].color = "#F0F0F0"
      }
    }
  
    return data;
  }

  render() {
    this.colorizeData(this.state.data);
    sortData(this.state.data);

    return (
      <div id="home">
        <HoverBox item={this.state.hover_item}></HoverBox>
        <div id="home-header"
          onClick={() => this.setState({focused: !this.state.focused})}
          style={{backgroundColor: this.state.focused ? "#358562": "white"}}>
          <input type="checkbox" id="focus-box" checked={this.state.focused}></input>
          <span id="focus-mode-text" style={{color: this.state.focused ? "white" : "black"}}>Focus Mode</span>
          <i className="fas fa-cog icon" id="settings-cog" style={{color: this.state.focused ? "white" : "black"}} onClick={this.handleOnClick}></i>
          <hr></hr>
        </div>
        <div id="icon-holder">
          <i className="fas fa-chart-pie icon"
             onClick={() => {this.setState({chart_type: "pie"})}}
             style={{color: this.state.chart_type === "pie" ? "#4A90E2" : null, 
              opacity: this.state.chart_type === "pie" ? 1.0 : null}} ></i>
          <i className="fas fa-chart-bar icon"
             onClick={() => {this.setState({chart_type: "bar"})}}
             style={{color: this.state.chart_type === "bar" ? "#4A90E2" : null, 
              opacity: this.state.chart_type === "bar" ? 1.0 : null}}></i>
        </div>
        <div id="date-selector" className="gtext">
          <span>Today</span>
          <span>Past Week</span>
          <span>Custom</span>
        </div>
        {  <div id="data-vis">
          <div id="pie-chart-container">
            { this.handleChartClick(this.state.data) }
          </div>
        </div>  }

        <div id="recently-used" className="gtext">
          <span id="recently-used-text">Recently Used</span>
          { renderRecents(this.state.data) }
        </div>
      </div>
    )
  }
}

function renderRecents(data) {
  let recents = [];

  let recent_max = Math.min(3, data.length);
  for(let i = 0; i < recent_max; i++) {
    recents.push(getRecentText(data[i]));
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
 
function getTimeDetails(total_time, total_focus, data) {
    for(let item of data) {
    	total_time += item.seconds;
    	if(item.focused) {
      		total_focus += item.seconds;
    	}
  	}
  	return [total_time, total_focus];
}

function getDate() {
  // format YYYY/MM/DD
  let date = new Date();
  let date_string = date.getFullYear() + "";
  let month = date.getMonth();
  if(month.length == 1) {
    month = "0" + month;
  }
  let day = date.getDate();
  if(day.length == 1) {
    day = "0" + day;
  }
  
  return date_string + month + day;
}

export default Home;
