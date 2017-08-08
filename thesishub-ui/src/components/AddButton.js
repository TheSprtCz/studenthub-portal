import React, { Component } from 'react';
import Button from 'react-toolbox/lib/button/Button.js';

/**
 * Renders the add Button.
 * @param toggleHandler()             defines the function to call onClick
 */
class AddButton extends Component {
  render() {
    return(
      <Button className="pull-right" onClick={() => this.props.toggleHandler()} icon="add" floating />
    );
  }
}

export default AddButton;
