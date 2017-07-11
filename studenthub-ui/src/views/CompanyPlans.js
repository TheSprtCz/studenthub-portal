import React, { Component } from 'react';
import Table from 'react-toolbox/lib/table/Table.js';
import TableHead from 'react-toolbox/lib/table/TableHead.js';
import TableRow from 'react-toolbox/lib/table/TableRow.js';
import TableCell from 'react-toolbox/lib/table/TableCell.js';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';
import Pager from '../components/Pager.js';

import DeleteButton from '../components/DeleteButton.js';
import EditButton from '../components/EditButton.js';
import SiteSnackbar from '../components/SiteSnackbar.js';

import Util from '../Util.js';
import _t from '../Translations.js';

class PlansTable extends Component {
  state = {
    plans: [],
    dialogActive: false,
    editId: -1,
    page: 0,
    pages: 1,
    snackbarLabel: "",
    snackbarActive: false
  }

  componentDidMount() {
    this.getPlans();
  }

  getPlans() {
    fetch("/api/plans?size=" + Util.PLANS_PER_PAGE + "&start=" +
          (this.state.page * Util.PLANS_PER_PAGE), {
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
      this.setState({plans: json});
    }.bind(this));
  }

  deletePlan = (name) => {
    fetch('/api/plans/' + name, {
      method: 'delete',
      credentials: 'same-origin'
    }).then(function(response) {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      this.setState({
        snackbarLabel: "The company plan has been succesfully removed.",
        snackbarActive: true
      });
      this.getPlans();
    }.bind(this));
  }

  toggleDialog = (id) => {
    this.setState({dialogActive: !this.state.dialogActive, editId: id});
  }

  toggleSnackbar = (message, isError) => {
    if (message === this.state.snackbarLabel)
      this.setState({snackbarActive: !this.state.snackbarActive});
    else {
      this.setState({snackbarActive: !this.state.snackbarActive, snackbarLabel: message});
      if (!isError)
        this.getPlans();
    }
  }

  changePage = (page) => {
    this.setState({page: page.selected});

    setTimeout(function() {
      this.getPlans();
    }.bind(this), 2);
  }

  render () {
    return (
      <div>
        <h1>
          { _t.translate('Company Plans') }
        </h1>
        <PlanDialog active={this.state.dialogActive} plan={(this.state.editId === -1) ? -1 : this.state.plans[this.state.editId]}
          toggleHandler={() => this.toggleDialog(-1)} snackbarHandler={(message, isError) => this.toggleSnackbar(message, isError)} />
        <Table selectable={false}>
          <TableHead>
            <TableCell numeric>{ _t.translate('Topic limit') }</TableCell>
            <TableCell>{ _t.translate('Name') }</TableCell>
            <TableCell>{ _t.translate('Description') }</TableCell>
            <TableCell>{ _t.translate('Actions') }</TableCell>
          </TableHead>
          {this.state.plans.map((item, index) => (
            <TableRow key={item.name}>
              <TableCell numeric>{item.maxTopics}</TableCell>
              <TableCell>{item.name}</TableCell>
              <TableCell>{item.description}</TableCell>
              <TableCell>
                <EditButton toggleHandler={() => this.toggleDialog(index)} />
                <DeleteButton deleteHandler={() => this.deleteCompany(item.name)} />
              </TableCell>
            </TableRow>
          ))}
        </Table>
        <Pager pages={this.state.pages} pageChanger={(page) => this.changePage(page)} />
        <SiteSnackbar active={this.state.snackbarActive} label={this.state.snackbarLabel} toggleHandler={() => this.toggleSnackbar(this.state.snackbarLabel, false)} />
      </div>
    );
  }
}

class PlanDialog extends Component {
  state = {
    name: '', description: '', maxTopics: 5, dialogTitle: _t.translate('New Plan'),
    actions : [
      { label: _t.translate('Add'), onClick: () => this.addPlan() },
      { label: _t.translate('Cancel'), onClick: () => this.handleToggle() }
    ]
  };

  /**
   * Sets default input values on props change.
   */
  componentWillReceiveProps(nextProps) {
    if(this.props === nextProps) return;

    this.setState({
      name: (nextProps.plan === -1) ? "" : nextProps.plan.name,
      description: (nextProps.plan === -1) ? "" : nextProps.plan.description,
      maxTopics: (nextProps.plan === -1) ? 5 : nextProps.plan.maxTopics,
      dialogTitle: (nextProps.plan === -1) ? _t.translate('New Plan') : _t.translate('Edit Plan'),
      actions: (nextProps.plan === -1) ? [
        { label: _t.translate('Add'), onClick: () => this.addPlan()},
        { label: _t.translate('Cancel'), onClick: () => this.handleToggle() }
      ] : [
        { label: _t.translate('Edit'), onClick: () => this.editPlan()},
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
  addPlan = () => {
    fetch('/api/plans/', {
      method: 'post',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        name: this.state.name,
        description: this.state.description,
        maxTopics: this.state.maxTopics
      })
    }).then(function(response) {
      if (response.ok) {
        this.props.snackbarHandler("The company plan has been succesfully created!");
        this.handleToggle();
      } else {
        this.props.snackbarHandler("There was a problem with network connection. Your request hasn't been processed.");
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this));
  }

  /**
   * Handles editing request.
   */
  editPlan = () => {
    fetch('/api/plans/' + this.props.plan.name, {
      method: 'put',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        name: this.state.name,
        description: this.state.description,
        maxTopics: this.state.maxTopics
      })
    }).then(function(response) {
      if (response.ok) {
        this.props.snackbarHandler("The company plan has been succesfully edited!");
        this.handleToggle();
      } else {
        this.props.snackbarHandler("There was a problem with network connection. Your request hasn't been processed.");
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
              <Input type='number' min={0} label={ _t.translate('Topic limit') } icon='format_list_numbered'  hint="Change plan topic maximum limit" value={this.state.maxTopics}
                onChange={this.handleChange.bind(this, 'maxTopics')} />
            </div>
          </div>
        </Dialog>
      </div>
    )
  }
}

const CompanyPlans = () => (
  <div>
    <PlansTable />
  </div>
);

export default CompanyPlans;
