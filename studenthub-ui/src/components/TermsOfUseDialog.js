import React from 'react';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';
import Link from 'react-toolbox/lib/link/Link.js';
import ReactMarkdown from 'react-markdown';
import Util from '../Util.js';

class TermsOfUseDialog extends React.Component {
  state = {
    active: false
  };

  handleToggle = () => {
    this.setState({active: !this.state.active});
  }

  actions = [
    { label: "Close", onClick: this.handleToggle }
  ];

  render () {
    return (
      <div>
        <Link label='Terms of Use' onClick={this.handleToggle} />
        <Dialog
          actions={this.actions}
          active={this.state.active}
          onEscKeyDown={this.handleToggle}
          onOverlayClick={this.handleToggle}
          title='Terms of Use' >
          <ReactMarkdown source={ Util.TERMS_OF_USE } />
        </Dialog>
      </div>
    );
  }
}

export default TermsOfUseDialog;
