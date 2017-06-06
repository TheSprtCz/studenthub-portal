import React, { Component } from 'react';
import ReactMarkdown from 'react-markdown';
import Button from 'react-toolbox/lib/button/Button.js';
import Chip from 'react-toolbox/lib/chip/Chip.js';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';

import _t from '../Translations.js';

/**
 * Renders the add Button.
 * @param toggleHandler()             defines the function to call onClick
 */
class TopicDetailsDialog extends Component {
  state = { active: false };

  actions = [
    { label: "Close", onClick: () => this.handleToggle() }
  ];

  handleToggle = () => {
    this.setState({active: !this.state.active});
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
            <hr />
            <h3>{ _t.translate('Short abstract')}</h3>
            <p>{ this.props.topic.shortAbstract }</p>
            <h3>{ _t.translate('Topic description')}</h3>
            <ReactMarkdown source={ this.props.topic.description } />
            <h3>{ _t.translate('Technical leader')}</h3>
            <p>{ this.props.topic.creator.name }</p>
            <p>{ this.props.topic.creator.company.name }</p>
            <h3>{ _t.translate('Tags')}</h3>
            <p>{ this.props.topic.tags.map( (tag) => <Chip key={tag}> {tag} </Chip> ) }</p>
            <hr />
          </div>
        </Dialog>
      </div>
    );
  }
}

export default TopicDetailsDialog;
