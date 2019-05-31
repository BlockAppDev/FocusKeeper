import React, { Component } from 'react';
import { BrowserRouter, Route, Switch } from 'react-router-dom';

import Home from './home.js';
import Settings from './settings';
import NewBlockList from './new_block_list';
import NewScheduledBlock from "./new_scheduled_block";
import "inter-ui/inter.css";


class App extends Component {

  render() {
    
    return (
      <BrowserRouter>
        <Switch>
          <Route exact path="/" component={Home} />
          <Route path="/settings" component={Settings} />
          <Route path="/newblocklist" component={NewBlockList} />
          <Route path="/newscheduledblock" component={NewScheduledBlock} />
        </Switch>
      </BrowserRouter>
    );
  }
}

export default App;
