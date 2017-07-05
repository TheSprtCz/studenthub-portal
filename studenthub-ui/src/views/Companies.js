import React, { Component } from 'react';
import Table from 'react-toolbox/lib/table/Table.js';
import TableHead from 'react-toolbox/lib/table/TableHead.js';
import TableRow from 'react-toolbox/lib/table/TableRow.js';
import TableCell from 'react-toolbox/lib/table/TableCell.js';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';
import Dropdown from 'react-toolbox/lib/dropdown/Dropdown.js';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';

import DeleteButton from '../components/DeleteButton.js';
import EditButton from '../components/EditButton.js';
import SiteSnackbar from '../components/SiteSnackbar.js';

import Util from '../Util.js';
import _t from '../Translations.js';

class PlanSelect extends React.Component {
  state = { value: '', plans: [] }

  componentDidMount() {
    this.getPlans();
  }

  handleChange = (value) => {
    this.setState({value: value});
    this.props.changeHandler({name: value});
  };

  getPlans() {
    fetch('/api/plans', {
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
          value: json[i].name,
          label: json[i].name
        });
      }
      this.setState({
        plans: newData
      });
    }.bind(this));
  }

  render () {
    return (
      <Dropdown
        auto required
        label={ _t.translate("Plan") }
        onChange={this.handleChange}
        source={this.state.plans}
        value={this.state.value}
        icon='assignment' />
    );
  }
}

class CompaniesTable extends Component {
  state = {
    companies: [],
    dialogActive: false,
    editId: -1,
    snackbarLabel: "",
    snackbarActive: false
  }

  componentDidMount() {
    this.getCompanies();
  }

  getCompanies = () => {
    fetch('/api/companies/', {
      method: 'get',
      credentials: 'same-origin'
    }).then(function(response) {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      this.setState({
        companies: json
      });
    }.bind(this));
  }

  deleteCompany = (id) => {
    fetch('/api/companies/' + id, {
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
        snackbarLabel: "The company has been succesfully removed.",
        snackbarActive: true
      });
      this.getCompanies();
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
        this.getCompanies();
    }
  }

  render () {
    return (
      <div>
        <h1>
          { _t.translate('Companies') }
        </h1>
        <CompanyDialog active={this.state.dialogActive} company={(this.state.editId === -1) ? -1 : this.state.companies[this.state.editId]}
          toggleHandler={() => this.toggleDialog(-1)} snackbarHandler={(message, isError) => this.toggleSnackbar(message, isError)} />
        <Table selectable={false}>
          <TableHead>
            <TableCell>ID</TableCell>
            <TableCell>Logo</TableCell>
            <TableCell>{ _t.translate('Name') }</TableCell>
            <TableCell>{ _t.translate('City') }</TableCell>
            <TableCell>{ _t.translate('Country') }</TableCell>
            <TableCell>{ _t.translate('Web page') }</TableCell>
            <TableCell>{ _t.translate('Size') }</TableCell>
            <TableCell>{ _t.translate('Plan') }</TableCell>
          </TableHead>
          {this.state.companies.map((item, index) => (
            <TableRow key={item.id}>
              <TableCell>{item.id}</TableCell>
              <TableCell><img src={"http://"+item.logoUrl} alt='Logo' /></TableCell>
              <TableCell>{item.name}</TableCell>
              <TableCell>{item.city}</TableCell>
              <TableCell>{item.country}</TableCell>
              <TableCell><a href={"http://"+item.url} target="_blank">{item.url}</a></TableCell>
              <TableCell>{item.size}</TableCell>
              <TableCell>{(Util.isEmpty(item.plan)) ? "N/A" : item.plan.name}</TableCell>
              <TableCell>
                <EditButton toggleHandler={() => this.toggleDialog(index)} />
                <DeleteButton deleteHandler={() => this.deleteCompany(item.id)} />
              </TableCell>
            </TableRow>
          ))}
        </Table>
        <SiteSnackbar active={this.state.snackbarActive} label={this.state.snackbarLabel} toggleHandler={() => this.toggleSnackbar(this.state.snackbarLabel, false)} />
      </div>
    );
  }
}

