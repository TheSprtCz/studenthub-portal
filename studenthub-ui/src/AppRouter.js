import React from 'react';
import {
  BrowserRouter as Router,
  Route,
  Redirect
} from 'react-router-dom';
import Auth from './Auth.js';
import NavBar from './components/NavBar.js'
import LoginView from './views/LoginView.js';
import HomeView from './views/HomeView.js';
import OrgsView from './views/OrgsView.js';
import UnisView from './views/UnisView.js';
import UsersView from './views/UsersView.js';
import MyTopicsView from './views/MyTopicsView.js';
import ProfileView from './views/ProfileView.js';
import TopicDetailView from './views/TopicDetailView.js';

const PrivateRoute = ({ component, ...rest }) => (
  <Route {...rest} render={props => (
    Auth.isAuthenticated() ? (
      React.createElement(component, props)
    ) : (
      <Redirect to={{
        pathname: '/login',
        state: { from: props.location }
      }}/>
    )
  )}/>
)

const AppRouter = () => (
  <Router>
    <div id="router">
      <NavBar />
      <Route path="/" exact component={HomeView} />
      <Route path="/topics/:id" component={TopicDetailView}/>

      <PrivateRoute path="/my-topics" component={MyTopicsView}/>
      <PrivateRoute path="/users" component={UsersView}/>
      <PrivateRoute path="/universities" component={UnisView}/>
      <PrivateRoute path="/organisations" component={OrgsView}/>

      <Route path="/login" component={LoginView}/>
      <PrivateRoute path="/profile" component={ProfileView}/>
    </div>
  </Router>
)

export default AppRouter
