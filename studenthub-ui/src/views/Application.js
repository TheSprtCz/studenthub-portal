import React, { Component } from 'react';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';
import DatePicker from 'react-toolbox/lib/date_picker/DatePicker.js';
import Dropdown from 'react-toolbox/lib/dropdown/Dropdown.js';
import Tab from 'react-toolbox/lib/tabs/Tab.js';
import Tabs from 'react-toolbox/lib/tabs/Tabs.js';
import ListCheckbox from 'react-toolbox/lib/list/ListCheckbox.js';
import ReactMarkdown from 'react-markdown';

import AddButton from '../components/AddButton.js';
import EditButton from '../components/EditButton.js';
import DeleteButton from '../components/DeleteButton.js';
import TaskDialog from '../components/TaskDialog.js';
import TopicDetailsDialog from '../components/TopicDetailsDialog.js';

import Util from '../Util.js';
import Auth from '../Auth.js';
import _t from '../Translations.js';

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
        label={ _t.translate('Faculty') }
        onChange={this.handleChange}
        source={this.state.labels}
        value={this.state.value}
        icon='business' />
    );
  }
}

class LeaderSelect extends React.Component {
  state = { value: "", leaders: [] };

  componentDidMount() {
    if (!Util.isEmpty(this.props.currentLeader)) {
      this.setState({
        value: this.props.currentLeader.id,
        leaders: [{
          value: this.props.currentLeader.id,
          label: this.props.currentLeader.email
        }]
      });
    }
    this.getLeaders();
  }

  /**
   * Sets default input values on props change.
   */
  componentWillReceiveProps(nextProps) {
    if (this.props === nextProps) return;

    this.setState({
      value: (Util.isEmpty(nextProps.currentLeader)) ? "" : nextProps.currentLeader.id
    });
  }

  handleChange = (value) => {
    this.setState({value: value});
    this.props.changeHandler({id: value});
  };

  getLeaders() {
    fetch('/api/users', {
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
        if (json[i].roles.indexOf(Util.userRoles.techLeader) !== -1)
          newData.push({
            value: json[i].id,
            label: json[i].email
          });
      }
      this.setState({
        leaders: newData
      });
    }.bind(this));
  }

  render () {
    return (
      <Dropdown
        auto required
        label={ _t.translate("Technical leader") }
        onChange={this.handleChange}
        source={this.state.leaders}
        value={this.state.value}
        icon='code' />
    );
  }
}

class SupervisorSelect extends React.Component {
  state = { value: "", supervisors: [] };

  componentDidMount() {
    if (!Util.isEmpty(this.props.currentSupervisor)) {
      this.setState({
        value: this.props.currentSupervisor.id,
        leaders: [{
          value: this.props.currentSupervisor.id,
          label: this.props.currentSupervisor.email
        }]
      });
    }
    this.getSupervisors();
  }

  /**
   * Sets default input values on props change.
   */
  componentWillReceiveProps(nextProps) {
    if (this.props === nextProps) return;

    this.setState({
      value: (Util.isEmpty(nextProps.currentSupervisor)) ? "" : nextProps.currentSupervisor.id
    });
  }

  handleChange = (value) => {
    this.setState({value: value});
    this.props.changeHandler({id: value});
  };

  getSupervisors() {
    fetch('/api/users', {
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
        if (json[i].roles.indexOf(Util.userRoles.superviser) !== -1)
          newData.push({
            value: json[i].id,
            label: json[i].email
          });
      }
      this.setState({
        supervisors: newData
      });
    }.bind(this));
  }

  render () {
    return (
      <Dropdown
        auto
        label={ _t.translate("Academic supervisor") }
        onChange={this.handleChange}
        source={this.state.supervisors}
        value={this.state.value}
        icon='supervisor_account' />
    );
  }
}

