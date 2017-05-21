import React, { Component } from 'react';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';
import DatePicker from 'react-toolbox/lib/date_picker/DatePicker.js';
import Dropdown from 'react-toolbox/lib/dropdown/Dropdown.js';
import Tab from 'react-toolbox/lib/tabs/Tab.js';
import Tabs from 'react-toolbox/lib/tabs/Tabs.js';
import List from 'react-toolbox/lib/list/List.js';
import ListSubHeader from 'react-toolbox/lib/list/ListSubHeader.js';
import ListCheckbox from 'react-toolbox/lib/list/ListCheckbox.js';

import SiteSnackbar from '../components/SiteSnackbar.js';

import Util from '../Util.js';
import Auth from '../Auth.js';

class FacultySelect extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      value: (Util.isEmpty(this.props.baseValue)) ? 0: this.props.baseValue.id,
      labels: []
    };

    this.getFacultyLabels();
  }

  /**
   * Sets default input values on props change.
   */
  componentWillReceiveProps(nextProps) {
    if (this.props === nextProps) return;

    this.setState({
      value: (Util.isEmpty(nextProps.baseValue)) ? 0 : nextProps.baseValue.id
    });
  }

  handleChange = (value) => {
    this.setState({value: value});
    this.props.changeHandler({id: value});
  };

  getFacultyLabels() {
    fetch('/api/faculties', {
      credentials: 'same-origin',
      method: 'get'
    }).then(function(response) {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      var newData = [];

      for(let i in json) {
        newData.push({
          value: json[i].id,
          label: json[i].name
        });
      }
      this.setState({
        labels: newData
      });
    }.bind(this));
  }

  render () {
    return (
      <Dropdown
        auto required
        label='Faculty'
        onChange={this.handleChange}
        source={this.state.labels}
        value={this.state.value}
        icon='business' />
    );
  }
}

class ApplicationForm extends Component {
  constructor(props) {
    super(props);

    this.state = {
      student: "",
      officialAssignment: "",
      grade: "",
      degree: "",
      thesisFinish: "",
      thesisStarted: "",
      topic: "",
      faculty: "",
      academicSupervisor: "",
      techLeader: "",
      snackbarLabel: "",
      snackbarActive: false
    };
    this.getData();
  }

  getData = () => {
    fetch('/api/applications/' + this.props.id, {
      method: 'get',
      credentials: 'same-origin'
    }).then(function(response) {
      if(response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      this.setState({
        student: json.student,
        officialAssignment: json.officialAssignment,
        grade: json.grade,
        degree: json.degree,
        thesisFinish: json.thesisFinish,
        thesisStarted: json.thesisStarted,
        topic: json.topic,
        faculty: json.faculty,
        academicSupervisor: json.academicSupervisor,
        techLeader: json.techLeader
      });
    }.bind(this));
  }

  handleSubmit = () => {
    fetch('/api/applications/' + this.props.id, {
      method: 'put',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        student: this.state.student,
        officialAssignment: this.state.officialAssignment,
        grade: this.state.grade,
        degree: this.state.degree,
        thesisFinish: this.state.thesisFinish,
        thesisStarted: this.state.thesisStarted,
        topic: this.state.topic,
        faculty: this.state.faculty
      })
    }).then(function(response) {
      if (response.ok) {
        this.setState({
          snackbarActive: true,
          snackbarLabel: "Your application has been successfully updated!"
        });
        this.getData();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {});
  }

  handleLead = () => {
    fetch('/api/applications/' + this.props.id, {
      method: 'put',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        techLeader: {id: Auth.getUserInfo().sub.substring(Auth.getUserInfo().sub.indexOf("#")+1)},
        student: this.state.app.student,
        topic: this.state.app.topic,
        faculty: this.state.app.faculty
      })
    }).then(function(response) {
      if (response.ok) {
        this.setState({
          snackbarActive: true,
          snackbarLabel: "You are now leading this application!"
        });
        this.getData();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {});
  };

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  };

  toggleSnackbar = () => {
    this.setState({ snackbarActive: !this.state.snackbarActive });
  };

