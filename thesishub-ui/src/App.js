import React, { Component } from 'react';
import { Router, Route, Switch } from 'react-router-dom';

import history from './common/History';
import ScrollToTop from './common/ScrollToTop';

import Topics from './views/Topics';
import NoMatch from './views/NoMatch';

class App extends Component {
  render() {
    return (
      <Router history={history}>
        <ScrollToTop>
          <div id="react-app">

            <Switch>
              <Route exact path="/" component={Topics}/>
              <Route component={NoMatch}/>
            </Switch>
          </div>
        </ScrollToTop>
      </Router>
    );
  }
}

export default App;
