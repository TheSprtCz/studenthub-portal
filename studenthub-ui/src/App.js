import React, { Component } from 'react';
import AppRouter from './AppRouter.js';

class App extends Component {
  render() {
    return (
      <div className="container">
        <div className="content">
          <AppRouter />
        </div>
      </div>
    );
  }
}

export default App;
