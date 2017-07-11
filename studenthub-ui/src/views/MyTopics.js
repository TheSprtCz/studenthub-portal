import React, { Component } from 'react';
import ReactMarkdown from 'react-markdown';
import Table from 'react-toolbox/lib/table/Table.js';
import TableHead from 'react-toolbox/lib/table/TableHead.js';
import TableRow from 'react-toolbox/lib/table/TableRow.js';
import TableCell from 'react-toolbox/lib/table/TableCell.js';
import Tab from 'react-toolbox/lib/tabs/Tab.js';
import Tabs from 'react-toolbox/lib/tabs/Tabs.js';
import Checkbox from 'react-toolbox/lib/checkbox/Checkbox.js';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';
import Chip from 'react-toolbox/lib/chip/Chip.js';

import Pager from '../components/Pager.js';
import DeleteButton from '../components/DeleteButton.js';
import EditButton from '../components/EditButton.js';
import SiteSnackbar from '../components/SiteSnackbar.js';

import Auth from '../Auth.js';
import Util from '../Util.js';
import _t from '../Translations.js';

const TopicTableHint = () => (
  <div className="alert alert-info alert-dismissible" role="alert" style={{ marginTop: '1em'}}>
    <button type="button" className="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
    <strong>{ _t.translate('Hint:') }</strong> { _t.translate('Here you can modify your topics.') }
  </div>
)

class TopicTable extends Component {
  state = {topics: [], dialogActive: false, editId: -1, snackbarLabel: "",
    snackbarActive: false, page: 0, pages: 1}

  componentDidMount() {
    if (Auth.hasRole(Util.userRoles.companyRep) && !Auth.hasRole(Util.userRoles.admin))
      this.getCompanyTopics();
    else
      this.getTopics();
  }

  /**
   * Gets the list of all company topics.
   */
  getCompanyTopics = () => {
    fetch('/api/users/' + Auth.getUserInfo().sub, {
        credentials: 'same-origin',
        method: 'get'
      }).then(function(response) {
        if (response.ok) {
            return response.json();
        } else {
          throw new Error('There was a problem with network connection.');
        }
      }).then(function(json) {
        if (Util.isEmpty(json.company)) {
          this.setState({
            snackbarLabel: "There is no company connected to your account!",
            snackbarActive: true
          });
          return;
        }
        fetch('/api/companies/' + json.company.id + "/topics?size=" + Util.TOPICS_PER_PAGE_TABLE + "&start=" +
          (this.state.page * Util.TOPICS_PER_PAGE_TABLE), {
            credentials: 'same-origin',
            method: 'get'
        }).then(function(response) {
          if (response.ok) {
            this.setState({pages: parseInt(response.headers.get("Pages"), 10)});
            return response.json();
          } else {
            throw new Error('There was a problem with network connection.');
          }
        }.bind(this)).then(function(json) {
          this.setState({topics: json});
        }.bind(this));
      }.bind(this));
  }

