import React, { Component } from 'react';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';
import Tab from 'react-toolbox/lib/tabs/Tab.js';
import Tabs from 'react-toolbox/lib/tabs/Tabs.js';
import List from 'react-toolbox/lib/list/List.js';
import ListItem from 'react-toolbox/lib/list/ListItem.js';
import ReactMarkdown from 'react-markdown';

import Util from '../Util.js';
import _t from '../Translations.js';

class CompanyList extends React.Component {
  state = {project: {companies: []}, active: false, deleteId: -1};

  actions = [
    { label: _t.translate("Delete"), onClick: () => this.removeCompany() },
    { label: _t.translate("Cancel"), onClick: () => this.handleToggle(-1) }
  ]

  componentDidMount() {
    this.getProject();
  }

  getProject = () => {
    fetch('/api/projects/' + this.props.id, {
      method: 'get',
      credentials: 'same-origin'
    }).then(function(response) {
      if(response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      this.setState({project: json});
    }.bind(this));
  }

  removeCompany = () => {
    var companies = this.state.project.companies;
    companies.splice(this.state.deleteId, 1);
    fetch('/api/projects/' + this.props.id, {
      method: 'put',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        companies: companies,
        name: this.state.project.name,
        description: this.state.project.description,
        faculties: this.state.project.faculties,
        creators: this.state.project.creators,
        topics: this.state.project.topics
      })
      }).then(function(response) {
      if (response.ok) {
        // Util.notify("success", "", "The company has been succesfully removed from the project!");
        this.getProject();
        this.setState({deleteId: -1});
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this));
  }

  handleToggle = (id) => {
    this.setState({ active: !this.state.active, deleteId: id });
  }

  render () {
    if (this.state.project.companies.length < 1)
      return (<div>N/A</div>);

    return (
      <div>
        <List>
          {this.state.project.companies.map( (item, index) => (
            <ListItem
              key={item.id}
              caption={item.name}
              legend={item.city + ", " + item.country}
              rightIcon="delete"
              onClick={() => this.handleToggle(index)}
              ripple selectable />
          ))}
        </List>
        <Dialog
          actions={this.actions}
          active={this.state.active}
          onEscKeyDown={() => this.handleToggle(-1)}
          onOverlayClick={() => this.handleToggle(-1)}
          title={ _t.translate("Are you sure you want to proceed?") }>
          <p>{ _t.translate("This company will be removed from the project.") }</p>
        </Dialog>
      </div>
    );
  }
}

class FacultyList extends React.Component {
  state = {project: {faculties: []}, active: false, deleteId: -1};

  actions = [
    { label: _t.translate("Delete"), onClick: () => this.removeFaculty() },
    { label: _t.translate("Cancel"), onClick: () => this.handleToggle(-1) }
  ]

  componentDidMount() {
    this.getProject();
  }

  getProject = () => {
    fetch('/api/projects/' + this.props.id, {
      method: 'get',
      credentials: 'same-origin'
    }).then(function(response) {
      if(response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      this.setState({project: json});
    }.bind(this));
  }

  removeFaculty = () => {
    var faculties = this.state.project.faculties;
    faculties.splice(this.state.deleteId, 1);
    fetch('/api/projects/' + this.props.id, {
      method: 'put',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        faculties: faculties,
        name: this.state.project.name,
        description: this.state.project.description,
        companies: this.state.project.companies,
        creators: this.state.project.creators,
        topics: this.state.project.topics
      })
      }).then(function(response) {
      if (response.ok) {
        // Util.notify("success", "", "The faculty has been succesfully removed from the project!");
        this.getProject();
        this.setState({deleteId: -1});
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this));
  }

  handleToggle = (id) => {
    this.setState({ active: !this.state.active, deleteId: id });
  }

  render () {
    if (this.state.project.faculties.length < 1)
      return (<div>N/A</div>);

    return (
      <div>
        <List>
          {this.state.project.faculties.map( (item, index) => (
            <ListItem
              key={item.id}
              caption={item.name}
              legend={item.university.name}
              rightIcon="delete"
              onClick={() => this.handleToggle(index)}
              ripple selectable />
          ))}
        </List>
        <Dialog
          actions={this.actions}
          active={this.state.active}
          onEscKeyDown={() => this.handleToggle(-1)}
          onOverlayClick={() => this.handleToggle(-1)}
          title={ _t.translate("Are you sure you want to proceed?") }>
          <p>{ _t.translate("This faculty will be removed from the project.") }</p>
        </Dialog>
      </div>
    );
  }
}

class LeaderList extends React.Component {
  state = {leaders: []};

  componentDidMount() {
    this.getLeaders();
  }

  getLeaders = () => {
    fetch('/api/projects/' + this.props.id + '/creators', {
      method: 'get',
      credentials: 'same-origin'
    }).then(function(response) {
      if(response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      this.setState({leaders: json});
    }.bind(this));
  }

  render () {
    return (
      <div>
        <List>
          {this.state.leaders.map( (item, index) => (
            <ListItem
              key={item.id}
              caption={item.name}
              legend={item.email}
              ripple />
          ))}
        </List>
      </div>
    );
  }
}

class TopicList extends React.Component {
  state = {project: {topics: []}, active: false, deleteId: -1};

