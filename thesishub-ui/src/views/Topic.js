import React from 'react';
import ReactMarkdown from 'react-markdown';
import Chip from 'react-toolbox/lib/chip/Chip.js';
import Button from 'react-toolbox/lib/button/Button.js';
import { TwitterButton, FacebookLikeButton } from 'react-social-buttons';

import Util from '../Util.js';
import _t from '../Translations.js';

var pdfConverter = require('jspdf');

const LEFT_MARGIN = 50;
const TOP_MARGIN = 80;
const LINE_MARGIN = 20;

class TopicDetails extends React.Component {
  constructor(props) {
    super(props);
    this.state = { topic: null };
    this.getTopic();
  }

  getTopic = () => {
    fetch('/api/topics/' + this.props.id, {
        credentials: 'same-origin',
        method: 'get'
      }).then(function(response) {
        if (response.ok) {
            return response.json();
        } else {
          throw new Error('There was a problem with network connection.');
        }
      }).then(function(json) {
        this.setState({
          topic: json
        });
      }.bind(this));
  }

  exportToPDF = () => {
    var pdf = new pdfConverter('p','pt','a4');
    var description = this.state.topic.description;
    var descriptionArray = [];

    while(description.indexOf(".") !== -1) {
      descriptionArray.push(description.substring(0, description.indexOf(".")));
      description = description.substring(description.indexOf("."));
    }
    descriptionArray.push(description);

    pdf.setDrawColor(175, 175, 175);
    pdf.setFont("arial");
    pdf.setFontSize(26);
    pdf.text(LEFT_MARGIN, TOP_MARGIN, this.state.topic.title);
    pdf.lines([[520, 0]], LEFT_MARGIN-10, TOP_MARGIN+5, false);
    pdf.setFontSize(18);
    pdf.text(LEFT_MARGIN, TOP_MARGIN+LINE_MARGIN*2, _t.translate('Technical leader'));
    pdf.text(LEFT_MARGIN, TOP_MARGIN+LINE_MARGIN*6, _t.translate('Short abstract'));
    pdf.text(LEFT_MARGIN, TOP_MARGIN+LINE_MARGIN*9, _t.translate('Tags'));
    pdf.text(LEFT_MARGIN, TOP_MARGIN+LINE_MARGIN*12, _t.translate('Topic description'));
    pdf.setFontSize(12);
    pdf.text(LEFT_MARGIN, TOP_MARGIN+LINE_MARGIN*3, this.state.topic.creator.name);
    pdf.text(LEFT_MARGIN, TOP_MARGIN+LINE_MARGIN*4, (Util.isEmpty(this.state.topic.creator.company)) ? "N/A" : this.state.topic.creator.company.name);
    pdf.text(LEFT_MARGIN, TOP_MARGIN+LINE_MARGIN*7, this.state.topic.shortAbstract);
    pdf.text(LEFT_MARGIN, TOP_MARGIN+LINE_MARGIN*10, this.state.topic.tags.toString());
    pdf.text(LEFT_MARGIN, TOP_MARGIN+LINE_MARGIN*13, descriptionArray);

    pdf.save("test.pdf");
  }

  render() {
    if (this.state.topic === null)
      return <div></div>;
    else
    return (
      <div className="container-fluid">
        <h2>{ this.state.topic.title }</h2>
        <p>{ (this.state.topic.enabled) ? _t.translate("This topic is ready to be used.") : _t.translate("This topic is disabled for use.") }</p>
        <hr />
        <h3>{ _t.translate('Short abstract')}</h3>
        <p>{ this.state.topic.shortAbstract }</p>
        <h3>{ _t.translate('Topic description')}</h3>
        <ReactMarkdown source={ this.state.topic.description } />
        <h3>{ _t.translate('Technical leader')}</h3>
        <p>{ this.state.topic.creator.name }</p>
        <p>{ (Util.isEmpty(this.state.topic.creator.company)) ? "N/A" : this.state.topic.creator.company.name }</p>
        <h3>{ _t.translate('Tags')}</h3>
        <p>{ this.state.topic.tags.map( (tag) => <Chip key={tag}> {tag} </Chip> ) }</p>
        <hr />
        <Button className="pull-right" label={ _t.translate('Save as PDF') } onClick={() => this.exportToPDF()} icon="picture_as_pdf" />
        <FacebookLikeButton url={window.location} />
        <TwitterButton url={window.location} text={this.state.topic.title + ": " + this.state.topic.shortAbstract + " #Study"}/>
      </div>
    )
  }
}

const Topic = ({ location }) => (
  <div>
    <h1>{ _t.translate('Topic overview') }</h1>
    <TopicDetails id={ new URLSearchParams(location.search).get('id') } />
  </div>
);

export default Topic
