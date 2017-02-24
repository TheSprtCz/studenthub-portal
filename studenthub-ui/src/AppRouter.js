import React from 'react';
import {
  BrowserRouter as Router,
  Route,
  NavLink,
  Redirect,
  withRouter
} from 'react-router-dom';
import Auth from './Auth.js';
import TopicTable from './components/TopicTable.js';
import LoginForm from './components/LoginForm.js';
import UsersTable from './components/UsersTable.js';
import MyTopics from './components/MyTopics.js';

// we need to re-render NavBar when route changes
const NavBar = withRouter(() => (
    <nav className="navbar navbar-default navbar-fixed-top">
    <div className="container">
      <div className="navbar-header">
        <button type="button" className="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
          <span className="icon-bar"></span>
          <span className="icon-bar"></span>
          <span className="icon-bar"></span>
        </button>
        <NavLink activeClassName="active" to="/" className="navbar-brand"><img src="/img/logo.jpg" alt="Student Hub" /></NavLink>
      </div>
      <div className="collapse navbar-collapse" id="myNavbar">
        <ul className="nav navbar-nav navbar-right text-uppercase">
          <li><NavLink activeClassName="active" to="/my-topics">My Topics</NavLink></li>
          {Auth.hasRole("ADMIN") || Auth.hasRole("COMPANY_REP") ? <li><NavLink activeClassName="active" to="/users">Users</NavLink></li> : ''}
          {Auth.hasRole("ADMIN") ? <li><NavLink activeClassName="active" to="/unis">Universities</NavLink></li> : ''}
          {Auth.hasRole("ADMIN") ? <li><NavLink activeClassName="active" to="/companies">Companies</NavLink></li> : ''}
          <li><NavLink activeClassName="active" to="/login"><AuthButton/></NavLink></li>
          <li><NavLink activeClassName="active" to="/profile">Protected</NavLink></li>
        </ul>
      </div>
    </div>
    </nav>
))

// 'push' from router.history
const AuthButton = withRouter(({ push }) => (
  Auth.isAuthenticated() ? (
    <span>
      Welcome! <button onClick={() => {
        Auth.signout(() => push('/'))
      }}>Sign out</button>
    </span>
  ) : (
    <span>Sign In.</span>
  )
))

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

/*
 Application "Views" (pages)
*/

// title page
class HomeView extends React.Component {
  render() {
    return (
      <div>
        <h2>Topics</h2>
        <TopicTable />
      </div>
    )
  }
}

// my-topics page
class MyTopicsView extends React.Component {
  render() {
    return (
      <div>
        <MyTopics />
      </div>
    )
  }
}

// user administration space
class UsersView extends React.Component {
  render() {
    return (
      <div>
        <UsersTable />
      </div>
    )
  }
}

// we need to pass router props
const LoginFormRouter = withRouter(LoginForm);

// login page
class LoginView extends React.Component {
  render() {
    return (
      <div>
        <LoginFormRouter />
      </div>
    )
  }
}

// profile page
class ProfileView extends React.Component {
  render() {
    return (
      <div>
        <h2>Protected Profile page!</h2>
      </div>
    )
  }
}

// profile page
class TopicDetailView extends React.Component {
  render() {
    return (
      <div>
        <TopicDetailView />
      </div>
    )
  }
}

const AppRouter = () => (
  <Router>
    <div id="router">
      <NavBar />
      <Route path="/" exact component={HomeView} />
      <PrivateRoute path="/my-topics" component={MyTopicsView}/>
      <Route path="/users" component={UsersView}/>
      <Route path="/login" component={LoginView}/>
      <Route path="/topics/:id" component={TopicDetailView}/>
      <PrivateRoute path="/profile" component={ProfileView}/>
    </div>
  </Router>
)

export default AppRouter
