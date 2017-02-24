import React from 'react';
import { NavLink } from 'react-router-dom';

function NavBar() {
  return (
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
          <li><NavLink activeClassName="active" to="/">Home</NavLink></li>
          <li><NavLink activeClassName="active" to="/my-topics">My Topics</NavLink></li>
          <li><NavLink activeClassName="active" to="/users">Users</NavLink></li>
          <li><NavLink activeClassName="active" to="/unis">Universities</NavLink></li>
          <li><NavLink activeClassName="active" to="/companies">Companies</NavLink></li>
          <li><NavLink activeClassName="active" to="/login">Sign In</NavLink></li>
        </ul>
      </div>
    </div>
    </nav>
  );
}

export default NavBar;
