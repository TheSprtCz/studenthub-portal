import React, { Component } from 'react';
import { Redirect } from 'react-router-dom';
import Table from 'react-toolbox/lib/table/Table.js';
import TableHead from 'react-toolbox/lib/table/TableHead.js';
import TableRow from 'react-toolbox/lib/table/TableRow.js';
import TableCell from 'react-toolbox/lib/table/TableCell.js';

import EditButton from '../components/EditButton.js';
import DeleteButton from '../components/DeleteButton.js';

import Auth from '../Auth.js';
import Util from '../Util.js';
import _t from '../Translations.js';

class ProjectTable extends Component {
  state = {projects: [], redirect: -1};

  componentDidMount() {
    this.getProjects();
  }

  /**
   * Gets the list of all applications.
   */
  getProjects = () => {
    fetch(Auth.hasRole(Util.userRoles.admin) ? "/api/projects" : "/api/users/" +
          Auth.getUserInfo().sub + "/projects", {
      credentials: 'same-origin',
      method: 'get'
    }).then(function(response) {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      this.setState({projects: json});
    }.bind(this));
  }

  handleRedirect = (id) => {
    setTimeout(function() {
      this.setState({ redirect: id });
    }.bind(this), 100);
  }

  deleteProject = (id) => {
    fetch('/api/projects/' + id, {
      method: 'delete',
      credentials: 'same-origin'
    }).then(function(response) {
      if (response.ok) {
        Util.notify("success", "", "The project has been succesfully removed!");
        this.getProjects();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this));
  }

  render () {
    if (this.state.redirect !== -1) {
      return(<Redirect to={"/projects?id="+this.state.redirect} />);
    }

    return (
      <div>
        <h1>{ _t.translate('Projects') }</h1>
        <Table selectable={false}>
          <TableHead>
            <TableCell>{ _t.translate('Name') }</TableCell>
            <TableCell>{ _t.translate('Description') }</TableCell>
            <TableCell>{ _t.translate('Companies') }</TableCell>
            <TableCell>{ _t.translate('Faculties') }</TableCell>
            <TableCell>{ _t.translate('Leaders') }</TableCell>
            <TableCell>{ _t.translate('Topics') }</TableCell>
            <TableCell>{ _t.translate('Actions') }</TableCell>
          </TableHead>
          {this.state.projects.map((item) => (
            <TableRow key={item.id}>
              <TableCell><strong>{item.name}</strong></TableCell>
              <TableCell>{item.description}</TableCell>
              <TableCell>
                {item.companies.map((item2, index) => (
                  <span key={item2.id}>
                    {((item.companies.length - 1) === index) ? item2.name : item2.name + ", "}
                  </span>
                ))}
              </TableCell>
              <TableCell>
                {item.faculties.map((item2, index) => (
                  <span key={item2.id}>
                    {((item.faculties.length - 1) === index) ? item2.name : item2.name + ", "}
                  </span>
                ))}
              </TableCell>
              <TableCell>
                {item.creators.map((item2, index) => (
                  <span key={item2.id}>
                    {((item.creators.length - 1) === index) ? item2.name : item2.name + ", "}
                  </span>
                ))}
              </TableCell>
              <TableCell>
                {item.topics.map((item2, index) => (
                  <span key={item2.id}>
                    {((item.topics.length - 1) === index) ? item2.title : item2.title + ", "}
                  </span>
                ))}
              </TableCell>
              <TableCell>
                <EditButton toggleHandler={() => this.handleRedirect(item.id)} />
                <DeleteButton deleteHandler={() => this.deleteProject(item.id)} />
              </TableCell>
            </TableRow>
          ))}
        </Table>
      </div>
    );
  }
}

const Projects = () => (
  <div>
    <ProjectTable />
  </div>
);

export default Projects;
