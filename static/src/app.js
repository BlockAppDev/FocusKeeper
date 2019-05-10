import React, { Component } from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';

import Home from './home.js';
import MainMenu from './MainMenu.js';
import Settings from './Settings.js';
import NewBlockList from './NewBlockList.js';
import "inter-ui/inter.css";


class App extends Component {

  render() {
    
    return (
      <BrowserRouter>
        <Switch>
          <Route exact path="/" component={Home} />
          <Route path="/menu" component={MainMenu} />
          <Route path="/settings" component={Settings} />
          <Route path="/newblocklist" component={NewBlockList} />
        </Switch>
      </BrowserRouter>
    );
  }
}

export default App;
