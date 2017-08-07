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

class CountriesTable extends Component {
  state = {countries: [], dialogActive: false, editId: -1}

  componentDidMount() {
    this.getCountries();
  }

  getCountries() {
    fetch('/api/countries', {
      credentials: 'same-origin',
      method: 'get'
    }).then(function(response) {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      this.setState({countries: json});
    }.bind(this));
  }

  deleteCountry = (tag) => {
    fetch('/api/countries/' + tag, {
      method: 'delete',
      credentials: 'same-origin'
    }).then(function(response) {
      if (response.ok) {
        // Util.notify("success", "", "The country has been succesfully deleted!");
        this.getCountries();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this));
  }

  toggleDialog = (id) => {
    this.setState({dialogActive: !this.state.dialogActive, editId: id});
    if (id === -1)
      this.getCountries();
  }

  render () {
    return (
      <div>
        <h1>
          { _t.translate('Countries') }
        </h1>
        <CountryDialog active={this.state.dialogActive} country={(this.state.editId === -1) ? -1 : this.state.countries[this.state.editId]}
          toggleHandler={() => this.toggleDialog(-1)} />
        <Table selectable={false}>
          <TableHead>
            <TableCell>{ _t.translate('Tag') }</TableCell>
            <TableCell>{ _t.translate('Name') }</TableCell>
            <TableCell>{ _t.translate('Actions') }</TableCell>
          </TableHead>
          {this.state.countries.map((item, index) => (
            <TableRow key={item.tag}>
              <TableCell>{item.tag}</TableCell>
              <TableCell>{item.name}</TableCell>
              <TableCell>
                <EditButton toggleHandler={() => this.toggleDialog(index)} />
                <DeleteButton deleteHandler={() => this.deleteCountry(item.tag)} />
              </TableCell>
            </TableRow>
          ))}
        </Table>
      </div>
    );
  }
}

class CountryDialog extends Component {
  state = {
    tag: '', name: '', dialogTitle: _t.translate('New Country'),
    actions : [
      { label: _t.translate('Add'), onClick: () => this.addCountry() },
      { label: _t.translate('Cancel'), onClick: () => this.handleToggle() }
    ]
  };

  /**
   * Sets default input values on props change.
   */
  componentWillReceiveProps(nextProps) {
    if(this.props === nextProps) return;

    this.setState({
      tag: (nextProps.country === -1) ? "" : nextProps.country.tag,
      name: (nextProps.country === -1) ? "" : nextProps.country.name,
      dialogTitle: (nextProps.country === -1) ? _t.translate('New Country') : _t.translate('Edit Country'),
      actions: (nextProps.country === -1) ? [
        { label: _t.translate('Add'), onClick: () => this.addCountry()},
        { label: _t.translate('Cancel'), onClick: () => this.handleToggle() }
      ] : [
        { label: _t.translate('Edit'), onClick: () => this.editCountry()},
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
  addCountry = () => {
    fetch('/api/countries/', {
      method: 'post',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        tag: this.state.tag,
        name: this.state.name
      })
    }).then(function(response) {
      if (response.ok) {
        // Util.notify("success", "", "The country has been succesfully created!");
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
  editCountry = () => {
    fetch('/api/countries/' + this.props.country.tag, {
      method: 'put',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        tag: this.state.tag,
        name: this.state.name
      })
    }).then(function(response) {
      if (response.ok) {
        // Util.notify("success", "", "The country has been succesfully edited!");
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
              <Input type='name' label={ _t.translate('Tag') } icon='label'  hint="Change country tag" required
                value={this.state.tag} onChange={this.handleChange.bind(this, 'tag')} />
              <Input type='text' label={ _t.translate('Name') } icon='assignment'  hint="Change country name"
                value={this.state.name} onChange={this.handleChange.bind(this, 'name')} />
            </div>
          </div>
        </Dialog>
      </div>
    )
  }
}

const Countries = () => (
  <div>
    <CountriesTable />
  </div>
);

export default Countries;
