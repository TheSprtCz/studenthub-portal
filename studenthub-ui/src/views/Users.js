import React, { Component } from 'react';

import AdminUsersView from '../components/AdminUsersView.js';
import UsersTable from '../components/UsersTable.js';
import Pager from '../components/Pager.js';

import Auth from '../Auth.js';
import Util from '../Util.js';

import _t from '../Translations.js';

class Users extends Component {

  state = {users: [], companyId: -1, facultyId: -1 , page: 0, pages: 1}

  componentDidMount() {
    if (Auth.hasRole(Util.userRoles.admin))
      this.getUsers();
    else if (Auth.hasRole(Util.userRoles.companyRep))
      this.getCompanyUsers();
    else if (Auth.hasRole(Util.userRoles.ambassador))
      this.getUniversityUsers();
    this.changePage(1);
  }

  /**
   * Gets the list of all users.
   */
  getUsers = () => {
    fetch("/api/users?size=" + Util.USERS_PER_PAGE + "&start=" +
           (this.state.page * Util.USERS_PER_PAGE), {
        method: 'get',
        credentials: 'same-origin'
      }).then(function(response) {
          if(response.ok) {
            this.setState({pages: parseInt(response.headers.get("Pages"), 10)});
            return response.json();
          } else {
            throw new Error('There was a problem with network connection.');
          }
      }.bind(this)).then(function(json) {
        this.setState({users: json});
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
        } else {
          throw new Error('There was a problem with network connection.');
        }
      }).then(function(json) {
        this.setState({
          companyId: json.company.id
        });
        fetch('/api/companies/' + json.company.id + "/leaders?size=" + Util.USERS_PER_PAGE +
               "&start=" + (this.state.page * Util.USERS_PER_PAGE), {
            method: 'get',
            credentials: 'same-origin'
          }).then(function(response) {
              if(response.ok) {
                this.setState({pages: parseInt(response.headers.get("Pages"), 10)});
                return response.json();
            } else {
              throw new Error("There was a problem with network connection. The GET request couldn't be processed!");
            }
          }.bind(this)).then(function(json) {
            this.setState({users: json});
      }.bind(this));
    }.bind(this));
  }

  /**
  * Gets the list of only university users.
  */
  getUniversityUsers = () => {
    var page = (this.state.offsetWentDown) ? this.state.page : (this.state.page+1);
    fetch('/api/users/' + Auth.getUserInfo().sub, {
        method: 'get',
        credentials: 'same-origin'
      }).then(function(response) {
        if(response.ok) {
          return response.json();
        } else throw new Error('There was a problem with network connection.');
      }).then(function(json) {
        this.setState({
          facultyId: json.faculty.id
        });
        fetch('/api/universities/' + json.faculty.university.id + "/supervisors?size=" + Util.USERS_PER_PAGE + "&start=" +
          (page * Util.USERS_PER_PAGE), {
            method: 'get',
            credentials: 'same-origin'
          }).then(function(response) {
              if(response.ok) {
                return response.json();
            } else if (response.status === 404) {
              this.setState({
                users: (this.state.nextUsers === null || typeof this.state.nextUsers === 'undefined') ? this.state.users : this.state.nextUsers,
                nextUsers: null
              });
            } else {
              throw new Error("There was a problem with network connection. The GET request couldn't be processed!");
            }
          }.bind(this)).then(function(json) {
            if (this.state.offsetWentDown) {
              this.setState({
                users: json,
                nextUsers: this.state.users
              });
            }
            else {
              this.setState({
                users: (this.state.nextUsers === null || typeof this.state.nextUsers === 'undefined') ? this.state.users : this.state.nextUsers,
                nextUsers: json
              });
            };
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
          Util.notify("success", "", label);
          if(Auth.hasRole(Util.userRoles.admin))
            this.getUsers();
          else if(Auth.hasRole(Util.userRoles.companyRep))
            this.getCompanyUsers();
          else if (Auth.hasRole(Util.userRoles.ambassador))
            this.getUniversityUsers();
      } else {
        Util.notify("error", "There was a problem with network connection.", "Your request hasn't been processed.");
        throw new Error('There was a problem with network connection. '+method.toUpperCase()+' could not be processed!');
      }
    }.bind(this));
  }

  /**
   * Generates the appropriate user view components.
   * @return the desired component
   */
  generateView = () => {
    if (Auth.hasRole(Util.userRoles.ambassador))
      return (<AdminUsersView users={this.state.users}
        dataHandler={(method, id, data) => this.manageData(method, id, data)} />);
    else if (Auth.hasRole(Util.userRoles.companyRep) || Auth.hasRole(Util.userRoles.ambassador))
      return (<UsersTable users={this.state.users} company={{ id: this.state.companyId }} faculty={{ id: this.state.facultyId }}
        dataHandler={(method, id, data) => this.manageData(method, id, data)} />);
  }

  changePage = (page) => {
    this.setState({page: page.selected});

    setTimeout(function() {
      if (Auth.hasRole(Util.userRoles.admin))
        this.getUsers();
      else if (Auth.hasRole(Util.userRoles.companyRep))
        this.getCompanyUsers();
      else if (Auth.hasRole(Util.userRoles.ambassador))
        this.getUniversityUsers();
    }.bind(this), 2);
  }

  render() {
    return(
      <div>
        <h1>{ _t.translate('Users') }</h1>
        {this.generateView()}
        <Pager pages={this.state.pages} pageChanger={(page) => this.changePage(page)} />
      </div>
    );
  }
}

export default Users;
