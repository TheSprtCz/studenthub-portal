import React, { Component } from 'react';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';
import Input from 'react-toolbox/lib/input/Input.js';

import PersonalDataProcDialog from './PersonalDataProcDialog.js';
import TermsOfUseDialog from './TermsOfUseDialog.js';
import InviteButton from './InviteButton.js';

import _t from '../Translations.js';

class UserInviteDialog extends Component {
  state = { active: false, email: '', name: '', phone: '' }

  actions = [
    { label: _t.translate('Invite user'), onClick: () => this.handleSubmit() },
    { label: _t.translate('Cancel'), onClick: () => this.handleToggle() }
  ];

  /**
   * Handels the invite request.
   */
  handleSubmit = () => {
    fetch('/api/account/signUp', {
      method: 'post',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        email: this.state.email,
        name: this.state.name,
        phone: this.state.phone,
        company: this.props.company,
        roles: ["TECH_LEADER"],
        username: this.state.email
      })
    }).then(function(response) {
      if (response.ok) {
        this.props.snackbarSetter("The user has been succesfully invited.");
      } else {
        this.props.snackbarSetter("An error occured! Your request couldn't be processed. It's possible that you have a problem with your internet connection or that the server is not responding.");
        throw new Error('There was a problem with network connection. POST request could not be processed!');
      }
    }.bind(this));
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
     this.setState({ active: !this.state.active });
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
            <p>
              Here you can invite a new user from your company to become a TECH_LEADER. Activation email will be sent to them to complete the registration.
            </p>
            <Input type='email' label={ _t.translate('Email address')} hint="User'semail adress" icon='email' required value={this.state.email} onChange={this.handleChange.bind(this, 'email')} />
            <Input type='text' label={ _t.translate('Name')} hint="User's name" name='name' icon='textsms' required value={this.state.name} onChange={this.handleChange.bind(this, 'name')} maxLength={16} />
            <Input type='tel' label={ _t.translate('Phone')} hint="User's phone number" name='phone' icon='phone' value={this.state.phone} onChange={this.handleChange.bind(this, 'phone')} maxLength={12} />
            <br />
          </section>
        </Dialog>
      </div>
    );
  }
}

export default UserInviteDialog;
