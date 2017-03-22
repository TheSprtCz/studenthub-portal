import React from 'react';
import Auth from '../Auth.js';
import { Link, NavLink, withRouter } from 'react-router-dom';

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
        <NavLink activeClassName="active" to="/" className="navbar-brand"><img src="/logotype.png" alt="Student Hub" /></NavLink>
      </div>
      <div className="collapse navbar-collapse" id="myNavbar">
        <ul className="nav navbar-nav navbar-right text-uppercase">
          {Auth.isAuthenticated() ? <li><NavLink activeClassName="active" to="/my-topics">My Topics</NavLink></li> : ''}
          {Auth.hasRole("ADMIN") || Auth.hasRole("COMPANY_REP") ? <li><NavLink activeClassName="active" to="/users">Users</NavLink></li> : ''}
          {Auth.hasRole("ADMIN") ? <li><NavLink activeClassName="active" to="/universities">Universities</NavLink></li> : ''}
          {Auth.hasRole("ADMIN") ? <li><NavLink activeClassName="active" to="/organisations">Organisations</NavLink></li> : ''}
          {/* <li><NavLink activeClassName="active" to="/login"><AuthButton/></NavLink></li> */}
          <li><Link to="/profile"><AuthButton/></Link></li>
        </ul>
      </div>
    </div>
    </nav>
))

// 'push' from router.history
const AuthButton = withRouter(({ history }) => (
  Auth.isAuthenticated() ? (
    <span>
      <i className="fa fa-user-circle" aria-hidden="true"></i>
      { Auth.getUserInfo().display_name }
    </span>
  ) : (
    <span>Sign In</span>
  )
))

export default NavBar;
