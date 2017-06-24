import React from 'react';
import ReactMarkdown from 'react-markdown';
import Chip from 'react-toolbox/lib/chip/Chip.js';

import Util from '../Util.js';
import _t from '../Translations.js';

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
      </div>
    )
  }
}

const Topic = ({ match }) => (
  <div>
    <h1>{ _t.translate('Topic overview') }</h1>
    <TopicDetails id={match.params.id} />
  </div>
);

export default Topic
