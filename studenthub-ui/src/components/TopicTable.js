import React from 'react';
import { NavLink } from 'react-router-dom';

// "import" jquery
const $ = window.$;

// Search input component
class SearchBar extends React.Component {
  render() {
    return (
      <div className="input-group">
        <input type="text" className="form-control" placeholder="Search for..." />
        <span className="input-group-btn">
          <button className="btn btn-default" type="button">Go!</button>
        </span>
      </div>
    );
  }
}

// Component for rendering basic topic info in search
function Topic(props) {
  const link = "/topics/" + props.topic.id;
  return (
    <div>
      <h3><NavLink to={link}>{props.topic.title}</NavLink></h3>
      <p>{props.topic.shortAbstract}</p>
    </div>
  );
}

// Comprehensive component for searching in topics
class TopicTable extends React.Component {
  constructor(props) {
    super(props);
    this.state = {topics: []};
  }

  componentDidMount() {
    let self = this;
    $.ajax("http://localhost:8080/api/topics").done(function(data) {
      self.setState({
        topics: data
      });
    });
  }

  render() {
    const topics = this.state.topics.map((topic) =>
      <Topic topic={topic} key={topic.id} />
    );

    return (
      <div>
        <SearchBar />
        {topics}
      </div>
    );
  }
}

export default TopicTable;