  render() {
    return(
      <div className="col-md-12">
        <div>Topic: <a href="#">{ this.state.topic.title }</a></div>
        <br />
        <div className="col-md-6">
          <h3>Basic information</h3>
          <DatePicker
            label='Thesis started'
            onChange={this.handleChange.bind(this, 'thesisStarted')}
            value={(Util.isEmpty(this.state.thesisStarted)) ? "" : this.state.thesisStarted} />
          <DatePicker
            label='Thesis finish'
            onChange={this.handleChange.bind(this, 'thesisFinish')}
            value={(Util.isEmpty(this.state.thesisFinish)) ? "" : this.state.thesisFinish} />
          <Input
            type='text'
            label='Official Assignment'
            multiline rows={8}
            value={(Util.isEmpty(this.state.officialAssignment)) ? "" : this.state.officialAssignment}
            onChange={this.handleChange.bind(this, 'officialAssignment')} />
        </div>
        <div className="col-md-6">
          <h3>Administration</h3>
          <FacultySelect changeHandler={this.handleChange.bind(this, 'faculty')} baseValue={this.state.faculty} />
          <Input
            type='text'
            label='Academic Supervisor'
            value={(Util.isEmpty(this.state.academicSupervisor)) ? "" : this.state.academicSupervisor.email}
            onChange={this.handleChange.bind(this, 'academicSupervisor')} disabled />
          <Input
            type='text'
            label='Tech Leader'
            value={(Util.isEmpty(this.state.techLeader)) ? "" : this.state.techLeader.email}
            onChange={this.handleChange.bind(this, 'techLeader')} disabled />
          <Dropdown
            name='grade'
            label='Grade'
            onChange={this.handleChange.bind(this, 'grade')}
            source={Util.gradesSource}
            value={this.state.grade} />
          <Dropdown
            name='degree'
            label='Degree'
            onChange={this.handleChange.bind(this, 'degree')}
            source={Util.degreesSource}
            value={this.state.degree} />
        </div>
        {(Auth.hasRole("TECH_LEADER") && (this.state.techLeader === null || typeof this.state.techLeader === 'undefined')) ? <Button icon='person_add' label='Lead this application' raised primary className='pull-right' onClick={this.handleLead}/> : '' }
        <Button icon='save' label='Save changes' raised primary className='pull-right' onClick={this.handleSubmit} />
        <SiteSnackbar active={this.state.snackbarActive} label={this.state.snackbarLabel} toggleHandler={() => this.toggleSnackbar()} />
      </div>
    )
  }
}

class TaskList extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      tasks: [],
      snackbarLabel: "",
      snackbarActive: false
    };
    this.getTasks();
  }

  getTasks = () => {
    fetch('/api/applications/' + this.props.id + '/tasks', {
      credentials: 'same-origin',
      method: 'get'
    }).then(function(response) {
      if(response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      var newData = [];

      for(let i in json) {
        newData.push({
          application: json[i].application,
          completed: json[i].completed,
          deadline: json[i].deadline,
          id: json[i].id,
          title: json[i].title
        });
      }
      this.setState({
        tasks: newData
      });
    }.bind(this));
  }

  handleCompletionChange = (id) => {
    fetch('/api/applications/' + this.props.id + '/tasks', {
      method: 'put',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        application: this.state.tasks[id].application,
        completed: !this.state.tasks[id].completed,
        deadline: this.state.tasks[id].deadline,
        id: this.state.tasks[id].id,
        title: this.state.tasks[id].title
      })
    }).then(function(response) {
      if (response.ok) {
        this.setState({
          snackbarLabel: "Task status changed successfully!",
          snackbarActive: false
        });
        this.getTasks();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this));
  }

  render () {
    return (
      <div>
        <List>
          <ListSubHeader caption='TODO List' />
          {this.state.tasks.map( (task, index) => (
            <ListCheckbox
              caption={task.title}
              checked={task.completed}
              legend={task.deadline}
              onChange={() => this.handleCompletionChange(index)} />
          ))}
        </List>
        <SiteSnackbar active={this.state.snackbarActive} label={this.state.snackbarLabel} toggleHandler={() => this.toggleSnackbar()} />
      </div>
    );
  }
}

class TopicApplicationDetails extends Component {
  state = { index: 0 };

  handleTabChange = (index) => {
    this.setState({index});
  };

  render () {
    return (
      <Tabs index={this.state.index} onChange={this.handleTabChange}>
        <Tab label='Application Details'><ApplicationForm id={this.props.id} /></Tab>
        <Tab label='Task List'><TaskList id={this.props.id} /></Tab>
      </Tabs>
    );
  }
}

const Application = ({ match }) => (
  <div>
    <h1>Application details</h1>
    <TopicApplicationDetails id={match.params.id} />
  </div>
);

export default Application;
