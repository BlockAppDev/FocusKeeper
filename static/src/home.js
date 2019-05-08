import React, { Component } from 'react';
import "./home.css";
import "@fortawesome/fontawesome-free/css/all.css"

class Home extends Component {
  constructor(props) {
    super(props);
    this.state = {
      focused: false,
      x:0,
      y:0
    }
    this.hover_item = null;
    this.chart_type = "pie";

    window.addEventListener("mousemove", function(event) {
      this.setState({x: event.clientX, y: event.clientY});
    }.bind(this));
  }

	handleOnClick = () => {
    let path = '/NewBlockList';
    this.props.history.push(path);
  }

  handleChartClick = (data) => {
    if (this.chart_type === "pie") {
        return this.renderPieChart(250, 250, data);
    } else if (this.chart_type === "bar") {
        return this.renderBarChart(250, 250, data);
      }
  }

  _onMouseMove(e) {
    this.setState({x:e.screenX, y:e.screenY});
  }

  getBar(maxWidth, maxHeight, numItems, total_time, previous, item) {
    let width = maxWidth / (numItems*2);
    let startX = 0;
    let leftAlign = maxWidth/2 - (numItems*width/2);
    let rect_height = item.seconds / total_time * (maxHeight - 20);
    if (previous === 0) {
      startX = leftAlign;
    } else {
      startX = previous + 3; 
    }
    return [startX + width, <rect key = {Math.random()} rx = {5} ry = {5} onMouseEnter={() => {this.hover_item = item}} onMouseLeave={() => {this.hover_item = null}} x={startX} y={maxHeight - rect_height} width = {width} height = {rect_height} style={{fill: item.color}} />]
  }

  renderBarChart(height, width, data) {
    let total_time = 0;
    let total_focus = 0;
    let len = 0;
    let bars = [];
    let previous = 0;
  
    let results = getTimeDetails(total_time, total_focus, data);
    total_time = results[0];
    total_focus = results[1];
  
    if (data.length >= 20) {
      len = 20;
    } else {
      len = data.length;
    }
  
    for (let i = 0; i < len; i++) {
      let result = this.getBar(width, height, len, total_time, previous, data[i]);
      previous = result[0];
      bars.push(result[1]);
    }
    let start_y = 40;
    let text_x = 0;
    return (<div>
      <svg height={height} width={width}>
        {bars}
        <text x={text_x} y={start_y}>In Focus</text>
        <text x={text_x} y={start_y + 30} fontSize="1.5em" fontWeight="bold">{ secondsToHours(total_focus) }</text>
  
        <text x={text_x + 120} y={start_y}>Total Screentime</text>
        <text x={text_x + 120} y={start_y + 30} fontSize="1.5em" fontWeight="bold">{ secondsToHours(total_time) }</text>
      </svg>
    </div>);
  
  }
  getCircle(diam, total, previous, item) {
    let radius = diam / 2;
    let stroke_width = diam / 10;
  
    let degree_width = item.seconds / total * 360;
  
    return [previous + degree_width, <path onMouseEnter={() => {this.hover_item = item}} 
      onMouseLeave={() => {this.hover_item = null}} 
      d={describeArc(radius, radius, radius - stroke_width, previous, previous + degree_width)} 
      fill="none" strokeWidth={stroke_width} stroke={item.color} key={Math.random()}></path>]
  }
  renderPieChart(height, width, data) {
    let total_time = 0;
    let total_focus = 0;
    
    let results = getTimeDetails(total_time, total_focus, data);
    total_time = results[0];
    total_focus = results[1];
  
    let circles = [];
    let previous = 0;
    for(let item of data) {
      let result = this.getCircle(height, total_time, previous, item);
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
  onItem() {
    if (this.hover_item) {
      return <div id = "hover-info" style={{"display":true ? "visible" : "none", position:"absolute", "top": this.state.y, "left": this.state.x + 10, backgroundColor: "#F4F8FF"}} onMouseMove={this._onMouseMove.bind(this)}>
      <span>{this.hover_item ? this.hover_item.name : ""}</span>
      <div></div>
      <span>{this.hover_item ? secondsToHours(this.hover_item.seconds) : ""}</span>
    </div>
    }
    return null;
  }

  render() {
    let data = [
      {"seconds": 100 * 300 + 120 * 10, "focused": true, "name": "VSCode"},
      {"seconds": 23 * 300, "focused": false, "name": "Netflix"},
      {"seconds": 32 * 300, "focused": true, "name": "Piazza"}
    ];

    colorizeData(data);
    sortData(data);

    return (
      <div id="home">
        {this.onItem()}
        <div id="home-header"
          onClick={() => this.setState({focused: !this.state.focused})}
          style={{backgroundColor: this.state.focused ? "#358562": "white"}}>
          <input type="checkbox" id="focus-box" checked={this.state.focused}></input>
          <span id="focus-mode-text" style={{color: this.state.focused ? "white" : "black"}}>Focus Mode</span>
          <i className="fas fa-cog icon" id="settings-cog" style={{color: this.state.focused ? "white" : "black"}} onClick={this.handleOnClick}></i>
          <hr></hr>
        </div>
        <div id="icon-holder">
          <i className="fas fa-chart-pie icon" onClick={() => {this.chart_type = "pie"}} ></i>
          <i className="fas fa-chart-bar icon" onClick={() => {this.chart_type = "bar"}}></i>
        </div>
        <div id="date-selector">
          <span>Today</span>
          <span>Past Week</span>
          <span>Custom</span>
        </div>
        {  <div id="data-vis">
          <div id="pie-chart-container">
            { this.handleChartClick(data) }
          </div>
        </div>  }

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
 
function getTimeDetails(total_time, total_focus, data) {
    for(let item of data) {
    	total_time += item.seconds;
    	if(item.focused) {
      		total_focus += item.seconds;
    	}
  	}
  	return [total_time, total_focus];
}

export default Home;
