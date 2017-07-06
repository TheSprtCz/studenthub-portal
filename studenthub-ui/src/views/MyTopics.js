import React, { Component } from 'react';
import ReactMarkdown from 'react-markdown';
import Table from 'react-toolbox/lib/table/Table.js';
import TableHead from 'react-toolbox/lib/table/TableHead.js';
import TableRow from 'react-toolbox/lib/table/TableRow.js';
import TableCell from 'react-toolbox/lib/table/TableCell.js';
import Tab from 'react-toolbox/lib/tabs/Tab.js';
import Tabs from 'react-toolbox/lib/tabs/Tabs.js';
import List from 'react-toolbox/lib/list/List.js';
import ListCheckbox from 'react-toolbox/lib/list/ListCheckbox.js';
import Checkbox from 'react-toolbox/lib/checkbox/Checkbox.js';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';
import Chip from 'react-toolbox/lib/chip/Chip.js';

import Pager from '../components/Pager.js';
import DeleteButton from '../components/DeleteButton.js';
import EditButton from '../components/EditButton.js';

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
  state = { topics: [], nextTopics: [], dialogActive: false, editId: -1,
    page: -1, offsetWentDown: false }

  componentDidMount() {
    if (Auth.hasRole(Util.userRoles.companyRep) && !Auth.hasRole(Util.userRoles.admin))
      this.getCompanyTopics();
    else
      this.getTopics();
    this.changePage(1);
  }

  /**
   * Gets the list of all company topics.
   */
  getCompanyTopics = () => {
    var page = (this.state.offsetWentDown) ? this.state.page : (this.state.page+1);
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
          Util.notify("error", "", "There is no company connected to your account!");
          return;
        }
        fetch('/api/companies/' + json.company.id + "/topics?size=" + Util.TOPICS_PER_PAGE_TABLE + "&start=" +
          (page * Util.TOPICS_PER_PAGE_TABLE), {
            credentials: 'same-origin',
            method: 'get'
          }).then(function(response) {
            if (response.ok) {
                return response.json();
            } else if (response.status === 404) {
              this.setState({
                topics: (this.state.nextTopics === null || typeof this.state.nextTopics === 'undefined') ? this.state.topics : this.state.nextTopics,
                nextTopics: null
              });
            }  else {
              throw new Error('There was a problem with network connection.');
            }
          }.bind(this)).then(function(json) {
            if (this.state.offsetWentDown) {
              this.setState({
                topics: json,
                nextTopics: this.state.topics
              });
            }
            else {
              this.setState({
                topics: (this.state.nextTopics === null || typeof this.state.nextTopics === 'undefined') ? this.state.topics : this.state.nextTopics,
                nextTopics: json
              });
            }
          }.bind(this));
      }.bind(this));
  }

  /**
   * Gets the list of all user topics.
   */
  getTopics = () => {
    let page = (this.state.offsetWentDown) ? this.state.page : (this.state.page+1);
    fetch((Auth.hasRole(Util.userRoles.admin)) ? "/api/topics?size=" + Util.TOPICS_PER_PAGE_TABLE + "&start=" +
    (page * Util.TOPICS_PER_PAGE_TABLE) : "/api/users/" + Auth.getUserInfo().sub +
    "/ownedTopics?size=" + Util.TOPICS_PER_PAGE_TABLE + "&start=" +
    (page * Util.TOPICS_PER_PAGE_TABLE), {
        credentials: 'same-origin',
        method: 'get'
      }).then(function(response) {
        if (response.ok) {
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
        if (this.state.offsetWentDown) {
          this.setState({
            topics: json,
            nextTopics: this.state.topics
          });
        }
        else {
          this.setState({
            topics: (this.state.nextTopics === null || typeof this.state.nextTopics === 'undefined') ? this.state.topics : this.state.nextTopics,
            nextTopics: json
          });
        }
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
          Util.notify("success", "", label);
          this.getTopics();
      } else {
        Util.notify("error", "There was a problem with network connection.", "Your request hasn't been processed.");
        throw new Error('There was a problem with network connection. '+method.toUpperCase()+' could not be processed!');
      }
    }.bind(this));
  }

  toggleDialog = (id) => {
    this.setState({dialogActive: !this.state.dialogActive, editId: id});
  }

  changePage = (offset) => {
    this.setState({ page: this.state.page + offset, offsetWentDown: (offset < 0) ? true : false });

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
              <TableCell>{item.degrees.map( (degree) => <Chip key={degree.name}> {degree.description} </Chip> )}</TableCell>
              <TableCell>{item.tags.map( (tag) => <Chip key={tag}> {tag} </Chip> )}</TableCell>
              {(Auth.hasRole(Util.userRoles.companyRep) && !Auth.hasRole(Util.userRoles.admin)) ? '' :
              <TableCell>
                <EditButton toggleHandler={() => this.toggleDialog(index)} />
                <DeleteButton deleteHandler={() => this.manageData("delete", item.id, null)} />
              </TableCell>}
            </TableRow>
          ))}
        </Table>
        <Pager currentPage={this.state.page} nextData={this.state.nextTopics}
          pageChanger={(offset) => this.changePage(offset)} />
      </div>
    );
  }
}

