import React, { Component } from 'react';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';
import Dropdown from 'react-toolbox/lib/dropdown/Dropdown.js';
import Input from 'react-toolbox/lib/input/Input.js';

import Util from '../Util.js';

/**
 * Renders the input Dialog for universities.
 * @param active                                whether the dialog is active
 * @param university                            editing university
 * @param toggleHandler(label)                function to call when closing the Dialog with a given label for SiteSnackbar
 */
class UniversityDialog extends Component {
  constructor(props) {
    super(props);

    this.state = {
      name: "",
      city: "",
      country: "CZ",
      url: "",
      logo: "",
      titleLabel: "Add a new university",
      actions: [
        { label: "Add", onClick: () => this.handleAdd()},
        { label: "Cancel", onClick: () => this.props.toggleHandler("") }
      ]
    }
  }

  /**
   * Sets default input values on props change.
   */
  componentWillReceiveProps(nextProps) {
    if (this.props === nextProps)
      return;

    this.setState({
      name: Util.isEmpty(nextProps.university) ? "" : nextProps.university.name,
      city: Util.isEmpty(nextProps.university) ? "" : nextProps.university.city,
      country: Util.isEmpty(nextProps.university) ? "CZ" : nextProps.university.country,
      url: Util.isEmpty(nextProps.university) ? "" : nextProps.university.url,
      logo: Util.isEmpty(nextProps.university) ? "" : nextProps.university.logoUrl,
      titleLabel: Util.isEmpty(nextProps.university) ? "Add a new university" : "Edit university",
      actions:
      (Util.isEmpty(nextProps.university)) ? [
        { label: "Add", onClick: () => this.handleAdd()},
        { label: "Cancel", onClick: () => nextProps.toggleHandler("") }
      ] : [
        { label: "Save", onClick: () => this.handleEdit()},
        { label: "Cancel", onClick: () => nextProps.toggleHandler("") }
      ]
    });
  }



  /**
   * Handles changes of Dialog Inputs.
   * @param name    state variable name
   * @param value   new input value to be set onChange
   */
  handleInputChange = (name, value) => {
    this.setState({
      [name]: value
    })
  }

  /**
   * Handles adding request.
   */
  handleAdd = () => {
    fetch('/api/universities', {
      method: 'post',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        city: this.state.city,
        country: this.state.country,
        logoUrl: this.state.logo,
        name: this.state.name,
        url: this.state.url
      })
    }).then(function(response) {
        if(response.ok) {
        this.props.toggleHandler("The university has been succesfully created!");
      } else throw new Error('There was a problem with network connection.');
    }.bind(this));
  }

  /**
   * Handles editing request.
   */
  handleEdit = () => {
    fetch('/api/universities/' + this.props.university.id, {
      method: 'put',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        city: this.state.city,
        country: this.state.country,
        logoUrl: this.state.logo,
        name: this.state.name,
        url: this.state.url
      })
    }).then(function(response) {
        if(response.ok) {
        this.props.toggleHandler("The university has been succesfully updated!");
      } else throw new Error('There was a problem with network connection.');
    }.bind(this));
  }

  render() {
    return(
      <div className="Dialog-university">
        <Dialog
          active={this.props.active}
          actions={this.state.actions}
          onEscKeyDown={() => this.props.toggleHandler("")}
          onOverlayClick={() => this.props.toggleHandler("")}
          title={this.state.titleLabel}
          type="large" >
          <table width="100%">
            <tbody>
              <tr>
                <td colSpan="2">
                  <Input type='text'  label="Name" hint='University name' value={this.state.name} onChange={(value) => this.handleInputChange("name", value)} required />
                </td>
              </tr>
              <tr>
                <td>
                  <Input type='text' label="City" hint='City' value={this.state.city} onChange={(value) => this.handleInputChange("city", value)}  required maxLength={32} />
                </td>
                <td>
                  <Dropdown
                    auto
                    source={Util.countriesSource}
                    onChange={(value) => this.handleInputChange("country", value)}
                    label='Country'
                    value={this.state.country}
                  />
                </td>
              </tr>
              <tr>
                <td colSpan="2">
                  <Input type='text' label='Website' hint='Link to the web page' value={this.state.url} onChange={(value) => this.handleInputChange("url", value)} />
                </td>
              </tr>
              <tr>
                <td colSpan="2">
                  <Input type='text' label='Logo' hint='Link to the logo source' value={this.state.logo} onChange={(value) => this.handleInputChange("logo", value)} />
                </td>
              </tr>
            </tbody>
          </table>
        </Dialog>
      </div>
    );
  }
}

export default UniversityDialog;