import React, { Component } from 'react';
import { Redirect } from 'react-router-dom';
import Table from 'react-toolbox/lib/table/Table.js';
import TableHead from 'react-toolbox/lib/table/TableHead.js';
import TableRow from 'react-toolbox/lib/table/TableRow.js';
import TableCell from 'react-toolbox/lib/table/TableCell.js';

import EditButton from '../components/EditButton.js';
import Pager from '../components/Pager.js';


import Auth from '../Auth.js';
import Util from '../Util.js';
import _t from '../Translations.js';

const ApplicationTableHint = () => (
  <div className="alert alert-info alert-dismissible" role="alert" style={{ marginTop: '1em'}}>
    <button type="button" className="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
    <strong>{ _t.translate('Hint:') }</strong> { _t.translate('Here you can manage your applications.') }
  </div>
)

class ApplicationTable extends Component {
  state = {applications: [], nextAppllications: [], redirect: -1,
           page: 0, pages: 1};

  componentDidMount() {
    this.getApplications();
  }

  /**
   * Gets the list of all applications.
   */
  getApplications = () => {
    var url = "";
    if (Auth.hasRole(Util.userRoles.admin))
      url = "/api/applications?size=" + Util.APPLICATIONS_PER_PAGE + "&start=" +
             (this.state.page * Util.APPLICATIONS_PER_PAGE);
    else if (Auth.hasRole(Util.userRoles.techLeader))
      url = '/api/users/'+ Auth.getUserInfo().sub + "/ledApplications?size=" + Util.APPLICATIONS_PER_PAGE +
             "&start=" + (this.state.page * Util.APPLICATIONS_PER_PAGE);
    else if (Auth.hasRole(Util.userRoles.superviser))
      url = '/api/users/'+ Auth.getUserInfo().sub + "/supervisedApplications?size=" +
             Util.APPLICATIONS_PER_PAGE + "&start=" +
             (this.state.page * Util.APPLICATIONS_PER_PAGE);
    else if (Auth.hasRole(Util.userRoles.student))
      url = '/api/users/'+ Auth.getUserInfo().sub + "/applications?size=" + Util.APPLICATIONS_PER_PAGE +
             "&start=" + (this.state.page * Util.APPLICATIONS_PER_PAGE);

    fetch(url, {
      credentials: 'same-origin',
      method: 'get'
    }).then(function(response) {
      if (response.ok) {
        this.setState({pages: parseInt(response.headers.get("Pages"), 10)});
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this)).then(function(json) {
      this.setState({applications: json});
    }.bind(this));
  }

  generateRedirect = () => {
    if (this.state.redirect !== -1) {
      return(<Redirect to={"/applications/"+this.state.redirect} />);
    } else {
      return;
    }
  }

  handleRedirect = (id) => {
    setTimeout(function() {
      this.setState({ redirect: id });
    }.bind(this), 100);
  }

  changePage = (page) => {
    this.setState({page: page.selected});

    setTimeout(function() {
      this.getApplications();
    }.bind(this), 2);
  }

  render () {
    return (
      <div>
        <ApplicationTableHint />
        <h1>{ _t.translate('My Applications') }</h1>
        <Table selectable={false}>
          <TableHead>
            <TableCell>{ _t.translate('Topic title') }</TableCell>
            <TableCell>{ _t.translate('Faculty') }</TableCell>
            <TableCell>{ _t.translate('Technical leader') }</TableCell>
            <TableCell>{ _t.translate('Academic supervisor') }</TableCell>
            <TableCell>{ _t.translate('Start') }</TableCell>
            <TableCell>{ _t.translate('Finish') }</TableCell>
            <TableCell>{ _t.translate('Grade') }</TableCell>
            <TableCell>{ _t.translate('Actions') }</TableCell>
          </TableHead>
          {this.state.applications.map((item) => (
            <TableRow key={item.id}>
              <TableCell><strong>{item.topic.title}</strong></TableCell>
              <TableCell>{item.faculty.name}</TableCell>
              <TableCell>{Util.isEmpty(item.techLeader) ? "" : item.techLeader.email}</TableCell>
              <TableCell>{Util.isEmpty(item.academicSupervisor) ? "" : item.academicSupervisor.email}</TableCell>
              <TableCell>{Util.isEmpty(item.thesisStarted) ? "No date defined" :
                new Date(item.thesisStarted).toString()}</TableCell>
              <TableCell>{Util.isEmpty(item.thesisFinish) ? "No date defined" :
                new Date(item.thesisFinish).toString()}</TableCell>
              <TableCell>{item.grade}</TableCell>
              <TableCell>
                <EditButton toggleHandler={() => this.handleRedirect(item.id)} />
              </TableCell>
            </TableRow>
          ))}
        </Table>
        <Pager pages={this.state.pages} pageChanger={(page) => this.changePage(page)} />
        {this.generateRedirect()}
      </div>
    );
  }
}

const MyApplications = () => (
  <div>
    <ApplicationTable />
  </div>
);

export default MyApplications;
