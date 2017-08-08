import React, { Component } from 'react';
import IconButton from 'react-toolbox/lib/button/IconButton.js';

/**
 * Renders the return to university list IconButton.
 * @param returnCallback() defines the return function to call onClick
 */
class ReturnButton extends Component {
  render() {
    return(
      <span>
        <IconButton className="pull-right" onClick={() => this.props.returnCallback()} icon="arrow_back" />
      </span>
    );
  }
}

export default ReturnButton;
