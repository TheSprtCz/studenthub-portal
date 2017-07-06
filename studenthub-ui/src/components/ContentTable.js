import React, { Component } from 'react';

import AddButton from './AddButton';
import ReturnButton from './ReturnButton.js';
import Pager from './Pager.js';
import FacultyDialog from './FacultyDialog.js';
import UniversityDialog from './UniversityDialog.js';
import { UniversityTable, UniversityRow, UniversityHead} from './UniversityTable.js';
import { FacultyTable, FacultyRow, FacultyHead} from './FacultyTable.js';

import _t from '../Translations.js'
import Util from '../Util.js'

/**
 * Renders the content Table and all its inside elements.
 */
class ContentTable extends Component {
  state = {
      selectedUniversity: -1,
      header: _t.translate("Universities"),
      universityData: [],
      nextUniversities: [],
      facultyData: [],
      nextFaculties: [],
      page: -1,
      editId: -1,
      universityDialogActive: false,
      facultyDialogActive: false,
      offsetWentDown: false
    }

  componentDidMount() {
    this.getUniversities();
    this.changePage(1);
  }

  resetPages() {
    if(this.state.selectedUniversity === -1) {
      this.setState({
        page: -1,
        universityData: [],
        nextUniversities: []
      });
      setTimeout(function(){
        this.getUniversities();
        this.changePage(1);
      }.bind(this), 2);
    } else {
      this.setState({
        page: -1,
        facultyData: [],
        nextFaculties: []
      });
      setTimeout(function(){
        this.getFaculties(this.state.selectedUniversity);
        this.changePage(1);
      }.bind(this), 2);
    }
  }

