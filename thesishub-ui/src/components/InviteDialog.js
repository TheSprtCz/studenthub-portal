import React, { Component } from 'react';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';
import Dropdown from 'react-toolbox/lib/dropdown/Dropdown.js';
import Input from 'react-toolbox/lib/input/Input.js';

import InviteButton from './InviteButton.js';

import Util from '../Util.js';
import _t from '../Translations.js';

class InviteDialog extends Component {
  state = { active: false, email: '', name: '', phone: '', role: '' }

  actions = [
    { label: _t.translate('Invite user'), onClick: () => this.handleSubmit() },
    { label: _t.translate('Cancel'), onClick: () => this.handleToggle() }
  ];

  /**
   * Handels the invite request.
   */
  handleSubmit = () => {
    fetch('/api/account/invite', {
      method: 'post',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        email: this.state.email,
        name: this.state.name,
        phone: this.state.phone,
        company: (this.props.company.id === -1) ? null : this.props.company,
        faculty: (this.props.faculty.id === -1) ? null : this.props.faculty,
        roles: [this.state.role],
        username: this.state.email
      })
    }).then(function(response) {
      if (response.ok) {
        Util.notify("success", "", "The user has been succesfully invited.");
      } else {
        Util.notify("error", "It's possible that you have a problem with your internet connection or that the server is not responding.",
          "An error occured! Your request couldn't be processed.");
        throw new Error('There was a problem with network connection. POST request could not be processed!');
      }
    });
  }

  /**
   * Handles changes of Dialog Inputs.
   * @param name    state variable name
   * @param value   new input value to be set onChange
   */
  handleChange = (name, value) => {
    this.setState({ [name]: value })
  }

  /**
   * Handles visiblity changing.
   */
   handleToggle = () => {
     this.setState({active: !this.state.active});
   }

  render() {
    return(
      <div>
        <InviteButton toggleHandler={() => this.handleToggle()}/>
        <Dialog
          active={this.state.active}
          actions={this.actions}
          onEscKeyDown={() => this.handleToggle()}
          onOverlayClick={() => this.handleToggle()}
          title={ _t.translate('Invite user')}
          type="large">
          <section className="container">
            <Input type='email' label={ _t.translate('Email address')} hint="User'semail adress" icon='email'
              required value={this.state.email} onChange={this.handleChange.bind(this, 'email')} />
            <Input type='text' label={ _t.translate('Name')} hint="User's name" name='name' icon='textsms'
              required value={this.state.name} onChange={this.handleChange.bind(this, 'name')} maxLength={16} />
            <Input type='tel' label={ _t.translate('Phone')} hint="User's phone number" name='phone' icon='phone'
              value={this.state.phone} onChange={this.handleChange.bind(this, 'phone')} maxLength={12} />
            <Dropdown
              auto required
              label={ _t.translate('Role') }
              onChange={this.handleChange.bind(this, 'role')}
              source={(this.props.company.id === -1) ? Util.rolesSourceUniversity : Util.rolesSourceCompany}
              value={this.state.role}
              icon='person' />
            <br />
          </section>
        </Dialog>
      </div>
    );
  }
}

export default InviteDialog;