  actions = [
    { label: _t.translate("Delete"), onClick: () => this.removeTopic() },
    { label: _t.translate("Cancel"), onClick: () => this.handleToggle(-1) }
  ]

  componentDidMount() {
    this.getProject();
  }

  getProject = () => {
    fetch('/api/projects/' + this.props.id, {
      method: 'get',
      credentials: 'same-origin'
    }).then(function(response) {
      if(response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      this.setState({project: json});
    }.bind(this));
  }

  removeTopic = () => {
    var topics = this.state.project.topics;
    topics.splice(this.state.deleteId, 1);
    fetch('/api/projects/' + this.props.id, {
      method: 'put',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        topics: topics,
        name: this.state.project.name,
        description: this.state.project.description,
        companies: this.state.project.companies,
        faculties: this.state.project.faculties,
        creators: this.state.project.creators,
      })
      }).then(function(response) {
      if (response.ok) {
        // Util.notify("success", "", "The topic has been succesfully removed from the project!");
        this.getProject();
        this.setState({deleteId: -1});
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this));
  }

  handleToggle = (id) => {
    this.setState({ active: !this.state.active, deleteId: id });
  }

  render () {
    if (this.state.project.topics.length < 1)
      return (<div>N/A</div>);

    return (
      <div>
        <List>
          {this.state.project.topics.map( (item, index) => (
            <ListItem
              key={item.id}
              caption={item.title}
              legend={item.shortAbstract}
              rightIcon="delete"
              onClick={() => this.handleToggle(index)}
              ripple selectable />
          ))}
        </List>
        <Dialog
          actions={this.actions}
          active={this.state.active}
          onEscKeyDown={() => this.handleToggle(-1)}
          onOverlayClick={() => this.handleToggle(-1)}
          title={ _t.translate("Are you sure you want to proceed?") }>
          <p>{ _t.translate("This topic will be removed from the project.") }</p>
        </Dialog>
      </div>
    );
  }
}

class ProjectForm extends Component {
  state = {
    project: { },
    name: "",
    description: "",
    companies: [],
    faculties: [],
    index: 0
  };

  componentDidMount() {
    this.getProject();
  }

  handleTabChange = (index) => {
    this.setState({index});
  };

  getProject = () => {
    fetch('/api/projects/' + this.props.id, {
      method: 'get',
      credentials: 'same-origin'
    }).then(function(response) {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      this.setState({
        project: json,
        name: json.name,
        description: json.description,
        companies: json.companies,
        faculties: json.faculties,
      });
    }.bind(this));
  }

  handleSubmit = () => {
    fetch('/api/projects/' + this.props.id, {
      method: 'put',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        name: this.state.name,
        description: this.state.description,
        companies: this.state.project.companies,
        faculties: this.state.project.faculties,
        creators: this.state.project.creators,
        topics: this.state.project.topics
      })
    }).then(function(response) {
      if (response.ok) {
        // Util.notify("success", "", "The project has been succesfully edited!");
        this.getProject();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this));
  }

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  };

  render() {
    return(
      <div className="col-md-12">
        <Input type='text' label="name" hint='Name of the project' icon="textsms"
               required value={this.state.name} onChange={(value) => this.handleChange("name", value)} />
        {/* Description tabs */}
        <div className='col-md-12'>
          <Tabs index={this.state.index} onChange={this.handleTabChange}>
            <Tab label={ _t.translate('Description') }>
              <Input
                type='text'
                label={ _t.translate('Description') }
                multiline rows={10}
                icon='description'
                value={(Util.isEmpty(this.state.description)) ? "" : this.state.description}
                onChange={this.handleChange.bind(this, 'description')} />
            </Tab>
            <Tab label={ _t.translate('Preview') }>
              <ReactMarkdown source={ this.state.description } />
            </Tab>
          </Tabs>
        </div>
        <Button icon='save' label={ _t.translate('Save changes') } raised primary className='pull-right' onClick={this.handleSubmit} />
      </div>
    )
  }
}

class ProjectDetails extends Component {
  state = { index: 0 };

  handleTabChange = (index) => {
    this.setState({index});
  };

  render () {
    return (
      <Tabs index={this.state.index} onChange={this.handleTabChange}>
        <Tab label={ _t.translate('Project details') }><ProjectForm id={this.props.id} /></Tab>
        <Tab label={ _t.translate('Companies') }><CompanyList id={this.props.id} /></Tab>
        <Tab label={ _t.translate('Faculties') }><FacultyList id={this.props.id} /></Tab>
        <Tab label={ _t.translate('Leaders') }><LeaderList id={this.props.id} /></Tab>
        <Tab label={ _t.translate('Topics') }><TopicList id={this.props.id} /></Tab>
      </Tabs>
    );
  }
}

const Project = ({ match }) => (
  <div>
    <h1>{ _t.translate('Project details') }</h1>
    <ProjectDetails id={match.params.id} />
  </div>
);

export default Project;
