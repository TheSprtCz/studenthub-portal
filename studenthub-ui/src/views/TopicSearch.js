import React from 'react';
import { Redirect } from 'react-router-dom';
import Button from 'react-toolbox/lib/button/Button.js';
import Chip from 'react-toolbox/lib/chip/Chip.js';
import Input from 'react-toolbox/lib/input/Input.js';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';
import Card from 'react-toolbox/lib/card/Card.js';
import CardTitle from 'react-toolbox/lib/card/CardTitle.js';
import CardText from 'react-toolbox/lib/card/CardText.js';
import CardActions from 'react-toolbox/lib/card/CardActions.js';

import TopicDetailsDialog from '../components/TopicDetailsDialog.js';
import Pager from '../components/Pager.js';

import Auth from '../Auth.js';
import Util from '../Util.js';

import _t from '../Translations.js';

class TopicCard extends React.Component {
  state = { confirmActive: false, confirmText: "",
    confirmActions: [], redirect: -1 };

  handleConfirmToggle = (apply) => {
    if (apply === -1) {
      this.setState({confirmActive: !this.state.confirmActive});
    } else {
      this.setState({
        confirmActive: !this.state.confirmActive,
        confirmText: (apply === 1) ? _t.translate("Are you sure you want to apply to this topic?") : _t.translate("Are you sure you want to supervise this topic?"),
        confirmActions: [
          (apply === 1) ? { label: _t.translate("Apply"), onClick: this.handleApply } : { label: _t.translate("Supervise"), onClick: this.handleSupervise },
          { label: _t.translate("Cancel"), onClick: () => this.handleConfirmToggle(-1) }
        ]
      });
    }
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
        Util.notify("info", "", "Your are now supervising topic ID " + this.props.id);
        this.setState({
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
    })
    .then(function(json) {
      if (Util.isEmpty(json.faculty)) {
        Util.notify("error", "There was a problem with network connection.", "Your request hasn't been processed.");
        this.setState({
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
          techLeader: { id: this.props.topic.creator.id },
          student: {id: json.id},
          thesisStarted: new Date(),
          thesisFinish: new Date()
        })
      }).then(function(response) {
        if(response.ok) {
          return response.json();
        } else {
          throw new Error('There was a problem with network connection.');
        }
      }).then(function(json2) {
        Util.notify("success", "", "Your are now applied to topic ID "+this.props.id);
        this.setState({
          confirmActive: false
        });

        this.props.redirectHandler(json2.id);
      }.bind(this));
    }.bind(this));
  }

  isSupervising = () => {
    for (var i = 0; i < this.props.topic.academicSupervisors.length; i++) {
      if (this.props.topic.academicSupervisors[i].id === Auth.getUserInfo().sub)
        return true;
    }
    return false;
  }

  render() {
    return(
      <Card>
        <CardTitle
          avatar={this.props.topic.logoUrl}
          title={this.props.topic.title} />
        <CardText>{ this.props.topic.shortAbstract }</CardText>
        <CardText>{ this.props.topic.tags.map( (tag) => <Chip key={tag}> {tag} </Chip> ) }</CardText>
        <CardActions>
           <TopicDetailsDialog topic={this.props.topic} label={ _t.translate("Details") } />
          { (Auth.hasRole(Util.userRoles.student)) ? <Button label={ _t.translate("Apply") } primary icon='send'
            disabled={this.props.isApplied} onClick={() => this.handleConfirmToggle(1)} /> : '' }
          { (Auth.hasRole(Util.userRoles.superviser)) ? <Button label={ _t.translate("Supervise") } primary icon='supervisor_account'
            disabled={this.isSupervising()} onClick={() => this.handleConfirmToggle(0)} /> : '' }
        </CardActions>
        <Dialog
          actions={this.state.confirmActions}
          active={this.state.confirmActive}
          onEscKeyDown={() => this.handleConfirmToggle(-1)}
          onOverlayClick={() => this.handleConfirmToggle(-1)}
          title={ _t.translate("Please confirm your action") } >
          <p>{this.state.confirmText}</p>
        </Dialog>
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
      <TopicCard topic={topic} key={topic.id} id={topic.id} isApplied={(this.props.applications.indexOf(topic.id) === -1) ? false : true }
        redirectHandler={(id) => this.handleRedirect(id)} />
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
  state = { query: '', topics: [], nextTopics: [], applications: [], supervisedTopics: [],
    page: -1, typing: false, fetched: false };

  componentDidMount() {
    this.getTopics();
    if (Auth.hasRole(Util.userRoles.student)) this.getApplications();
    this.changePage(1);
    setInterval(function() {
      if(!this.state.typing && !this.state.fetched)
        this.resetPages();
    }.bind(this), 300);
  }

  resetPages() {
    this.setState({
      page: -1,
      topics: [],
      nextTopics: []
    });
    setTimeout(function(){
      this.getTopics();
      this.changePage(1);
    }.bind(this), 2);
  }

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value, typing: true});
    setTimeout(function(){
      this.setState({ typing: false, fetched: false });
    }.bind(this), 50);
  };

  getTopics = () => {
    fetch("/api/topics/search?size=" + Util.TOPICS_PER_PAGE + "&start=" + ((this.state.page+1) * Util.TOPICS_PER_PAGE) +
      "&text="+this.state.query, {
      credentials: 'same-origin',
      method: 'get' }).then(function(response) {
      if(response.ok) {
        return response.json();
      } else if (response.status === 404) {
        this.setState({
          topics: (this.state.nextTopics === null || typeof this.state.nextTopics === 'undefined') ? this.state.topics : this.state.nextTopics,
          nextTopics: null
        });
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this)).then(function(json) {
      this.setState({
        topics: (this.state.nextTopics === null || typeof this.state.nextTopics === 'undefined') ? this.state.topics : this.state.nextTopics,
        nextTopics: json
      });
    }.bind(this));
    this.setState({fetched: true});
  }

  getApplications = () => {
    fetch('/api/users/' + Auth.getUserInfo().sub + '/applications', {
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
        newData.push(json[i].topic.id);
      }
      this.setState({
        applications: newData
      });
    }.bind(this));
    this.setState({fetched: true});
  }

  changePage = (offset) => {
    this.setState({ page: this.state.page + offset });

    setTimeout(function() {
      this.getTopics();
    }.bind(this), 2);
  }

  render () {
    return (
      <section className="container">
        <h1>{ _t.translate("Diploma thesis topic search") }</h1>
        <Input type='text' label={ _t.translate("What topics are you interested in?") } name='query' icon='search'
          required value={this.state.query} onChange={this.handleChange.bind(this, 'query')} />
        <br />
        <TopicCards topics={this.state.topics} applications={this.state.applications} />
        <Pager currentPage={this.state.page} nextData={this.state.nextTopics}
          pageChanger={(offset) => this.changePage(offset)} />
      </section>
    );
  }
}

export default TopicSearch;
