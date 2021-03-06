import React, { Component } from 'react';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';
import Dropdown from 'react-toolbox/lib/dropdown/Dropdown.js';
import Input from 'react-toolbox/lib/input/Input.js';
import Checkbox from 'react-toolbox/lib/checkbox/Checkbox.js';

import Util from '../Util.js';
import _t from '../Translations.js';

class CompanySelect extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      value: Util.isEmpty(this.props.baseValue) ? 0: this.props.baseValue.id,
      labels: []
    };

    this.getCompanyLabels();
  }

  /**
   * Sets default input values on props change.
   */
  componentWillReceiveProps(nextProps) {
    if(this.props === nextProps) return;

    this.setState({
      value: Util.isEmpty(nextProps.baseValue) ? 0 : nextProps.baseValue.id
    });
  }

  handleChange = (value) => {
    this.setState({value: value});
    this.props.changeHandler({id: value});
  };

  getCompanyLabels() {
    fetch('/api/companies', {
      method: 'get'
    }).then(function(response) {
        if(response.ok) {
          return response.json();
      } else throw new Error('There was a problem with network connection.');
    }).then(function(json) {
      var newData = [];

      for(let i in json) {
        newData.push({
          value: json[i].id,
          label: json[i].name
        });
      }
      this.setState({
        labels: newData
      });
    }.bind(this));
  }

  render () {
    return (
      <Dropdown
        auto required
        label={ _t.translate('Company')}
        onChange={this.handleChange}
        source={this.state.labels}
        value={this.state.value}
        icon='business' />
    );
  }
}

class FacultySelect extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      value: Util.isEmpty(this.props.baseValue) ? 0: this.props.baseValue.id,
      labels: []
    };

    this.getFacultyLabels();
  }

  /**
   * Sets default input values on props change.
   */
  componentWillReceiveProps(nextProps) {
    if(this.props === nextProps) return;

    this.setState({
      value: Util.isEmpty(nextProps.baseValue) ? 0 : nextProps.baseValue.id
    });
  }

  handleChange = (value) => {
    this.setState({value: value});
    this.props.changeHandler({id: value});
  };

  getFacultyLabels() {
    fetch('/api/faculties', {
        method: 'get'
      }).then(function(response) {
          if(response.ok) {
            return response.json();
        } else throw new Error('There was a problem with network connection.');
      }).then(function(json) {
        var newData = [];

        for(let i in json) {
          newData.push({
            value: json[i].id,
            label: json[i].name
          });
        }
        this.setState({
          labels: newData
        });
      }.bind(this));
  }

  render () {
    return (
      <Dropdown
        auto required
        label={ _t.translate('Faculty')}
        onChange={this.handleChange}
        source={this.state.labels}
        value={this.state.value}
        icon='business' />
    );
  }
}

/**
 * Renders the add Dialog for faculties.
 * @param active                          whether the dialog is active
 * @param user                            the user to edit
 * @param editHandler(user)               editing callback function
 * @param toggleHandler()                 function to call when closing the Dialog
 */
class UserEditDialog extends Component {
  constructor(props) {
    super(props);

    this.state = {
      name: "",
      mail: "",
      phone: "",
      faculty: "",
      company: "",
      tags: "",
      supervisor: false,
      admin: false,
      rep: false,
      student: false,
      tech: false,
      project: false,
      amb: false
    }
  }

  /**
   * Sets default input values on props change.
   */
  componentWillReceiveProps(nextProps) {
    if(this.props === nextProps || nextProps.user === -1) return;

    var tagsState = "";
    var supervisorState = false;
    var adminState = false;
    var repState = false;
    var studentState = false;
    var techState = false;
    var projectState = false;
    var ambState = false;


    for (let i = 0; i < nextProps.user.tags.length; i++) {
      tagsState += nextProps.user.tags[i];
      if ((i + 1) < nextProps.user.tags.length) tagsState += ";"
    }

    for (let i = 0; i < nextProps.user.roles.length; i++) {
      if (nextProps.user.roles[i] === Util.userRoles.superviser) supervisorState = true;
      else if (nextProps.user.roles[i] === Util.userRoles.admin) adminState = true;
      else if (nextProps.user.roles[i] === Util.userRoles.companyRep) repState = true;
      else if (nextProps.user.roles[i] === Util.userRoles.student) studentState = true;
      else if (nextProps.user.roles[i] === Util.userRoles.techLeader) techState = true;
      else if (nextProps.user.roles[i] === Util.userRoles.proLeader) projectState = true;
      else if (nextProps.user.roles[i] === Util.userRoles.ambassador) ambState = true;
    }

    this.setState({
      name: nextProps.user.name,
      mail: nextProps.user.email,
      phone: nextProps.user.phone,
      faculty: nextProps.user.faculty,
      company: nextProps.user.company,
      tags: tagsState,
      supervisor: supervisorState,
      admin: adminState,
      rep: repState,
      student: studentState,
      tech: techState,
      project: projectState,
      amb: ambState
    });
  }

