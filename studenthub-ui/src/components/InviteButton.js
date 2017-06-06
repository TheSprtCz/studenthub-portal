import React, { Component } from 'react';
import Button from 'react-toolbox/lib/button/Button.js';
import _t from '../Translations.js';
/**
 * Renders the invite Button.
 * @param toggleHandler()   defines the function to call onClick when invitation Dialog should be shown
 */
class InviteButton extends Component {
  render() {
    return(
      <span className="pull-right" style={ { "paddingTop" : "15px" } }>
        <Button onClick={() => this.props.toggleHandler()} icon="send" label={ _t.translate('Invite user') } raised primary floating />
      </span>
    );
  }
}

export default InviteButton;
