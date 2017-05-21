import React from 'react';
import { Redirect } from 'react-router';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';
import Dropdown from 'react-toolbox/lib/dropdown/Dropdown.js';
import Checkbox from 'react-toolbox/lib/checkbox/Checkbox.js';

import SiteSnackbar from '../components/SiteSnackbar.js';
import PersonalDataProcDialog from '../components/PersonalDataProcDialog.js';
import TermsOfUseDialog from '../components/TermsOfUseDialog.js';

import Util from '../Util.js';

class RoleSelect extends React.Component {
  state = { value: 'STUDENT' };

  handleChange = (value) => {
    this.setState({value: value});
    this.props.changeHandler(value);
  };

  render () {
    return (
      <Dropdown
        auto
        label='Role'
        onChange={this.handleChange}
        source={Util.rolesSource}
        value={this.state.value}
        icon='account_box' />
    );
  }
}

class FacultySelect extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      value: '',
      labels: []
    };

    this.getFacultyLabels();
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
          label: (Util.isEmpty(json[i].university)) ? json[i].name : json[i].university.name + ": " + json[i].name
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


class SignUpForm extends React.Component {
  state = {
    email: '', name: '', phone: '', role: 'STUDENT', faculty: { }, terms: false,
    snackbarActive: false, snackbarLabel: '', redirect: false, error: false
  };

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  };

  handleSubmit = () => {
    fetch('/api/account/signUp', {
      method: 'post',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        email: this.state.email,
        faculty: this.state.faculty,
        name: this.state.name,
        phone: this.state.phone,
        roles: [this.state.role],
        username: this.state.email
      })
    }).then(function(response) {
      if (response.ok) {
        this.setState({
          snackbarLabel: "Your account has been succesfully created!",
          snackbarActive: true,
          error: false
        });
      } else {
        this.setState({
          snackbarLabel: "An error occured! Your request couldn't be processed. It's possible that you have a problem with your internet connection or that the server is not responding.",
          snackbarActive: true,
          error: true
        });
        throw new Error('There was a problem with network connection. POST request could not be processed!');
      }
    }.bind(this));
  };

  handleToggle = () => {
    this.setState({ snackbarActive: false });
    if (this.state.error === false) this.setState({ redirect: true });
  };

  generateRedirect = () => {
    if (this.state.redirect === false) {
      return;
    } else {
      return (<Redirect to="/signin" />);
    }
  }

  render () {
    return (
      <section className="container">
        <h1>Sign Up</h1>
        <Input type='email' label='Email address' hint='Your email adress' icon='email' required value={this.state.email} onChange={this.handleChange.bind(this, 'email')} />
        <Input type='text' label='Name' hint='Your name' name='name' icon='textsms' required value={this.state.name} onChange={this.handleChange.bind(this, 'name')} maxLength={16} />
        <Input type='tel' label='Phone' hint='Your phone number' name='phone' icon='phone' value={this.state.phone} onChange={this.handleChange.bind(this, 'phone')} maxLength={9} />
        <RoleSelect changeHandler={(value) => this.handleChange("role", value)} />
        <FacultySelect changeHandler={(value) => this.handleChange("faculty", value)} />
        <table>
          <tbody>
            <tr>
              <td>
                <Checkbox checked={this.state.terms} onChange={this.handleChange.bind(this, 'terms')} required />
              </td>
              <td style={{ paddingBottom: '15px', paddingLeft: '10px', display: 'inline-flex'}}>
                I have read and I do accept the &nbsp;<TermsOfUseDialog />&nbsp; and &nbsp;<PersonalDataProcDialog />.
              </td>
            </tr>
          </tbody>
        </table>
        <br />
        <Button icon='person_add' label='Sign Up' raised primary className='pull-right' onClick={this.handleSubmit}/>
        <SiteSnackbar active={this.state.snackbarActive} label={this.state.snackbarLabel} toggleHandler={() => this.handleToggle()} />
          {this.generateRedirect()}
      </section>
    );
  }
}

export default SignUpForm;