  actions = [
    { label: _t.translate('Save changes'), onClick: () => this.handleSave() },
    { label: _t.translate('Cancel'), onClick: () => this.props.toggleHandler() }
  ];

  /**
   * Handels the request to save the changes.
   */
  handleSave = () => {
    this.props.editHandler(JSON.stringify({
      company: this.state.company,
      email:	this.state.mail,
      faculty: this.state.faculty,
      name:	this.state.name,
      phone:	this.state.phone,
      roles:	this.getRoles(),
      tags:	this.getTags(),
      username:	this.state.mail
    }));
    this.props.toggleHandler();
  }

  /**
   * Handles changes of states.
   * @param name    state variable name
   * @param value   new input value to be set onChange
   */
  handleChange = (name, value) => {
    this.setState({
      [name]: value
    })
  }

  getTags = () => {
    var tags = [];
    var stringTags = this.state.tags;

    while(stringTags.indexOf(";") !== -1) {
      tags.push(stringTags.substring(0, stringTags.indexOf(";")));
      stringTags = stringTags.substring(stringTags.indexOf(";")+1);
    }

    return tags;
  }

  getRoles = () => {
    var roles = [];

    if (this.state.supervisor === true) roles.push(Util.userRoles.superviser);
    if (this.state.admin === true) roles.push(Util.userRoles.admin);
    if (this.state.rep === true) roles.push(Util.userRoles.companyRep);
    if (this.state.student === true) roles.push(Util.userRoles.student);
    if (this.state.tech === true) roles.push(Util.userRoles.techLeader);
    if (this.state.project === true) roles.push(Util.userRoles.proLeader);
    if (this.state.amb === true) roles.push(Util.userRoles.ambassador);

    return roles;
  }

  render() {
    return(
      <div className="Dialog-faculty">
        <Dialog
          active={this.props.active}
          actions={this.actions}
          onEscKeyDown={() => this.props.toggleHandler()}
          onOverlayClick={() => this.props.toggleHandler()}
          title={ _t.translate("Edit user") }
          type="large">
          <table width="100%">
            <tbody>
              <tr>
                <td colSpan="2">
                  <Input type='text' label="Email" hint='Email of the user'  required  value={this.state.mail} onChange={(value) => this.handleChange("mail", value)} />
                </td>
              </tr>
              <tr>
                <td width="50%">
                  <Input type='text' label={ _t.translate("Name") } hint='Name of the user' required  value={this.state.name} onChange={(value) => this.handleChange("name", value)} />
                </td>
                <td width="50%">
                  <Input type='text' label={ _t.translate("Phone") } hint='Phone number of the user' value={this.state.phone} onChange={(value) => this.handleChange("phone", value)} />
                </td>
              </tr>
              <tr>
                <td width="50%">
                  <FacultySelect changeHandler={(value) => this.handleChange("faculty", value)} baseValue={this.state.faculty} />
                </td>
                <td width="50%">
                  <CompanySelect changeHandler={(value) => this.handleChange("company", value)} baseValue={this.state.company} />
                </td>
              </tr>
              <tr>
                <td colSpan="2">
                  <Input type='text' label={ _t.translate("Tags") } hint='Divide tags using ;' value={this.state.tags} onChange={(value) => this.handleChange("tags", value)} />
                </td>
              </tr>
            </tbody>
          </table>
          <Checkbox
            checked={this.state.supervisor}
            label={ _t.translate("AC_SUPERVISOR") }
            name='roles'
            onChange={this.handleChange.bind(this, 'supervisor')} />
          <Checkbox
            checked={this.state.admin}
            label={ _t.translate("ADMIN") }
            name='roles'
            onChange={this.handleChange.bind(this, 'admin')} />
          <Checkbox
            checked={this.state.rep}
            label={ _t.translate("COMPANY_REP") }
            name='roles'
            onChange={this.handleChange.bind(this, 'rep')} />
          <Checkbox
            checked={this.state.amb}
            label={ _t.translate("UNIVERSITY_AMB") }
            name='roles'
            onChange={this.handleChange.bind(this, 'amb')} />
          <Checkbox
            checked={this.state.student}
            label={ _t.translate("STUDENT") }
            name='roles'
            onChange={this.handleChange.bind(this, 'student')} />
          <Checkbox
            checked={this.state.tech}
            label={ _t.translate("TECH_LEADER") }
            name='roles'
            onChange={this.handleChange.bind(this, 'tech')} />
          <Checkbox
            checked={this.state.project}
            label={ _t.translate("PROJECT_LEADER") }
            name='roles'
            onChange={this.handleChange.bind(this, 'project')} />
        </Dialog>
      </div>
    );
  }
}

export default UserEditDialog;
