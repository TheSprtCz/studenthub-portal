import React from 'react';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';
import Link from 'react-toolbox/lib/link/Link.js';
import ReactMarkdown from 'react-markdown';
import Util from '../Util.js';
import _t from '../Translations.js';

class PersonalDataProcDialog extends React.Component {
  state = {
    active: false
  };

  handleToggle = () => {
    this.setState({active: !this.state.active});
  }

  actions = [
    { label: _t.translate('Close'), onClick: this.handleToggle }
  ];

  render () {
    return (
      <div>
        <Link label={ _t.translate('Personal Data Processing') } onClick={this.handleToggle} />
        <Dialog
          actions={this.actions}
          active={this.state.active}
          onEscKeyDown={this.handleToggle}
          onOverlayClick={this.handleToggle}
          title={ _t.translate('Personal Data Processing') } >
          <ReactMarkdown source={ Util.PERSONAL_DATA_PROCESSING } />
        </Dialog>
      </div>
    );
  }
}

export default PersonalDataProcDialog;