  /**
   * Connects to the server to update current data using GET.
   */
  getUniversities = () => {
    let page = (this.state.offsetWentDown) ? this.state.page : (this.state.page+1);
    fetch("/api/universities?size=" + Util.UNIVERSITIES_PER_PAGE + "&start=" + (page * Util.UNIVERSITIES_PER_PAGE), {
      credentials: 'same-origin',
      method: 'get'
    }).then(function(response) {
      if (response.ok) {
        return response.json();
      } else if (response.status === 404) {
      this.setState({
        universityData: (this.state.nextUniversities === null || typeof this.state.nextUniversities === 'undefined') ?
          this.state.universityData : this.state.nextUniversities,
        nextUniversities: null,
        selectedUniversity: -1,
        header: _t.translate("Universities")
      });
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this)).then(function(json) {
      if (this.state.offsetWentDown) {
        this.setState({
          universityData: json,
          nextUniversities: this.state.universityData,
          selectedUniversity: -1,
          header: _t.translate("Universities")
        });
      }
      else {
        this.setState({
          universityData: (this.state.nextUniversities === null || typeof this.state.nextUniversities === 'undefined') ?
            this.state.universityData : this.state.nextUniversities,
          nextUniversities: json,
          selectedUniversity: -1,
          header: _t.translate("Universities")
        });
      }
    }.bind(this));
  }

  /**
   * Connects to the server to update current data using GET.
   */
  getFaculties = (id) => {
    let page = (this.state.offsetWentDown) ? this.state.page : (this.state.page+1);
    fetch('/api/universities/' + this.state.universityData[id].id + "/faculties?size=" + Util.FACULTIES_PER_PAGE
      + "&start=" + (page * Util.FACULTIES_PER_PAGE), {
      credentials: 'same-origin',
      method: 'get'
    }).then(function(response) {
      if (response.ok) {
        return response.json();
      } else if (response.status === 404) {
        this.setState({
          facultyData: (this.state.nextFaculties === null || typeof this.state.nextFaculties === 'undefined') ?
            this.state.facultyData : this.state.nextFaculties,
          nextFaculties: null,
          selectedUniversity: id,
          header: this.state.universityData[id].name + " " + _t.translate("faculties")
        });
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }.bind(this)).then(function(json) {
      if (this.state.offsetWentDown) {
        this.setState({
          facultyData: json,
          nextFaculties: this.state.facultyData,
          selectedUniversity: id,
          header: this.state.universityData[id].name + " " + _t.translate("faculties")
        });
      }
      else {
        this.setState({
          facultyData: (this.state.nextFaculties === null || typeof this.state.nextFaculties === 'undefined') ?
            this.state.facultyData : this.state.nextFaculties,
          nextFaculties: json,
          selectedUniversity: id,
          header: this.state.universityData[id].name + " " + _t.translate("faculties")
        });
      }
    }.bind(this));
  }

  /**
   * Handles clicks on TableCells.
   * @param id selectedUniversity value to be set onClick
   */
  handleCellClick = (id) => {
    this.setState({ page: -1 });
    setTimeout(function() {
      if (this.state.selectedUniversity === -1) {
        this.setState({ selectedUniversity: id });
        setTimeout(function(){
          this.resetPages();
        }.bind(this), 2);
      }
      else {
        this.setState({ selectedUniversity: -1 });
        setTimeout(function(){
          this.resetPages();
        }.bind(this), 2);
      }
    }.bind(this), 2);
  }

  /**
   * Generates the table based on selected university.
   * @return generated cells
   */
  generateTable = () => {
    if (this.state.selectedUniversity === -1) {
      return(
        <UniversityTable multiSelectable={false} selectable={false} theme={{}} width="100%" >
          <UniversityHead />
          {this.state.universityData.map( (uni, index) => <UniversityRow key={index} uni={uni} clickHandler={() => this.handleCellClick(index)} toggleHandler={() => this.toggleUniversityDialog(index, "")} deleteHandler={() => this.deleteUniversity(uni.id)} /> )}
        </UniversityTable>
      );
    }
    else {
      return(
      <FacultyTable multiSelectable={false} selectable={false} theme={{}} width="100%" >
        <FacultyHead />
        {this.state.facultyData.map( (fac, index) => <FacultyRow key={index} fac={fac} toggleHandler={() => this.toggleFacultyDialog(index, "")} deleteHandler={() => this.deleteFaculty(fac.id)} /> )}
      </FacultyTable>
    );
    }
  }

  /**
   * Generates the return to university list Button.
   * @return desired button
   */
  generateReturnButton = () => {
    if (this.state.selectedUniversity !== -1) {
      return (<ReturnButton returnCallback={() => this.handleCellClick(-1)} />);
    }
    return;
  }

  /**
   * Creates the dialog appropriate for the university selection.
   */
  generateDialogs = () => {
    if (this.state.selectedUniversity === -1)
      return(
        <UniversityDialog
            active={this.state.universityDialogActive}
            university={(this.state.editId === -1) ? null : this.state.universityData[this.state.editId]}
            toggleHandler={(label) => this.toggleUniversityDialog(this.state.editId, label)} />
      );
    else
      return(
        <FacultyDialog
          active={this.state.facultyDialogActive}
          data={(this.state.editId === -1) ? null : this.state.facultyData[this.state.editId]}
          selectedUniversity={this.state.universityData[this.state.selectedUniversity]}
          toggleHandler={(label) => this.toggleFacultyDialog(this.state.editId, label)} />
      );
  }

  deleteUniversity = (id) => {
    fetch('/api/universities/' + id, {
        method: 'delete',
        credentials: 'same-origin',
        headers: { "Content-Type" : "application/json" }
      }).then(function(response) {
          if(response.ok) {
            Util.notify("success", "", "The university has been succesfully removed!");
            this.getUniversities();
        } else throw new Error('There was a problem with network connection.');
      }.bind(this));
  }

  deleteFaculty = (id) => {
    fetch('/api/faculties/' + id, {
        method: 'delete',
        credentials: 'same-origin',
        headers: { "Content-Type" : "application/json" }
      }).then(function(response) {
          if(response.ok) {
            Util.notify("success", "", "The faculty has been succesfully removed!");
            this.getFaculties(this.state.selectedUniversity);
        } else throw new Error('There was a problem with network connection.');
      }.bind(this));
  }

  /**
   * Toggles the visiblity of the university Dialog.
   * @param id     the id of the university to edit
   * @param label  notification title
   */
  toggleUniversityDialog = (id, label) => {
    this.setState({
      universityDialogActive: !this.state.universityDialogActive,
      editId: id
    })
    if(label === "") return;
    else {
      Util.notify("info", "", label);
      this.getUniversities();
    }
  }

  /**
   * Toggles the visiblity of the faculty Dialog.
   * @param id  the id of the faculty to edit
   * @param label  notification title
   */
  toggleFacultyDialog = (id, label) => {
    this.setState({
      facultyDialogActive: !this.state.facultyDialogActive,
      editId: id
    })
    if(label === "") return;
    else {
      Util.notify("info", "", label);
      this.getFaculties(this.state.selectedUniversity);
    }
  }

  changePage = (offset) => {
    this.setState({ page: this.state.page + offset, offsetWentDown: (offset < 0) ? true : false });

    setTimeout(function() {
      if (this.state.selectedUniversity === -1)
        this.getUniversities();
      else
        this.getFaculties(this.state.selectedUniversity);
    }.bind(this), 2);
  }

  render() {
    return(
      <div className="Table-container">
        <h1>
          {this.state.header}
          <AddButton toggleHandler={(this.state.selectedUniversity === -1) ? () => this.toggleUniversityDialog(-1, "") : () => this.toggleFacultyDialog(-1, "")} />
        </h1>
        {this.generateTable()}
        {this.generateReturnButton()}
        {this.generateDialogs()}
        <Pager currentPage={this.state.page} nextData={(this.state.selectedUniversity === -1) ? this.state.nextUniversities : this.state.nextFaculties}
          pageChanger={(offset) => this.changePage(offset)} />
      </div>
    );
  }
}

export default ContentTable;