  /**
   * Gets the list of all user topics.
   */
  getTopics = () => {
    fetch((Auth.hasRole(Util.userRoles.admin)) ? "/api/topics?size=" + Util.TOPICS_PER_PAGE_TABLE +
      "&start=" + (this.state.page * Util.TOPICS_PER_PAGE_TABLE) : "/api/users/" +
      Auth.getUserInfo().sub + "/ownedTopics?size=" + Util.TOPICS_PER_PAGE_TABLE +
      "&start=" + (this.state.page * Util.TOPICS_PER_PAGE_TABLE), {
        credentials: 'same-origin',
        method: 'get'
    }).then(function(response) {
      if (response.ok) {
        this.setState({pages: parseInt(response.headers.get("Pages"), 10)});
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this)).then(function(json) {
      this.setState({topics: json});
    }.bind(this));
  }

  /**
   * Handles all nonGET server requests. Updates using getting methods afterwards.
   * @param  method     method to call
   * @param  id         item id
   * @param  data       item body
   */
  manageData = (method, id, data) => {
    fetch('/api/topics/' + id, {
      method: method,
      credentials: 'same-origin',
      headers: {
        "Content-Type" : "application/json" },
      body: data
    }).then(function(response) {
      if(response.ok) {
          var label = "";
          switch(method.toLowerCase()) {
            case "post":
              label = "The topic has been succesfully created!";
              break;
            case "put":
              label = "The topic has been succesfully updated!";
              break;
            case "delete":
              label = "The topic has been succesfully removed!";
              break;
            default:
              label = "Wrong method input!";
              return;
          }
          this.setState({
            snackbarLabel: label,
            snackbarActive: true
          });
          if (Auth.hasRole(Util.userRoles.companyRep) && !Auth.hasRole(Util.userRoles.admin))
            this.getCompanyTopics();
          else
            this.getTopics();
      } else {
        this.setState({
          snackbarLabel: "An error occured! Your request couldn't be processed. It's possible that you have a problem with your internet connection or that the server is not responding.",
          snackbarActive: true
        });
        throw new Error('There was a problem with network connection. '+method.toUpperCase()+' could not be processed!');
      }
    }.bind(this));
  }

  toggleDialog = (id) => {
    this.setState({dialogActive: !this.state.dialogActive, editId: id});
  }

  toggleSnackbar = () => {
    this.setState({snackbarActive: !this.state.snackbarActive});
  }

  changePage = (page) => {
    this.setState({page: page.selected});

    setTimeout(function() {
      if (Auth.hasRole(Util.userRoles.companyRep) && !Auth.hasRole(Util.userRoles.admin))
        this.getCompanyTopics();
      else
        this.getTopics();
    }.bind(this), 2);
  }

  render () {
    return (
      <div>
        <TopicTableHint />
        <h1>
          { _t.translate('My Topics') } { Auth.hasRole(Util.userRoles.techLeader) ?
            <NewTopicDialog active={this.state.dialogActive} dataHandler={(method, id, data) =>
              this.manageData(method, id, data)} topic={(this.state.editId === -1) ? -1 :
              this.state.topics[this.state.editId]} toggleHandler={() => this.toggleDialog(-1)}/> : '' }
        </h1>
        <Table selectable={false}>
          <TableHead>
            <TableCell>{ _t.translate('Topic title') }</TableCell>
            <TableCell>{ _t.translate('Short abstract') }</TableCell>
            <TableCell>{ _t.translate('Degrees') }</TableCell>
            <TableCell>{ _t.translate('Tags') }</TableCell>
            <TableCell>{ _t.translate('Actions') }</TableCell>
          </TableHead>
          {this.state.topics.map((item, index) => (
            <TableRow key={item.id}>
              <TableCell><strong>{item.title}</strong></TableCell>
              <TableCell>{item.shortAbstract}</TableCell>
              <TableCell>{item.degrees.map( (degree) => <Chip key={degree}> {degree} </Chip> )}</TableCell>
              <TableCell>{item.tags.map( (tag) => <Chip key={tag}> {tag} </Chip> )}</TableCell>
              {(Auth.hasRole(Util.userRoles.companyRep) && !Auth.hasRole(Util.userRoles.admin)) ? '' :
              <TableCell>
                <EditButton toggleHandler={() => this.toggleDialog(index)} />
                <DeleteButton deleteHandler={() => this.manageData("delete", item.id, null)} />
              </TableCell>}
            </TableRow>
          ))}
        </Table>
        <SiteSnackbar active={this.state.snackbarActive} label={this.state.snackbarLabel}
          toggleHandler={() => this.toggleSnackbar()} />
        <Pager pages={this.state.pages} pageChanger={(page) => this.changePage(page)} />
      </div>
    );
  }
}

class NewTopicDialog extends Component {
  state = {
    bachelor: false, master: false, phd: false, highSchool: false, title: '', enabled: true,
    shortAbstract: '', description: '', tags: '', tagIndex: 0, dialogTitle: _t.translate('New Topic'),
    actions : [
      { label: _t.translate('Add'), onClick: () => this.handleAdd()},
      { label: _t.translate('Cancel'), onClick: () => this.handleToggle() }
    ]
  };

  /**
   * Sets default input values on props change.
   */
  componentWillReceiveProps(nextProps) {
    if(this.props === nextProps) return;

    var bachelorState = false;
    var masterState = false;
    var phdState = false;
    var highSchoolState = false;
    var enabledState = true;
    var titleState;
    var shortAbstractState;
    var descriptionState;
    var tagsState = "";
    var dialogTitleState;
    var actionsState;

    if(nextProps.topic === -1) {
      titleState = "";
      shortAbstractState = "";
      descriptionState = "";
      dialogTitleState = _t.translate('New Topic');
      actionsState = [
        { label: _t.translate('Add'), onClick: () => this.handleAdd()},
        { label: _t.translate('Cancel'), onClick: () => this.handleToggle() }
      ];
    }
    else {
      for (let i = 0; i < nextProps.topic.degrees.length; i++) {
        if(nextProps.topic.degrees[i] === "BACHELOR") bachelorState = true;
        else if(nextProps.topic.degrees[i] === "MASTER") masterState = true;
        else if(nextProps.topic.degrees[i] === "PhD") phdState = true;
        else if(nextProps.topic.degrees[i] === "HIGH_SCHOOL") highSchoolState = true;
      }
      enabledState = nextProps.topic.enabled;
      titleState = nextProps.topic.title;
      shortAbstractState = nextProps.topic.shortAbstract;
      descriptionState = nextProps.topic.description;
      for (let i = 0; i < nextProps.topic.tags.length; i++) {
        tagsState += nextProps.topic.tags[i];
        if ((i + 1) < nextProps.topic.tags.length) tagsState += ";"
      }
      dialogTitleState = _t.translate('Edit Topic');
      actionsState = [
        { label: _t.translate('Save changes'), onClick: () => this.handleEdit()},
        { label: _t.translate('Cancel'), onClick: () => this.handleToggle() }
      ];
    }

    this.setState({
      bachelor: bachelorState,
      master: masterState,
      phd: phdState,
      highSchool: highSchoolState,
      title: titleState,
      shortAbstract: shortAbstractState,
      description: descriptionState,
      tags: tagsState,
      dialogTitle: dialogTitleState,
      actions: actionsState,
      enabled: enabledState
    });
  }

