import React, { Component } from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import Home from './Home.js';
import MainMenu from './MainMenu.js';
import Settings from './Settings.js';
import './App.css';
import './index.css';
import { Dropdown, DropdownToggle, DropdownMenu, DropdownItem } from 'reactstrap';



class App extends Component {

  render() {
    
    return (
      <BrowserRouter>
        <Switch>
          <Route exact path="/" component={Home} />
          <Route path="/menu" component={MainMenu} />
          <Route path="/settings" component={Settings} />
        </Switch>
      </BrowserRouter>
    );
  }
}

export default App;
