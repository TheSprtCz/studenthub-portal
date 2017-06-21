import React, { Component } from 'react';
import { Redirect } from 'react-router-dom';
import ReactMarkdown from 'react-markdown';
import Button from 'react-toolbox/lib/button/Button.js';
import Chip from 'react-toolbox/lib/chip/Chip.js';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';

import Util from '../Util.js';
import _t from '../Translations.js';

/**
 * Renders the add Button.
 * @param toggleHandler()             defines the function to call onClick
 */
class TopicDetailsDialog extends Component {
  state = { active: false, redirect: false };

  actions = [
    { label: _t.translate('Go to topic page'), onClick: () => this.handleRedirect() },
    { label: _t.translate('Close'), onClick: () => this.handleToggle() }
  ];

  handleToggle = () => {
    this.setState({active: !this.state.active});
  }

  handleRedirect = () => {
    this.setState({redirect: true});
  }

  render() {
    return(
      <div>
        <Button label={this.props.label} onClick={() => this.handleToggle()} icon='assignment' />
        <Dialog
          actions={this.actions}
          active={this.state.active}
          onEscKeyDown={() => this.handleToggle()}
          onOverlayClick={() => this.handleToggle()} >
          <div className="container-fluid">
            <h2>{ this.props.topic.title }</h2>
            <p>{ (this.props.topic.enabled) ? _t.translate("This topic is ready to be used.") : _t.translate("This topic is disabled for use.") }</p>
            <hr />
            <h3>{ _t.translate('Short abstract')}</h3>
            <p>{ this.props.topic.shortAbstract }</p>
            <h3>{ _t.translate('Topic description')}</h3>
            <ReactMarkdown source={ this.props.topic.description } />
            <h3>{ _t.translate('Technical leader')}</h3>
            <p>{ this.props.topic.creator.name }</p>
            <p>{ (Util.isEmpty(this.props.topic.creator.company)) ? "N/A" : this.props.topic.creator.company.name }</p>
            <h3>{ _t.translate('Tags')}</h3>
            <p>{ this.props.topic.tags.map( (tag) => <Chip key={tag}> {tag} </Chip> ) }</p>
            <hr />
          </div>
        </Dialog>
        { (this.state.redirect) ? <Redirect to={"/topics/"+this.props.topic.id} /> : "" }
      </div>
    );
  }
}

export default TopicDetailsDialog;
