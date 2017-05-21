import React from 'react';
import { Redirect } from 'react-router-dom';
import ReactMarkdown from 'react-markdown';
import Button from 'react-toolbox/lib/button/Button.js';
import Chip from 'react-toolbox/lib/chip/Chip.js';
import Input from 'react-toolbox/lib/input/Input.js';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';
import Card from 'react-toolbox/lib/card/Card.js';
import CardTitle from 'react-toolbox/lib/card/CardTitle.js';
import CardText from 'react-toolbox/lib/card/CardText.js';
import CardActions from 'react-toolbox/lib/card/CardActions.js';
import Snackbar from 'react-toolbox/lib/snackbar/Snackbar.js';

import Auth from '../Auth.js';
import Util from '../Util.js';

const PER_PAGE = Util.TOPICS_PER_PAGE;

class TopicCard extends React.Component {
  constructor(props) {
    super(props);
    this.state = {active: false, barActive: false, barLabel: '', confirmActive: false, confirmText: "", confirmActions: [], redirect: -1};
  }

  handleToggle = () => {
    this.setState({active: !this.state.active});
  }

  handleConfirmToggle = (apply) => {
    if (apply === -1) {
      this.setState({confirmActive: !this.state.confirmActive});
    } else {
      this.setState({
        confirmActive: !this.state.confirmActive,
        confirmText: (apply === 1) ? "Are you sure you want to apply to this topic?" : "Are you sure you want to supervise this topic?",
        confirmActions: [
          (apply === 1) ? { label: "Apply", onClick: this.handleApply } : { label: "Supervise", onClick: this.handleSupervise },
          { label: "Cancel", onClick: () => this.handleConfirmToggle(-1) }
        ]
      });
    }
  }

  handleBarToggle = () => {
    this.setState({barActive: !this.state.barActive});
  }

  handleSupervise = () => {
    fetch('/api/topics/' + this.props.id + '/supervise', {
      method: 'put',
      credentials: 'same-origin',
      headers: {
        "Content-Type" : "application/json"
      },
      body: JSON.stringify({ id: this.props.id })
    }).then(function(response) {
      if(response.ok) {
        this.setState({
          barLabel: "Your are now supervising topic ID " + this.props.id,
          barActive: true,
          confirmActive: false
        });
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this));
  }

  handleApply = () => {
    fetch('/api/users/' + Auth.getUserInfo().sub, {
      method: 'get',
      credentials: 'same-origin'
    })
    .then(function(response) {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      if (Util.isEmpty(json.faculty)) {
        this.setState({
          barLabel: "Your request couldn't be processed as don't have a faculty! Please choose a faculty in profile view!",
          barActive: true,
          confirmActive: false
        });
        return;
      }
      fetch('/api/applications', {
        method: 'post',
        credentials: 'same-origin',
        headers: {
          "Content-Type" : "application/json"
        },
        body: JSON.stringify({
          topic: { id: this.props.id },
          faculty: { id: json.faculty.id },
          student: {id: json.id}
        })
      }).then(function(response) {
        if(response.ok) {
          return response.json();
        } else {
          throw new Error('There was a problem with network connection.');
        }
      }).then(function(json2) {
        this.setState({
          barLabel: "Your are now applied to topic ID "+this.props.id,
          barActive: true,
          confirmActive: false
        });

        this.props.redirectHandler(json2.id);
      }.bind(this));
    }.bind(this));
  }

  actions = [
    { label: "Close", onClick: this.handleToggle }
  ];

  render() {
    return(
      <Card>
        <CardTitle
          avatar={this.props.topic.logoUrl}
          title={this.props.topic.title} />
        <CardText>{ this.props.topic.shortAbstract }</CardText>
        <CardText>{ this.props.topic.tags.map( (tag) => <Chip key={tag}> {tag} </Chip> ) }</CardText>
        <CardActions>
          <Button label="Details" onClick={this.handleToggle} />
          { Auth.hasRole(Util.userRoles.student) ? <Button label="Apply" onClick={() => this.handleConfirmToggle(1)} /> : ''}
          { Auth.hasRole(Util.userRoles.superviser) ? <Button label="Supervise" onClick={() => this.handleConfirmToggle(0)} /> : ''}
        </CardActions>
        <Dialog
          actions={this.actions}
          active={this.state.active}
          onEscKeyDown={this.handleToggle}
          onOverlayClick={this.handleToggle}
          title={ this.props.topic.title } >
          <div>
            <span><h4>Short Abstract</h4>{ this.props.topic.shortAbstract }</span>
            <span><h4>Description</h4><ReactMarkdown source={ this.props.topic.description } /></span>
            <span><h4>Tags</h4>{ this.props.topic.tags.map( (tag) => <Chip key={tag}> {tag} </Chip> ) }</span>
          </div>
        </Dialog>
        <Dialog
          actions={this.state.confirmActions}
          active={this.state.confirmActive}
          onEscKeyDown={() => this.handleConfirmToggle(-1)}
          onOverlayClick={() => this.handleConfirmToggle(-1)}
          title="Please confirm your action" >
          <p>{this.state.confirmText}</p>
        </Dialog>
        <Snackbar
          action='Dismiss'
          active={this.state.barActive}
          label={this.state.barLabel}
          timeout={2000}
          onClick={this.handleBarToggle}
          onTimeout={this.handleBarToggle}
          type='warning' />
      </Card>
    )
  }
}

class TopicCards extends React.Component {
  state = { redirect: -1 };