  handleToggle = () => {
    this.props.toggleHandler();
  };

  handleTabChange = (index) => {
    this.setState({...this.state, tabIndex: index});
  };

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  };

  /**
   * Handles adding request.
   */
  handleAdd = () => {
    this.props.dataHandler("post", "",
      JSON.stringify({
        creator: { id: Auth.getUserInfo().sub },
        degrees: this.getDegrees(),
        description: this.state.description,
        shortAbstract: this.state.shortAbstract,
        tags: this.getTags(),
        title: this.state.title,
        enabled: this.state.enabled
      })
    );
    this.handleToggle();
  }

  /**
   * Handles editing request.
   */
  handleEdit = () => {
    this.props.dataHandler("put", this.props.topic.id,
      JSON.stringify({
        creator: { id: Auth.getUserInfo().sub },
        degrees: this.getDegrees(),
        description: this.state.description,
        shortAbstract: this.state.shortAbstract,
        tags: this.getTags(),
        title: this.state.title,
        enabled: this.state.enabled
      })
    );
    this.handleToggle();
  }

  getDegrees = () => {
    var degrees = [];

    if(this.state.bachelor === true) degrees.push("BACHELOR");
    if(this.state.master === true) degrees.push("MASTER");
    if(this.state.phd === true) degrees.push("PhD");
    if(this.state.highSchool === true) degrees.push("HIGH_SCHOOL");

    return degrees;
  }

  getTags = () => {
    var tags = [];
    var stringTags = this.state.tags;

    if(stringTags.indexOf(";") === stringTags.length-1)
      stringTags = stringTags.substring(0, stringTags.indexOf(";"));

    while(stringTags.indexOf(";") !== -1) {
      tags.push(stringTags.substring(0, stringTags.indexOf(";")));
      stringTags = stringTags.substring(stringTags.indexOf(";")+1);
    }
    tags.push(stringTags);

    return tags;
  }

  render() {
    return(
      <div className='pull-right'>
        <Button icon='add' floating onClick={this.handleToggle} />
        <Dialog
          actions={this.state.actions}
          active={this.props.active}
          onEscKeyDown={this.handleToggle}
          onOverlayClick={this.handleToggle}>
          <h2>{this.state.dialogTitle}</h2>
          <Tabs index={this.state.tabIndex} onChange={this.handleTabChange} >
            <Tab label={ _t.translate('Basic info') }>
              <Input type='text' label={ _t.translate('Topic title') } hint="Topic title" name='title' required value={this.state.title} onChange={this.handleChange.bind(this, 'title')} />
              <Input type='text' label={ _t.translate('Short abstract') } hint="Short info about the topic"  multiline rows={2} name='shortAbstract' value={this.state.shortAbstract} onChange={this.handleChange.bind(this, 'shortAbstract')} />
              <Input type='text' label={ _t.translate('Tags') } hint="Divide tags using ;" value={this.state.tags} onChange={this.handleChange.bind(this, 'tags')} />
              <table>
                <thead>
                  <tr>
                    <th colSpan="4"><h4>{ _t.translate('Degrees') }</h4></th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td style={{ paddingRight: '10px'}}>
                      <Checkbox
                        checked={this.state.bachelor}
                        label={ _t.translate('Bachelor') }
                        name='grades'
                        onChange={this.handleChange.bind(this, 'bachelor')} />
                    </td>
                    <td style={{ paddingRight: '10px'}}>
                      <Checkbox
                        checked={this.state.master}
                        label={ _t.translate('Master') }
                        name='grades'
                        onChange={this.handleChange.bind(this, 'master')} />
                    </td>
                    <td style={{ paddingRight: '10px'}}>
                      <Checkbox
                        checked={this.state.phd}
                        label={ _t.translate('PhD') }
                        name='grades'
                        onChange={this.handleChange.bind(this, 'phd')} />
                    </td>
                    <td>
                      <Checkbox
                        checked={this.state.highSchool}
                        label={ _t.translate('High school') }
                        name='grades'
                        onChange={this.handleChange.bind(this, 'highSchool')} />
                    </td>
                  </tr>
                </tbody>
              </table>
              <Checkbox
                checked={this.state.enabled}
                label={ _t.translate('Enabled for use') }
                name='enabled'
                onChange={this.handleChange.bind(this, 'enabled')} />
            </Tab>
            <Tab label={ _t.translate('Topic description') }>
              <Input type='text' label={ _t.translate('Topic description') } hint="Full topic description in markdown" multiline rows={20} value={this.state.description} onChange={this.handleChange.bind(this, 'description')} />
            </Tab>
            <Tab label={ _t.translate('Preview') }>
              <p>Below you can see a preview of the description. Uses <a href="https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet" target="_blank">Markdown</a>.</p>
              <ReactMarkdown source={ this.state.description } />
            </Tab>
          </Tabs>
        </Dialog>
      </div>
    )
  }

}

const MyTopics = () => (
  <div>
    <TopicTable />
  </div>
);

export default MyTopics;