class DegreeSelect extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      value: (Util.isEmpty(this.props.currentDegree)) ? 0: this.props.currentDegree.name,
      labels: []
    };

    this.getDegreeLabels();
  }

  /**
   * Sets default input values on props change.
   */
  componentWillReceiveProps(nextProps) {
    if (this.props === nextProps) return;

    this.setState({
      value: (Util.isEmpty(nextProps.currentDegree)) ? 0 : nextProps.currentDegree.name
    });
  }

  handleChange = (value) => {
    this.setState({value: value});
    this.props.changeHandler({name: value});
  };

  getDegreeLabels() {
    fetch('/api/degrees', {
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
          value: json[i].name,
          label: json[i].description
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
        auto
        label={ _t.translate('Degree') }
        onChange={this.handleChange}
        source={this.state.labels}
        value={this.state.value}
        icon='account_balance' />
    );
  }
}

class ApplicationForm extends Component {
  state = {
    techLeader: {},
    academicSupervisor: {},
    student: {},
    officialAssignment: "",
    grade: "",
    degree: {},
    thesisFinish: "",
    thesisStarted: "",
    link: "",
    topic: {},
    faculty: "",
    index: 0
  };

  handleTabChange = (index) => {
    this.setState({index});
  };

  componentDidMount() {
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
        techLeader: json.techLeader,
        academicSupervisor: json.academicSupervisor,
        student: json.student,
        officialAssignment: json.officialAssignment,
        grade: json.grade,
        degree: json.degree,
        thesisFinish: new Date(json.thesisFinish),
        thesisStarted: new Date(json.thesisStarted),
        topic: json.topic,
        faculty: json.faculty,
        link: json.link
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
        techLeader: this.state.techLeader,
        academicSupervisor: this.state.academicSupervisor,
        officialAssignment: this.state.officialAssignment,
        grade: this.state.grade,
        degree: this.state.degree,
        thesisFinish: this.state.thesisFinish,
        thesisStarted: this.state.thesisStarted,
        topic: this.state.topic,
        faculty: this.state.faculty,
        link: this.state.link
      })
    }).then(function(response) {
      if (response.ok) {
        Util.notify("success", "", "Your application has been successfully updated!");
        this.getData();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this));
  }

  changeLeader = (leader) => {
    this.setState({ techLeader: leader });
  };

  changeSupervisor = (supervisor) => {
    this.setState({ academicSupervisor: supervisor });
  };

  changeDegree = (degree) => {
    this.setState({ degree: degree });
  };

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  };

  render() {
    return(
      <div className="col-md-12">
        {(Util.isEmpty(this.state.topic.title)) ? '' :
        <div className="text-center">
          <TopicDetailsDialog topic={ this.state.topic } label={ _t.translate('Topic details') + ": " + this.state.topic.title} />
          <hr />
        </div>}
        <br />
        <div className="col-md-6">
          <h3>{ _t.translate('Basic info') }</h3>
          <DatePicker
            label={ _t.translate('Start') }
            icon='date_range'
            onChange={this.handleChange.bind(this, 'thesisStarted')}
            value={(Util.isEmpty(this.state.thesisStarted)) ? new Date() : this.state.thesisStarted} />
          <DatePicker
            label={ _t.translate('Finish') }
            icon='date_range'
            onChange={this.handleChange.bind(this, 'thesisFinish')}
            value={(Util.isEmpty(this.state.thesisFinish)) ? new Date() : this.state.thesisFinish} />
          <DegreeSelect currentDegree={this.state.degree} changeHandler={(degree) => this.changeDegree(degree)} />
          <Input
            type='url'
            label={ _t.translate('External link') }
            icon='link'
            onChange={this.handleChange.bind(this, 'link')}
            value={this.state.link} />
        </div>
        <div className="col-md-6">
          <h3>{ _t.translate('Administration') }</h3>
          <FacultySelect changeHandler={this.handleChange.bind(this, 'faculty')} baseValue={this.state.faculty} />
          {(Auth.hasRole(Util.userRoles.admin) || Auth.hasRole(Util.userRoles.techLeader)) ?
            <SupervisorSelect currentSupervisor={this.state.academicSupervisor} changeHandler={(supervisor) => this.changeSupervisor(supervisor)} /> :
            <Input
              type='text'
              label={ _t.translate('Academic supervisor') }
              icon='supervisor_account'
              value={(Util.isEmpty(this.state.academicSupervisor)) ? "" : this.state.academicSupervisor.email} disabled />}
          {(Auth.hasRole(Util.userRoles.admin) || Auth.hasRole(Util.userRoles.techLeader)) ?
            <LeaderSelect currentLeader={this.state.techLeader} changeHandler={(leader) => this.changeLeader(leader)} /> :
            <Input
              type='text'
              label={ _t.translate('Technical leader') }
              icon='code'
              value={(Util.isEmpty(this.state.techLeader)) ? "" : this.state.techLeader.email} disabled />}
          <Dropdown
            name='grade'
            label={ _t.translate('Grade') }
            icon='grade'
            disabled={ Auth.hasRole(Util.userRoles.student) ? true : false }
            onChange={this.handleChange.bind(this, 'grade')}
            source={Util.gradesSource}
            value={this.state.grade} />
        </div>
        {/* Official Assignment tabs */}
        <div className='col-md-12'>
          <Tabs index={this.state.index} onChange={this.handleTabChange}>
            <Tab label={ _t.translate('Official Assignment') }>
              <Input
                type='text'
                label={ _t.translate('Official Assignment') }
                multiline rows={14}
                icon='assignment'
                value={(Util.isEmpty(this.state.officialAssignment)) ? "" : this.state.officialAssignment}
                onChange={this.handleChange.bind(this, 'officialAssignment')} />
            </Tab>
            <Tab label={ _t.translate('Preview') }>
              <ReactMarkdown source={ this.state.officialAssignment } />
            </Tab>
          </Tabs>
        </div>
        <Button icon='save' label={ _t.translate('Save changes') } raised primary className='pull-right' onClick={this.handleSubmit} />
      </div>
    )
  }
}