  generateRedirect = () => {
    if (this.state.redirect !== -1) {
      return(<Redirect to={"/applications/"+this.state.redirect} />);
    } else {
      return;
    }
  }

  handleRedirect = (id) => {
    setTimeout(function(){
      this.setState({ redirect: id });
    }.bind(this), 1000);
  }

  render() {
    const topicCards = this.props.topics.map((topic) =>
      <TopicCard topic={topic} key={topic.id} id={topic.id} redirectHandler={(id) => this.handleRedirect(id)} />
    );

    return (
      <div>
        <div className="col-md-12">
          {topicCards}
        </div>
        {this.generateRedirect()}
      </div>
    );
  }
}

class TopicSearch extends React.Component {
  constructor(props) {
    super(props);

    this.state = { query: '', topics: [], page: 0, max: 0, typing: false, fetched: false};
    this.getTopics();
    setInterval(function() {
      if(!this.state.typing && !this.state.fetched)
        this.getTopics();
    }.bind(this), 300);
  }

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value, typing: true});
    setTimeout(function(){
      this.setState({ typing: false, fetched: false });
    }.bind(this), 50);
  };

  getTopics = () => {
    var url = (this.state.query === "") ? "/api/topics?size=" + PER_PAGE + "&start=" + (this.state.page * PER_PAGE)
      : "/api/topics/search?size=" + PER_PAGE + "&start=" + (this.state.page * PER_PAGE) + "&text=" + this.state.query;

    fetch(url, { 
      credentials: 'same-origin',
      method: 'get' }).then(function(response) {
      if(response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      var newData = [];

      for(let i in json) {
        newData.push({
          degrees: json[i].degrees,
          description: json[i].description,
          id: json[i].id,
          shortAbstract: json[i].shortAbstract,
          tags: json[i].tags,
          title: json[i].title
        });
      }

      this.setState({
        topics: newData,
        fetched: true
      });
    }.bind(this));
  }

  changePage = (offset) => {
    if (this.state.page === 0 && offset === -1) return;

    this.setState({ page: this.state.page + offset });

    setTimeout(function() {
      this.getTopics();
    }.bind(this), 2);
  }

  render () {
    return (
      <section className="container">
        <h1>Topic Search</h1>
        <Input type='text' label='What do you have in mind?' name='query' icon='search'
          required value={this.state.query} onChange={this.handleChange.bind(this, 'query')} />
        <br />
        <TopicCards topics={this.state.topics}/>
        <div className="col-md-12">
          <nav aria-label="...">
            <ul className="pager">
              <li><a href="#" onClick={() => this.changePage(-1)}>Previous</a></li>
              <li><a href="#" onClick={() => this.changePage(1)}>Next</a></li>
            </ul>
          </nav>
        </div>
      </section>
    );
  }
}

export default TopicSearch;
