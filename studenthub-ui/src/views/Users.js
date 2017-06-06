import React, { Component } from 'react';

import AdminUsersView from '../components/AdminUsersView.js';
import CompanyRepUsersView from '../components/CompanyRepUsersView.js';
import SiteSnackbar from '../components/SiteSnackbar.js';

import Auth from '../Auth.js';
import Util from '../Util.js';

import _t from '../Translations.js';

class Users extends Component {
  state = { users: [], companyId: -1, snackbarActive: false, snackbarLabel: "" }

  componentDidMount() {
    if (Auth.hasRole(Util.userRoles.admin))
      this.getUsers();
    else if (Auth.hasRole(Util.userRoles.companyRep))
      this.getCompanyUsers();
  }

  /**
   * Gets the list of all users.
   */
  getUsers = () => {
    fetch('/api/users', {
        method: 'get',
        credentials: 'same-origin'
      }).then(function(response) {
          if(response.ok) {
            return response.json();
        } else throw new Error('There was a problem with network connection.');
      }).then(function(json) {
        var newData = [];

        for(let i in json) {
          newData.push({
            company: json[i].company,
            email:	json[i].email,
            faculty:	json[i].faculty,
            id: json[i].id,
            lastLogin:	json[i].lastLogin,
            name: json[i].name,
            phone: json[i].phone,
            roles: json[i].roles,
            tags: json[i].tags,
            username: json[i].username
          });
        }
        this.setState({
          users: newData
        });
      }.bind(this));
  }

  /**
   * Gets the list of only company users.
   */
  getCompanyUsers = () => {
    fetch('/api/users/' + Auth.getUserInfo().sub, {
        method: 'get',
        credentials: 'same-origin'
      }).then(function(response) {
        if(response.ok) {
          return response.json();
        } else throw new Error('There was a problem with network connection.');
      }).then(function(json) {
        this.setState({
          companyId: json.company.id
        });
        fetch('/api/companies/' + json.company.id + '/leaders', {
            method: 'get',
            credentials: 'same-origin'
          }).then(function(response) {
              if(response.ok) {
                return response.json();
            } else {
              throw new Error("There was a problem with network connection. The GET request couldn't be processed!");
            }
          }).then(function(json) {
          var newData = [];

          for(let i in json) {
            newData.push({
              company: json[i].company,
              email:	json[i].email,
              faculty:	json[i].faculty,
              id: json[i].id,
              lastLogin:	json[i].lastLogin,
              name: json[i].name,
              phone: json[i].phone,
              roles: json[i].roles,
              tags: json[i].tags,
              username: json[i].username
            });
          }
          this.setState({
            users: newData
        });
      }.bind(this));
    }.bind(this));
  }

  /**
   * Handles all nonGET server requests. Updates using getting methods afterwards.
   * @param  method     method to call
   * @param  id         item id
   * @param  data       item body
   */
  manageData = (method, id, data) => {
    fetch('/api/users/'+id, {
      method: method,
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: data
    }).then(function(response) {
      if(response.ok) {
          var label = "";
          switch(method.toLowerCase()) {
            case "post":
              label = "The user has been succesfully created!";
              break;
            case "put":
              label = "The user has been succesfully updated!";
              break;
            case "delete":
              label = "The user has been succesfully removed!";
              break;
            default:
              label = "Wrong method input!";
              return;
          }
          this.setState({
            snackbarLabel: label,
            snackbarActive: true
          });
          if(Auth.hasRole(Util.userRoles.admin))
            this.getUsers();
          else if(Auth.hasRole(Util.userRoles.companyRep))
            this.getCompanyUsers();
      } else {
        this.setState({
          snackbarLabel: "An error occured! Your request couldn't be processed. It's possible that you have a problem with your internet connection or that the server is not responding.",
          snackbarActive: true
        });
        throw new Error('There was a problem with network connection. '+method.toUpperCase()+' could not be processed!');
      }
    }.bind(this));
  }

  /**
   * Toggles the visiblity of the Snackbar.
   */
  toggleSnackbar = () => {
    this.setState({ snackbarActive: !this.state.snackbarActive });
  }

  /**
   * Makes the Snackbar visible and gives it a new label.
   */
  setSnackbarResponse = (label) => {
    this.setState({ snackbarActive: true, snackbarLabel: label });
  }

  /**
   * Generates the appropriate user view components.
   * @return the desired component
   */
  generateView = () => {
    if (Auth.hasRole(Util.userRoles.admin))
      return (<AdminUsersView users={this.state.users}
        dataHandler={(method, id, data) => this.manageData(method, id, data)} />);
    else if (Auth.hasRole(Util.userRoles.companyRep))
      return (<CompanyRepUsersView users={this.state.users} companyId={this.state.companyId}
        dataHandler={(method, id, data) => this.manageData(method, id, data)}
        snackbarSetter={(label) => this.setSnackbarResponse(label)} />);
  }

  render() {
    return(
      <div>
        <h1>{ _t.translate('Users') }</h1>
        {this.generateView()}
        <SiteSnackbar active={this.state.snackbarActive} toggleHandler={this.toggleSnackbar} label={this.state.snackbarLabel} />
      </div>
    );
  }
}

export default Users;