class NewTopicDialog extends Component {
  state = {
    degrees: [], allDegrees: [], title: '', enabled: true, shortAbstract: '',
    description: '', tags: '', tagIndex: 0, dialogTitle: _t.translate('New Topic'),
    actions : [
      { label: _t.translate('Add'), onClick: () => this.handleAdd()},
      { label: _t.translate('Cancel'), onClick: () => this.handleToggle() }
    ]
  };

  componentDidMount() {
    this.getAllDegrees();
  }

  /**
   * Sets default input values on props change.
   */
  componentWillReceiveProps(nextProps) {
    if(this.props === nextProps) return;

    var tagsState = "";

    if (!nextProps.topic === -1) {
      for (let i = 0; i < nextProps.topic.tags.length; i++) {
        tagsState += nextProps.topic.tags[i];
        if ((i + 1) < nextProps.topic.tags.length) tagsState += ";"
      }
    }

    this.setState({
      degrees: (nextProps.topic === -1) ? [] : nextProps.topic.degrees,
      title: (nextProps.topic === -1) ? "" : nextProps.topic.title,
      shortAbstract: (nextProps.topic === -1) ? "" : nextProps.topic.shortAbstract,
      description: (nextProps.topic === -1) ? "" : nextProps.topic.description,
      tags: tagsState,
      dialogTitle: (nextProps.topic === -1) ? _t.translate('New Topic') : _t.translate('Edit Topic'),
      actions: (nextProps.topic === -1) ? [
        { label: _t.translate('Add'), onClick: () => this.handleAdd()},
        { label: _t.translate('Cancel'), onClick: () => this.handleToggle() }
      ] : [
        { label: _t.translate('Save changes'), onClick: () => this.handleEdit()},
        { label: _t.translate('Cancel'), onClick: () => this.handleToggle() }
      ],
      enabled: (nextProps.topic === -1) ? true : nextProps.topic.enabled
    });
  }

  getAllDegrees() {
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
      this.setState({allDegrees: json});
    }.bind(this));
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
        degrees: this.state.degrees,
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
        degrees: this.state.degrees,
        description: this.state.description,
        shortAbstract: this.state.shortAbstract,
        tags: this.getTags(),
        title: this.state.title,
        enabled: this.state.enabled
      })
    );
    this.handleToggle();
  }

  handleDegreeChange = (degree) => {
    var degrees = this.state.degrees;
    if (degrees.indexOf(degree) === -1) {
      degrees.push(degree);
    } else {
      degrees.splice(degrees.indexOf(degree), 1);
    }
    this.setState({degrees: degrees});
  }

  getChecked = (name) => {
    for (var i = 0; i < this.state.degrees.length; i++) {
      if (this.state.degrees[i].name === name) return true;
    }
    return false;
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
              <Checkbox
                checked={this.state.enabled}
                label={ _t.translate('Enabled for use') }
                name='enabled'
                onChange={this.handleChange.bind(this, 'enabled')} />
            </Tab>
            <Tab label={ _t.translate('Topic description') }>
              <Input type='text' label={ _t.translate('Topic description') } hint="Full topic description in markdown" multiline rows={20} value={this.state.description} onChange={this.handleChange.bind(this, 'description')} />
            </Tab>
            <Tab label={ _t.translate('Degrees') }>
              {this.state.allDegrees.map((item) => (
                <List key={item.name}>
                  <ListCheckbox
                    key={item.name}
                    caption={item.description}
                    checked={this.getChecked(item.name)}
                    onChange={() => this.handleDegreeChange(item)} ripple selectable />
                </List>
              ))}
            </Tab>
            <Tab label={ _t.translate('Preview') }>
              <p>Below you can see a preview of the description. Uses <a href="https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet"
                target="_blank" rel="noopener noreferrer">Markdown</a>.</p>
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
