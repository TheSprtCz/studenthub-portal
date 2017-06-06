import React, { Component } from 'react';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';
import Input from 'react-toolbox/lib/input/Input.js';
import DatePicker from 'react-toolbox/lib/date_picker/DatePicker.js';

import Util from '../Util.js';
import _t from '../Translations.js';

class FacultyDialog extends Component {
  constructor(props) {
    super(props);

    this.state = {
      title: "",
      deadline: "",
      titleLabel:  _t.translate("Add a new task"),
      actions: [
        { label:  _t.translate("Add"), onClick: () => this.handleAdd()},
        { label:  _t.translate("Cancel"), onClick: () => this.props.toggleHandler("") }
      ]
    }
  }

  /**
   * Sets default input values on props change.
   */
  componentWillReceiveProps(nextProps) {
    if(this.props === nextProps) return;

    this.setState({
      title: Util.isEmpty(nextProps.task) ? "" : nextProps.task.title,
      deadline: Util.isEmpty(nextProps.task) ? new Date() : nextProps.task.deadline,
      titleLabel: Util.isEmpty(nextProps.task) ?  _t.translate("Add a new task") :  _t.translate("Edit task"),
      actions:
      (Util.isEmpty(nextProps.task)) ?
        [
          { label: _t.translate("Add"), onClick: () => this.handleAdd()},
          { label: _t.translate("Cancel"), onClick: () => this.props.toggleHandler("") }
        ] : [
          { label: _t.translate("Save changes"), onClick: () => this.handleEdit()},
          { label: _t.translate("Cancel"), onClick: () => this.props.toggleHandler("") }
        ],
    });
  }

  /**
   * Handles changes of Dialog fields.
   * @param name    state variable name
   * @param value   new input value to be set onChange
   */
  handleChange = (name, value) => {
    this.setState({
      [name]: value
    })
  }

  /**
   * Handles adding request.
   */
  handleAdd = () => {
    fetch('/api/tasks', {
      method: 'post',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        application: this.props.app,
        title: this.state.title,
        deadline: this.state.deadline,
        completed: false
      })
    }).then(function(response) {
      if (response.ok) {
        this.props.toggleHandler("The task has been succesfully created!");
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this));
  }

  /**
   * Handles editing request.
   */
  handleEdit = () => {
    fetch('/api/tasks/' + this.props.task.id, {
      method: 'put',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        application: this.props.app,
        title: this.state.title,
        deadline: this.state.deadline,
        completed: this.props.task.completed
      })
    }).then(function(response) {
      if(response.ok) {
        this.props.toggleHandler("The task has been succesfully updated!");
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
          type="normal">
          <Input type='text' label={ _t.translate('Task name')} hint='Task title' required  value={this.state.title} onChange={(value) => this.handleChange("title", value)} />
          <DatePicker
            label={ _t.translate('Deadline')}
            onChange={this.handleChange.bind(this, 'deadline')}
            value={this.state.deadline} />
        </Dialog>
      </div>
    );
  }
}

export default FacultyDialog;
