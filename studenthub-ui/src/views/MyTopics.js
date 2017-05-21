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

import DeleteButton from '../components/DeleteButton.js';
import EditButton from '../components/EditButton.js';

import Auth from '../Auth.js';
import Util from '../Util.js';

class TopicTable extends Component {
  constructor(props) {
    super(props);

    this.state = {
      topics: [],
      dialogActive: false,
      editId: -1,
      snackbarLabel: "",
      snackbarActive: false
    };
    this.getTopics();
  }

  /**
   * Gets the list of all topics.
   */
  getTopics = () => {
    fetch('/api/users/' + Auth.getUserInfo().sub + '/ownedTopics', {
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
            degrees: json[i].degrees,
            description: json[i].description,
            id: json[i].id,
            shortAbstract: json[i].shortAbstract,
            tags: json[i].tags,
            title: json[i].title
          });
        }
        this.setState({
          topics: newData
        });
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

  /**
   * Gets the topic that is being edited.
   * @return the desired faculty, "" if none is selected
   */
  getAssociatedTopic = () => {
    if(this.state.editId === -1) return -1;
    else return this.state.topics[this.state.editId];
  }

  toggleDialog = (id) => {
    this.setState({dialogActive: !this.state.dialogActive, editId: id});
  }

  render () {
    return (
      <div>
        <h1>
          My Topics { Auth.hasRole(Util.userRoles.techLeader) ? <NewTopicDialog active={this.state.dialogActive} dataHandler={(method, id, data) => this.manageData(method, id, data)} topic={this.getAssociatedTopic()} toggleHandler={() => this.toggleDialog(-1)}/> : '' }
        </h1>
        <Table selectable={false}>
          <TableHead>
            <TableCell>Title</TableCell>
            <TableCell>Short Abstract</TableCell>
            <TableCell>Degrees</TableCell>
            <TableCell>Tags</TableCell>
            <TableCell>Actions</TableCell>
          </TableHead>
          {this.state.topics.map((item, index) => (
            <TableRow key={item.id}>
              <TableCell><strong>{item.title}</strong></TableCell>
              <TableCell>{item.shortAbstract}</TableCell>
              <TableCell>{item.degrees.map( (degree) => <Chip key={degree}> {degree} </Chip> )}</TableCell>
              <TableCell>{item.tags.map( (tag) => <Chip key={tag}> {tag} </Chip> )}</TableCell>
              <TableCell>
                <EditButton toggleHandler={() => this.toggleDialog(index)} />
                <DeleteButton deleteHandler={() => this.manageData("delete", item.id, null)} />
              </TableCell>
            </TableRow>
          ))}
        </Table>
      </div>
    );
  }
}

class NewTopicDialog extends Component {

  state = {
    bachelor: false, master: false, phd: false, title: '', shortAbstract: '', description: '', tags: '', tagIndex: 0, dialogTitle: "New Topic",
    actions : [
      { label: "Add", onClick: () => this.handleAdd()},
      { label: "Cancel", onClick: () => this.handleToggle() }
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
      dialogTitleState = "New Topic";
      actionsState = [
        { label: "Add", onClick: () => this.handleAdd()},
        { label: "Cancel", onClick: () => this.handleToggle() }
      ];
    }
    else {
      for (let i = 0; i < nextProps.topic.degrees.length; i++) {
        if(nextProps.topic.degrees[i] === "BACHELOR") bachelorState = true;
        else if(nextProps.topic.degrees[i] === "MASTER") masterState = true;
        else if(nextProps.topic.degrees[i] === "PhD") phdState = true;
      }
      titleState = nextProps.topic.title;
      shortAbstractState = nextProps.topic.shortAbstract;
      descriptionState = nextProps.topic.description;
      for (let i = 0; i < nextProps.topic.tags.length; i++) {
        tagsState += nextProps.topic.tags[i]+";";
      }
      dialogTitleState = "Edit Topic";
      actionsState = [
        { label: "Save", onClick: () => this.handleEdit()},
        { label: "Cancel", onClick: () => this.handleToggle() }
      ];
    }

    this.setState({
      bachelor: bachelorState,
      master: masterState,
      phd: phdState,
      title: titleState,
      shortAbstract: shortAbstractState,
      description: descriptionState,
      tags: tagsState,
      dialogTitle: dialogTitleState,
      actions: actionsState
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
        degrees: this.getDegrees(),
        description: this.state.description,
        shortAbstract: this.state.shortAbstract,
        tags: this.getTags(),
        title: this.state.title
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
        degrees: this.getDegrees(),
        description: this.state.description,
        shortAbstract: this.state.shortAbstract,
        tags: this.getTags(),
        title: this.state.title
      })
    );
    this.handleToggle();
  }

  getDegrees = () => {
    var degrees = [];

    if(this.state.bachelor === true) degrees.push("BACHELOR");
    if(this.state.master === true) degrees.push("MASTER");
    if(this.state.phd === true) degrees.push("PhD");

    return degrees;
  }

  getTags = () => {
    var tags = [];
    var stringTags = this.state.tags;

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
          onOverlayClick={this.handleToggle}
          title={this.state.dialogTitle}>
            <p>Here you can create a new or edit an existing Thesis topic.</p>
            <Input type='text' label='Title' hint="Topic title" name='title' required value={this.state.title} onChange={this.handleChange.bind(this, 'title')} />
            <Input type='text' label='Short Abstract' hint="Short info about the topic"  multiline rows={2} name='shortAbstract' value={this.state.shortAbstract} onChange={this.handleChange.bind(this, 'shortAbstract')} />
            <Tabs index={this.state.tabIndex} onChange={this.handleTabChange} >
              <Tab label="Description">
                <Input type='text' label='Description' hint="Full topic description in markdown" multiline rows={3} value={this.state.description} onChange={this.handleChange.bind(this, 'description')} />
              </Tab>
              <Tab label="Result">
                <ReactMarkdown source={ this.state.description } />
              </Tab>
            </Tabs>
            <Input type='text' label='Tags' hint="Divide tags using ;" value={this.state.tags} onChange={this.handleChange.bind(this, 'tags')} />
            <Checkbox
              checked={this.state.bachelor}
              label="Bachelor"
              name='grades'
              onChange={this.handleChange.bind(this, 'bachelor')} />
            <Checkbox
              checked={this.state.master}
              label="Master"
              name='grades'
              onChange={this.handleChange.bind(this, 'master')} />
            <Checkbox
              checked={this.state.phd}
              label="PhD"
              name='grades'
              onChange={this.handleChange.bind(this, 'phd')} />
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
