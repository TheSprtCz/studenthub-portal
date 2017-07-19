import React, { Component } from 'react';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';
import Input from 'react-toolbox/lib/input/Input.js';
import _t from '../Translations.js';

/**
 * Renders the add Dialog for faculties.
 * @param active                          whether the dialog is active
 * @param user                            the user to edit
 * @param editHandler(user)               editing callback function
 * @param toggleHandler()                 function to call when closing the Dialog
 */
class RestrictedUserEditDialog extends Component {
  state = { name: "", mail: "", phone: "", tags: "" }

  /**
   * Sets default input values on props change.
   */
  componentWillReceiveProps(nextProps) {
    if(this.props === nextProps || nextProps.user === -1) return;

    var tagsState = "";

    for (let i = 0; i < nextProps.user.tags.length; i++) {
      tagsState += nextProps.user.tags[i];
      if ((i + 1) < nextProps.user.tags.length) tagsState += ";"
    }

    this.setState({
      name: nextProps.user.name,
      mail: nextProps.user.email,
      phone: nextProps.user.phone,
      tags: tagsState
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
      company: this.props.user.company,
      email:	this.state.mail,
      faculty: this.props.user.faculty,
      name:	this.state.name,
      phone:	this.state.phone,
      roles:	this.props.user.roles,
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
    this.setState({ [name]: value })
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

  render() {
    return(
      <div>
        <Dialog
          active={this.props.active}
          actions={this.actions}
          onEscKeyDown={() => this.props.toggleHandler()}
          onOverlayClick={() => this.props.toggleHandler()}
          title={ _t.translate("Edit user") }
          type="large">
          <p>
            Here you can edit the profile of TECH_LEADERs from your company.
          </p>
          <Input type='text' label="Email" hint='Email of the user'  required  value={this.state.mail}
            onChange={(value) => this.handleChange("mail", value)} />
          <Input type='text' label={ _t.translate("Name") } hint='Name of the user' required  value={this.state.name}
            onChange={(value) => this.handleChange("name", value)} />
          <Input type='text' label={ _t.translate("Phone") } hint='Phone number of the user' value={this.state.phone}
            onChange={(value) => this.handleChange("phone", value)} />
          <Input type='text' label={ _t.translate("Tags") } hint='Divide tags using ;' value={this.state.tags}
            onChange={(value) => this.handleChange("tags", value)} />
        </Dialog>
      </div>
    );
  }
}

export default RestrictedUserEditDialog;
