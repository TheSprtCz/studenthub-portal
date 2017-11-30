import React, { Component } from 'react'

class TopicSearch extends Component {

  constructor(props) {
    super(props);
    this.state = { topics: [], query: props.query ? props.query : ''};

    this.handleChange = this.handleChange.bind(this);
  }

  componentDidMount() {
    this.getTopics();
  }

  handleChange(event) {
    const target = event.target;
    this.setState({[target.name]: target.value});
  }

  handleKeyPress = (e) => {
    if (e.charCode === 13) this.getTopics();
  }

  getTopics = () => {
    fetch("/api/topics/search?size=5&start=0&text=" + this.state.query, {
      credentials: 'same-origin'
    }).then(function(response) {
      if (response.ok) {
        return response.json();
      } else {
        console.log(response.status + ' ' + response.statusText);
      }
    }).then(function(json) {
      this.setState({topics: json});
    }.bind(this));
  }

  render() {
    return(
      <div>
        <div className="clearfix"></div>

        <h1>Topics</h1>

        <input type="text" name="query" placeholder="title, tags, abstract, ..."
          value={ this.state.query } onChange={ this.handleChange } onKeyPress={ this.handleKeyPress } />
        <button onClick={ this.getTopics }>Search</button>

        <hr/>

        { this.state.topics.map((item, index) => (
          <li key={ index }>
            <h4>{ item.title }</h4>
            <p>{ item.shortAbstract }</p>
          </li>
        ))}

      </div>
    )
  }
}

const Topics = ({ location }) => (
  <div>
    <TopicSearch query={ new URLSearchParams(location.search).get('query') } />
  </div>
);

export default Topics;
