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

import DegreeSelect from '../components/DegreeSelect.js';
import TopicDetailsDialog from '../components/TopicDetailsDialog.js';
import Pager from '../components/Pager.js';

import Auth from '../Auth.js';
import Util from '../Util.js';

import _t from '../Translations.js';

class TopicCard extends React.Component {

  state = {
    redirect: -1,
    applicationDegree: {},
    superviseDialogActive: false,
    superviseDialogActions: [
      { label: _t.translate("Supervise"), onClick: () => this.handleSupervise() },
      { label: _t.translate("Cancel"), onClick: () => this.toggleSuperviseDialog() }
    ],
    applicationDialogActive: false,
    applicationDialogActions: [
      { label: _t.translate("Apply"), onClick: () => this.handleApply() },
      { label: _t.translate("Cancel"), onClick: () => this.toggleApplicationDialog() }
    ]
  };

  changeDegree = (degree) => {
    this.setState({ applicationDegree: degree });
    console.log('Changing degree to: ' + degree);
  }

  toggleSuperviseDialog = () => {
    this.setState({ superviseDialogActive: !this.state.superviseDialogActive });
  }

  toggleApplicationDialog = () => {
    this.setState({ applicationDialogActive: !this.state.applicationDialogActive });
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
        this.setState({ superviseDialogActive: false });
      } else {
        Util.notify('error', response.statusText, 'Oops! Something went wrong. Please contact administrator.');
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
        Util.notify('error', response.statusText, 'Oops! Something went wrong. Please contact administrator.');
      }
    })
    .then(function(json) {
      if (Util.isEmpty(json.faculty)) {
        Util.notify("error", "There was a problem with network connection.", "Your request hasn't been processed.");
        this.setState({ applicationDialogActive: false });
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
          degree: this.state.applicationDegree
        })
      }).then(function(response) {
        if(response.ok) {
          return response.json();
        } else {
          throw new Error('There was a problem with network connection.');
        }
      }).then(function(json2) {
        Util.notify("success", "", "Your are now applied to topic ID " + this.props.id);
        this.setState({ applicationDialogActive: false });
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
            disabled={this.props.isApplied} onClick={ this.toggleApplicationDialog } /> : '' }
          { (Auth.hasRole(Util.userRoles.superviser)) ? <Button label={ _t.translate("Supervise") } primary icon='supervisor_account'
            disabled={this.isSupervising()} onClick={ this.toggleSuperviseDialog } /> : '' }
        </CardActions>
        {/* Dialog for supervising for Topic */}
        <Dialog
          actions={ this.state.superviseDialogActions }
          active={ this.state.superviseDialogActive }
          onEscKeyDown={ this.toggleSuperviseDialog }
          onOverlayClick={ this.toggleSuperviseDialog }
          title={ _t.translate("Please confirm your action") } >
          <div>
            <p>{ _t.translate("Are you sure you want to supervise this topic?") }</p>
          </div>
        </Dialog>
        {/* Dialog for applying Topic */}
        <Dialog
          actions={ this.state.applicationDialogActions }
          active={ this.state.applicationDialogActive }
          onEscKeyDown={ this.toggleApplicationDialog }
          onOverlayClick={ this.toggleApplicationDialog }
          title={ _t.translate("Please confirm your action") } >
          <div>
            <p>{ _t.translate("Are you sure you want to apply to this topic?") }</p>
            <DegreeSelect currentDegree={this.state.applicationDegree} changeHandler={(degree) => this.changeDegree(degree)} />
          </div>
        </Dialog>
      </Card>
    )
  }
}

class TopicCards extends React.Component {
  state = { redirect: -1 };

  generateRedirect = () => {
    if (this.state.redirect !== -1) {
      return(<Redirect to={"/application?id="+this.state.redirect} />);
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
  state = { query: '', topics: [], applications: [], supervisedTopics: [],
    page: 0, pages: 1, typing: false, fetched: false };

  componentDidMount() {
    this.getTopics();
    if (Auth.hasRole(Util.userRoles.student)) this.getApplications();
    setInterval(function() {
      if (!this.state.typing && !this.state.fetched)
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
    fetch("/api/topics/search?size=" + Util.TOPICS_PER_PAGE + "&start=" +
          (this.state.page * Util.TOPICS_PER_PAGE) + "&text="+this.state.query, {
      credentials: 'same-origin',
      method: 'get' })
    .then(function(response) {
      if (response.ok) {
        this.setState({pages: parseInt(response.headers.get("Pages"), 10)});
        console.log(this.state.pages);
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this)).then(function(json) {
      this.setState({topics: json});
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

  changePage = (page) => {
    this.setState({page: page.selected});

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
        <Pager pages={this.state.pages} pageChanger={(page) => this.changePage(page)} />
      </section>
    );
  }
}

export default TopicSearch;
