import React from 'react';
import { Redirect } from 'react-router';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';
import Dropdown from 'react-toolbox/lib/dropdown/Dropdown.js';
import Checkbox from 'react-toolbox/lib/checkbox/Checkbox.js';

import PersonalDataProcDialog from '../components/PersonalDataProcDialog.js';
import TermsOfUseDialog from '../components/TermsOfUseDialog.js';

import Util from '../Util.js';
import _t from '../Translations.js';

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
        label={ _t.translate("Faculty") }
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
      redirect: false
  };

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  }

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
        roles: [Util.userRoles.student],
        username: this.state.email
      })
    }).then(function(response) {
      if (response.ok) {
        Util.notify("success", "", "Your account has been succesfully created!");
        this.setState({
          redirect: true
        });
      } else {
        Util.notify("error", "There was a problem with network connection.", "Your request hasn't been processed.");
        this.setState({
          redirect: false
        });
        throw new Error('There was a problem with network connection. POST request could not be processed!');
      }
    }.bind(this));
  }

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
        <h1>{ _t.translate('Sign Up') }</h1>
        <Input type='email' label={ _t.translate("Email address") } hint={ _t.translate('Your email adress') }
          icon='email' required value={this.state.email} onChange={this.handleChange.bind(this, 'email')} />
        <Input type='text' label={ _t.translate("Name") } hint={ _t.translate('Your name') } name='name'
          icon='textsms' required value={this.state.name} onChange={this.handleChange.bind(this, 'name')} maxLength={16} />
        <Input type='tel' label={ _t.translate("Phone") } hint={ _t.translate('Your phone number') } name='phone'
          icon='phone' value={this.state.phone} onChange={this.handleChange.bind(this, 'phone')} maxLength={9} />
        <FacultySelect changeHandler={(value) => this.handleChange("faculty", value)} />
        <table>
          <tbody>
            <tr>
              <td style={{ paddingLeft: '1em'}}>
                <Checkbox checked={this.state.terms} onChange={this.handleChange.bind(this, 'terms')} required />
              </td>
              <td style={{ paddingTop: '0px', addingBottom: '1.5em', paddingLeft: '1em', display: 'inline-flex', fontSize: '17px'}}>
                { _t.translate('I have read and I do accept the')}: &nbsp; <TermsOfUseDialog /> &nbsp;{ _t.translate('and')}&nbsp; <PersonalDataProcDialog/>.
              </td>
            </tr>
          </tbody>
        </table>
        <br />
        <Button icon='person_add' label={ _t.translate('Sign Up') } raised primary className='pull-right' onClick={this.handleSubmit}/>
          {this.generateRedirect()}
      </section>
    );
  }
}

export default SignUpForm;
