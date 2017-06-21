import React, { Component } from 'react';
import IconButton from 'react-toolbox/lib/button/IconButton.js';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';

import _t from '../Translations.js';

/**
 * Renders a ContentTable delete IconButton.
 * @param deleteHandler() defines the function to call onClick
 */
class DeleteButton extends Component {
  state = { active: false }
  actions = [
    { label: _t.translate("Delete"), onClick: () => this.props.deleteHandler() },
    { label: _t.translate("Cancel"), onClick: () => this.handleToggle() }
  ]

  handleToggle = () => {
    this.setState({ active: !this.state.active });
  }

  render() {
    return(
      <span>
        <IconButton onClick={() => this.handleToggle()} icon="delete" label="Delete" />
        <Dialog
          actions={this.actions}
          active={this.state.active}
          onEscKeyDown={() => this.handleToggle()}
          onOverlayClick={() => this.handleToggle()}
          title={ _t.translate("Are you sure you want to proceed?") }>
          <p>{ _t.translate("Deleting is a permanent action!") }</p>
        </Dialog>
      </span>
    );
  }
}

export default DeleteButton;
