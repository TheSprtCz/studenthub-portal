function Home() {
  return (
    <div className="container">
      <h2>Topics</h2>
      <p>Search for topics</p>
      <TopicTable />
    </div>
  );
}

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

function Topic(props) {
  return (
    <div>
      <h3>{props.topic.title}</h3>
      <p>{props.topic.shortAbstract}</p>
    </div>
  );
}

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
