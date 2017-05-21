import React, { Component } from 'react';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';
import Input from 'react-toolbox/lib/input/Input.js';

import Util from '../Util.js';

/**
 * Renders the input Dialog for faculties.
 * @param active                                whether the dialog is active
 * @param data                                  the associated faculty
 * @param selectedUniversity                    the associated university
 * @param toggleHandler()                       function to call when closing the Dialog
 */
class FacultyDialog extends Component {
  constructor(props) {
    super(props);

    this.state = {
      name: "",
      titleLabel: "Add a new faculty",
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
    if(this.props === nextProps) return;

    this.setState({
      name: Util.isEmpty(nextProps.data) ? "" : nextProps.data.name,
      titleLabel: Util.isEmpty(nextProps.data) ? "Add a new faculty" : "Edit faculty",
      actions:
      (Util.isEmpty(nextProps.data)) ?
        [
          { label: "Add", onClick: () => this.handleAdd()},
          { label: "Cancel", onClick: () => this.handleToggle() }
        ] : [
          { label: "Save", onClick: () => this.handleEdit()},
          { label: "Cancel", onClick: () => this.handleToggle() }
        ],
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
    fetch('/api/faculties', {
      method: 'post',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        name: this.state.name,
        university: this.props.selectedUniversity
      })
    }).then(function(response) {
      if (response.ok) {
        this.props.toggleHandler("The faculty has been succesfully created!");
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this));
  }

  /**
   * Handles editing request.
   */
  handleEdit = () => {
    fetch('/api/faculties/' + this.props.data.id, {
      method: 'put',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        name: this.state.name,
        university: this.props.selectedUniversity
      })
    }).then(function(response) {
      if(response.ok) {
        this.props.toggleHandler("The faculty has been succesfully updated!");
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this));
  }

  render() {
    return(
      <div className="Dialog-faculty">
        <Dialog
          active={this.props.active}
          actions={this.state.actions}
          onEscKeyDown={() => this.props.toggleHandler("")}
          onOverlayClick={() => this.props.toggleHandler("")}
          title={this.state.titleLabel}
          type="large">
          <table width="100%">
            <tbody>
              <tr>
                <td>
                  <Input type='text' label="Name" hint='Faculty name' required  value={this.state.name} onChange={(value) => this.handleInputChange("name", value)} />
                </td>
              </tr>
            </tbody>
          </table>
        </Dialog>
      </div>
    );
  }
}

export default FacultyDialog;
