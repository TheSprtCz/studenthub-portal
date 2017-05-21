import React, { Component } from 'react';
import { Redirect } from 'react-router-dom';
import Table from 'react-toolbox/lib/table/Table.js';
import TableHead from 'react-toolbox/lib/table/TableHead.js';
import TableRow from 'react-toolbox/lib/table/TableRow.js';
import TableCell from 'react-toolbox/lib/table/TableCell.js';

import EditButton from '../components/EditButton.js';

import Auth from '../Auth.js';

class ApplicationTable extends Component {
  constructor(props) {
    super(props);

    this.state = {
      applications: [],
      redirect: -1
    };
    this.getApplications();
  }

  /**
   * Gets the list of all applications.
   */
  getApplications = () => {
    fetch('/api/users/'+ Auth.getUserInfo().sub + '/applications', {
      credentials: 'same-origin',
      method: 'get'
    }).then(function(response) {
      if (response.ok) {
          return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      var newData = [];

      for(let i in json) {
        newData.push({
          academicSupervisor: json[i].academicSupervisor,
          degree: json[i].degree,
          faculty: json[i].faculty,
          grade: json[i].grade,
          id: json[i].id,
          officialAssignment: json[i].officialAssignment,
          student: json[i].student,
          techLeader: json[i].techLeader,
          thesisFinish: json[i].thesisFinish,
          thesisStarted: json[i].thesisStarted,
          topic: json[i].topic
        });
      }
      this.setState({
        applications: newData
      });
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

  render () {
    return (
      <div>
        <h1>My Applications</h1>
        <Table selectable={false}>
          <TableHead>
            <TableCell>Topic Title</TableCell>
            <TableCell>Faculty</TableCell>
            <TableCell>Leader</TableCell>
            <TableCell>Supervisor</TableCell>
            <TableCell>Start</TableCell>
            <TableCell>Finish</TableCell>
            <TableCell>Grade</TableCell>
            <TableCell>Actions</TableCell>
          </TableHead>
          {this.state.applications.map((item) => (
            <TableRow key={item.id}>
              <TableCell><h5>{item.topic.title}</h5></TableCell>
              <TableCell>{item.faculty.name}</TableCell>
              <TableCell>{(item.techLeader === null || typeof item.techLeader === 'undefined') ? "" : item.techLeader.email}</TableCell>
              <TableCell>{(item.academicSupervisor === null || typeof item.academicSupervisor === 'undefined') ? "" : item.academicSupervisor.email}</TableCell>
              <TableCell>{item.thesisStarted}</TableCell>
              <TableCell>{item.thesisFinish}</TableCell>
              <TableCell>{item.grade}</TableCell>
              <TableCell>
                <EditButton toggleHandler={() => this.handleRedirect(item.id)} />
              </TableCell>
            </TableRow>
          ))}
        </Table>
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
