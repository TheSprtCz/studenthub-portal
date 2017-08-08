import React, { Component } from 'react';
import Table from 'react-toolbox/lib/table/Table.js';
import TableHead from 'react-toolbox/lib/table/TableHead.js';
import TableRow from 'react-toolbox/lib/table/TableRow.js';
import TableCell from 'react-toolbox/lib/table/TableCell.js';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';

import DeleteButton from '../components/DeleteButton.js';
import EditButton from '../components/EditButton.js';

import _t from '../Translations.js';

class DegreesTable extends Component {
  state = {degrees: [], dialogActive: false, editId: -1}

  componentDidMount() {
    this.getDegrees();
  }

  getDegrees() {
    fetch('/api/degrees', {
      credentials: 'same-origin',
      method: 'get'
    }).then(function(response) {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      this.setState({degrees: json});
    }.bind(this));
  }

  deleteDegree = (name) => {
    fetch('/api/degrees/' + name, {
      method: 'delete',
      credentials: 'same-origin'
    }).then(function(response) {
      if (response.ok) {
        // Util.notify("success", "", "The degree has been succesfully deleted!");
        this.getDegrees();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this));
  }

  toggleDialog = (id) => {
    this.setState({dialogActive: !this.state.dialogActive, editId: id});
    if (id === -1)
      this.getDegrees();
  }

  render () {
    return (
      <div>
        <h1>
          { _t.translate('Topic Degrees') }
        </h1>
        <DegreeDialog active={this.state.dialogActive} degree={(this.state.editId === -1) ? -1 : this.state.degrees[this.state.editId]}
          toggleHandler={() => this.toggleDialog(-1)} />
        <Table selectable={false}>
          <TableHead>
            <TableCell>{ _t.translate('Name') }</TableCell>
            <TableCell>{ _t.translate('Description') }</TableCell>
            <TableCell>{ _t.translate('Actions') }</TableCell>
          </TableHead>
          {this.state.degrees.map((item, index) => (
            <TableRow key={item.name}>
              <TableCell>{item.name}</TableCell>
              <TableCell>{item.description}</TableCell>
              <TableCell>
                <EditButton toggleHandler={() => this.toggleDialog(index)} />
                <DeleteButton deleteHandler={() => this.deleteDegree(item.name)} />
              </TableCell>
            </TableRow>
          ))}
        </Table>
      </div>
    );
  }
}

class DegreeDialog extends Component {
  state = {
    name: '', description: '', dialogTitle: _t.translate('New Degree'),
    actions : [
      { label: _t.translate('Add'), onClick: () => this.addDegree() },
      { label: _t.translate('Cancel'), onClick: () => this.handleToggle() }
    ]
  };

  /**
   * Sets default input values on props change.
   */
  componentWillReceiveProps(nextProps) {
    if(this.props === nextProps) return;

    this.setState({
      name: (nextProps.degree === -1) ? "" : nextProps.degree.name,
      description: (nextProps.degree === -1) ? "" : nextProps.degree.description,
      dialogTitle: (nextProps.degree === -1) ? _t.translate('New Degree') : _t.translate('Edit Degree'),
      actions: (nextProps.degree === -1) ? [
        { label: _t.translate('Add'), onClick: () => this.addDegree()},
        { label: _t.translate('Cancel'), onClick: () => this.handleToggle() }
      ] : [
        { label: _t.translate('Edit'), onClick: () => this.editDegree()},
        { label: _t.translate('Cancel'), onClick: () => this.handleToggle() }
      ]
    });
  }

  handleToggle = () => {
    this.props.toggleHandler();
  };

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  };

  /**
   * Handles adding request.
   */
  addDegree = () => {
    fetch('/api/degrees/', {
      method: 'post',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        name: this.state.name,
        description: this.state.description
      })
    }).then(function(response) {
      if (response.ok) {
        // Util.notify("success", "", "The degree has been succesfully created!");
        this.handleToggle();
      } else {
        // Util.notify("error", "It's possible that you have a problem with your internet connection or that the server is not responding.", "An error occured! Your request couldn't be processed.");
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this));
  }

  /**
   * Handles editing request.
   */
  editDegree = () => {
    fetch('/api/degrees/' + this.props.degree.name, {
      method: 'put',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        name: this.state.name,
        description: this.state.description
      })
    }).then(function(response) {
      if (response.ok) {
        // Util.notify("success", "", "The degree has been succesfully edited!");
        this.handleToggle();
      } else {
        // Util.notify("error", "It's possible that you have a problem with your internet connection or that the server is not responding.", "An error occured! Your request couldn't be processed.");
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this));
  }

  render() {
    return(
      <div className='pull-right'>
        <Button icon='add' floating onClick={this.handleToggle} />
        <Dialog
          actions={this.state.actions}
          active={this.props.active}
          onEscKeyDown={this.handleToggle}
          onOverlayClick={this.handleToggle}>
          <h2>{this.state.dialogTitle}</h2>
          <div>
            <div>
              <Input type='name' label={ _t.translate('Name') } icon='assignment'  hint="Change plan name" required value={this.state.name}
                onChange={this.handleChange.bind(this, 'name')} />
              <Input type='text' label={ _t.translate('Description') } icon='description'  hint="Change plan description" value={this.state.description}
                onChange={this.handleChange.bind(this, 'description')} />
            </div>
          </div>
        </Dialog>
      </div>
    )
  }
}

const TopicDegrees = () => (
  <div>
    <DegreesTable />
  </div>
);

export default TopicDegrees;
