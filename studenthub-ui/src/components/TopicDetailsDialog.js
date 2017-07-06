import React, { Component } from 'react';
import { Redirect } from 'react-router-dom';
import ReactMarkdown from 'react-markdown';
import Button from 'react-toolbox/lib/button/Button.js';
import Chip from 'react-toolbox/lib/chip/Chip.js';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';

import Util from '../Util.js';
import _t from '../Translations.js';

var pdfConverter = require('jspdf');

const LEFT_MARGIN = 50;
const TOP_MARGIN = 80;
const LINE_MARGIN = 20;

/**
 * Renders the add Button.
 * @param toggleHandler()             defines the function to call onClick
 */
class TopicDetailsDialog extends Component {
  state = { active: false, redirect: false };

  actions = [
    { label: _t.translate('Go to topic page'), onClick: () => this.handleRedirect() },
    { label: _t.translate('Save as PDF'), onClick: () => this.exportToPDF() },
    { label: _t.translate('Close'), onClick: () => this.handleToggle() }
  ];

  handleToggle = () => {
    this.setState({active: !this.state.active});
  }

  handleRedirect = () => {
    this.setState({redirect: true});
  }

  exportToPDF = () => {
    var pdf = new pdfConverter('p','pt','a4');
    var description = this.props.topic.description;
    var descriptionArray = [];

    while(description.indexOf(".") !== -1) {
      descriptionArray.push(description.substring(0, description.indexOf(".")));
      description = description.substring(description.indexOf("."));
    }
    descriptionArray.push(description);

    pdf.setDrawColor(175, 175, 175);
    pdf.setFont("arial");
    pdf.setFontSize(26);
    pdf.text(LEFT_MARGIN, TOP_MARGIN, this.props.topic.title);
    pdf.lines([[520, 0]], LEFT_MARGIN-10, TOP_MARGIN+5, false);
    pdf.setFontSize(18);
    pdf.text(LEFT_MARGIN, TOP_MARGIN+LINE_MARGIN*2, _t.translate('Technical leader'));
    pdf.text(LEFT_MARGIN, TOP_MARGIN+LINE_MARGIN*6, _t.translate('Short abstract'));
    pdf.text(LEFT_MARGIN, TOP_MARGIN+LINE_MARGIN*9, _t.translate('Tags'));
    pdf.text(LEFT_MARGIN, TOP_MARGIN+LINE_MARGIN*12, _t.translate('Topic description'));
    pdf.setFontSize(12);
    pdf.text(LEFT_MARGIN, TOP_MARGIN+LINE_MARGIN*3, this.props.topic.creator.name);
    pdf.text(LEFT_MARGIN, TOP_MARGIN+LINE_MARGIN*4, (Util.isEmpty(this.props.topic.creator.company)) ? "N/A" : this.props.topic.creator.company.name);
    pdf.text(LEFT_MARGIN, TOP_MARGIN+LINE_MARGIN*7, this.props.topic.shortAbstract);
    pdf.text(LEFT_MARGIN, TOP_MARGIN+LINE_MARGIN*10, this.props.topic.tags.toString());
    pdf.text(LEFT_MARGIN, TOP_MARGIN+LINE_MARGIN*13, descriptionArray);

    pdf.save("test.pdf");
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