class TaskList extends React.Component {
  state = { tasks: [], app: { }, dialogActive: false };

  componentDidMount() {
    this.getApp();
    this.getTasks();
  }

  getApp = () => {
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
        app: json
      });
    }.bind(this));
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
          deadline: new Date(json[i].deadline),
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
    fetch('/api/tasks/'+this.state.tasks[id].id, {
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
        Util.notify("success", "", "Task status changed successfully!");
        this.getTasks();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this));
  }

  handleDelete = (id) => {
    fetch('/api/tasks/'+id, {
      method: 'delete',
      credentials: 'same-origin'
      }).then(function(response) {
      if (response.ok) {
        Util.notify("success", "", "The task has been deleted successfully!");
        this.getTasks();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this));
  }

  /**
   * Toggles the visiblity of the Dialog.
   * @param id     the id of the task to edit
   * @param label  new notification label
   */
  toggleDialog = (id, label) => {
    this.setState({
      dialogActive: !this.state.dialogActive,
      editId: id
    })
    if(label === "") return;
    else {
      Util.notify("info", "", label);
      this.getTasks();
    }
  }

  render () {
    return (
      <div>
        <table width="100%">
          <tbody>
            <tr>
              <td colSpan="2">
                <AddButton toggleHandler={() => this.toggleDialog(-1, "")} />
              </td>
            </tr>
            {this.state.tasks.map( (task, index) => (
            <tr key={task.id}>
              <td width="80%">
                  <ListCheckbox
                    key={task.id}
                    caption={task.title}
                    checked={task.completed}
                    legend={(Util.isEmpty(task.deadline)) ? _t.translate('Has no deadline') : task.deadline.toString()}
                    onChange={() => this.handleCompletionChange(index)} ripple selectable />
              </td>
              <td width="20%">
                <span className="pull-right">
                  <EditButton toggleHandler={() => this.toggleDialog(index, "")} />
                  <DeleteButton deleteHandler={() => this.handleDelete(task.id)} />
                </span>
              </td>
            </tr>
            ))}
          </tbody>
        </table>
        <TaskDialog active={this.state.dialogActive} task={(this.state.editId === -1) ? null : this.state.tasks[this.state.editId]}
          toggleHandler={(label) => this.toggleDialog(this.state.editId, label)} app={this.state.app} />
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
        <Tab label={ _t.translate('Application details') }><ApplicationForm id={this.props.id} /></Tab>
        <Tab label={ _t.translate('Task list') }><TaskList id={this.props.id} /></Tab>
      </Tabs>
    );
  }
}

const Application = ({ match }) => (
  <div>
    <h1>{ _t.translate('Application details') }</h1>
    <TopicApplicationDetails id={match.params.id} />
  </div>
);

export default Application;