class CompanyDialog extends Component {
  state = {
    name: '', city: '', country: 'CZ', url: '', logoUrl: '', size: '', dialogTitle:
      _t.translate('New Company'), plan: [],
    actions : [
      { label: _t.translate('Add'), onClick: () => this.addCompany()},
      { label: _t.translate('Cancel'), onClick: () => this.handleToggle() }
    ]
  };

  /**
   * Sets default input values on props change.
   */
  componentWillReceiveProps(nextProps) {
    if(this.props === nextProps) return;

    this.setState({
      name: (nextProps.company === -1) ? "" : nextProps.company.name,
      city: (nextProps.company === -1) ? "" : nextProps.company.city,
      country: (nextProps.company === -1) ? "CZ" : nextProps.company.country,
      url: (nextProps.company === -1) ? "" : nextProps.company.url,
      logoUrl: (nextProps.company === -1) ? "" : nextProps.company.logoUrl,
      size: (nextProps.company === -1) ? "" : nextProps.company.size,
      plan: (nextProps.company === -1) ? "" : nextProps.company.plan,
      dialogTitle: (nextProps.company === -1) ? _t.translate('New Company') : _t.translate('Edit Company'),
      actions: (nextProps.company === -1) ? [
        { label: _t.translate('Add'), onClick: () => this.addCompany()},
        { label: _t.translate('Cancel'), onClick: () => this.handleToggle() }
      ] : [
        { label: _t.translate('Edit'), onClick: () => this.editCompany()},
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
  addCompany = () => {
    fetch('/api/companies/', {
      method: 'post',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        name: this.state.name,
        city: this.state.city,
        country: this.state.country,
        url: this.state.url,
        logoUrl: this.state.logoUrl,
        size: this.state.size,
        plan: this.state.plan
      })
    }).then(function(response) {
      if (response.ok) {
        this.props.snackbarHandler("The company has been succesfully created!");
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
  editCompany = () => {
    fetch('/api/companies/' + this.props.company.id, {
      method: 'put',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        name: this.state.name,
        city: this.state.city,
        country: this.state.country,
        url: this.state.url,
        logoUrl: this.state.logoUrl,
        size: this.state.size,
        plan: this.state.plan
      })
    }).then(function(response) {
      if (response.ok) {
        this.props.snackbarHandler("The company has been succesfully edited!");
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
              <Input type='name' label={ _t.translate('Name') } icon='textsms'  hint="Change company name" required value={this.state.name}
                onChange={this.handleChange.bind(this, 'name')} />
              <Input type='text' label={ _t.translate('City') } icon='location_city'  hint="Change company city headquarters" value={this.state.city}
                onChange={this.handleChange.bind(this, 'city')} />
              <Dropdown
                auto required
                onChange={this.handleChange.bind(this, 'country')}
                source={Util.countriesSource}
                name='country'
                value={this.state.country}
                icon='public'
                label={ _t.translate('Country') } />
              <Input type='url' label={ _t.translate('Web page') } icon='web'  hint="Change website url" value={this.state.url} onChange={this.handleChange.bind(this, 'url')} />
              <Input type='url' label='Logo' icon='photo'  hint="Change logo" value={this.state.logoUrl} onChange={this.handleChange.bind(this, 'logoUrl')} />
              <Dropdown
                auto required
                onChange={this.handleChange.bind(this, 'size')}
                source={Util.companySizesSource}
                name='size'
                value={this.state.size}
                icon='business'
                label={ _t.translate('Size') } />
                <PlanSelect changeHandler={(value) => this.handleChange("plan", value)} />
            </div>
          </div>
        </Dialog>
      </div>
    )
  }
}

const Companies = () => (
  <div>
    <CompaniesTable />
  </div>
);

export default Companies;
