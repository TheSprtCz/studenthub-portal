import React, { Component } from 'react';
import { Redirect } from 'react-router';
import Tab from 'react-toolbox/lib/tabs/Tab.js';
import Tabs from 'react-toolbox/lib/tabs/Tabs.js';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';
import IconButton from 'react-toolbox/lib/button/IconButton.js';
import RadioGroup from 'react-toolbox/lib/radio/RadioGroup.js';
import RadioButton from 'react-toolbox/lib/radio/RadioButton.js';
import Dropdown from 'react-toolbox/lib/dropdown/Dropdown.js';

import Util from '../Util.js';
import SiteSnackbar from '../components/SiteSnackbar.js';

const Tier1 = () => (
  <div className='col-sm-4'>
    <div className="panel panel-default">
      <div className="panel-heading">
        <h3 className="panel-title">
          <RadioButton label='TIER_1' value='TIER_1'/>
        </h3>
      </div>
      <div className="panel-body">
        SOME DESCRIPTION
      </div>
    </div>
  </div>
)

const Tier2 = () => (
  <div className='col-sm-4'>
    <div className="panel panel-info">
      <div className="panel-heading">
        <h3 className="panel-title">
          <RadioButton label='TIER_2' value='TIER_2'/>
        </h3>
      </div>
      <div className="panel-body">
        SOME DESCRIPTION
      </div>
    </div>
  </div>
)

const Tier3 = () => (
  <div className='col-sm-4'>
    <div className="panel panel-default">
      <div className="panel-heading">
        <h3 className="panel-title">
          <RadioButton label='TIER_3' value='TIER_3'/>
        </h3>
      </div>
      <div className="panel-body">
        SOME DESCRIPTION
      </div>
    </div>
  </div>
)

class CompanyRegForm extends Component {

  state = { company_name: '', rep_name: '', city:'', url: '', logo: '', country: '', size: '', phone: '', email: '', password: '', plan: 'TIER_2', index: 0,
  snackbarActive: false, snackbarLabel: '',  redirect: false };

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  };

  handleTabChange = (i) => {
    this.setState({index: i});
  };

  next = () => {
    this.setState({index: this.state.index+1});
  }

  submitData = () => {
    fetch('/api/companies', {
        method: 'post',
        credentials: 'same-origin',
        headers: { "Content-Type" : "application/json" },
        body: JSON.stringify({
          city:	this.state.city,
          country: this.state.country,
          logoUrl: this.state.logo,
          name:	this.state.company_name,
          plan:	this.state.plan,
          size:	this.state.size,
          url:	this.state.url
        })
      }).then(function(response) {
          if(response.ok) {
            return response.json();
        } else throw new Error('There was a problem with network connection.');
      }).then(function(json) {
        fetch('/api/users/signUp', {
            method: 'post',
            credentials: 'same-origin',
            headers: { "Content-Type" : "application/json" },
            body: JSON.stringify({
              name:	this.state.rep_name,
              company: json,
              email: this.state.email,
              username: this.state.email,
              phone: this.state.phone,
              roles: ["COMPANY_REP"],
              password: this.state.password
            })
          }).then(function(response) {
              if(response.ok) {
                this.setState({
                  snackbarLabel: "Your company and it's company representative have both been successfully created!",
                  snackbarActive: true
                });
                setTimeout(function(){
                  this.setState({ redirect: true })
                }.bind(this), 1000);
            } else throw new Error('There was a problem with network connection.');
          }.bind(this));
      }.bind(this));
  }

  generateRedirect = () => {
    if(this.state.redirect === false) return;
    else return (<Redirect to="/signin" />);
  }

  handleToggle = () => {
    this.setState({ snackbarActive: !this.state.snackbarActive });
  }

  render() {
    return(
      <div>
        <Tabs index={this.state.index} onChange={this.handleTabChange}>
          <Tab label='Basic Company Information'>
            <section>
              <Input type='text' label='Name' name='name' icon='textsms' required value={this.state.company_name} onChange={this.handleChange.bind(this, 'company_name')} />
              <Input type='text' label='City' name='name' icon='location_city' required value={this.state.city} onChange={this.handleChange.bind(this, 'city')} />
              <Dropdown
                auto required
                label='Country'
                onChange={this.handleChange.bind(this, 'country')}
                source={Util.countriesSource}
                name='country'
                value={this.state.country}
                icon='public' />
              <Input type='url' label='Web' name='name' icon='web' required value={this.state.url} onChange={this.handleChange.bind(this, 'url')} />
              <Input type='url' label='Logo' name='logo' icon='photo' required value={this.state.logo} onChange={this.handleChange.bind(this, 'logo')} />
              <Dropdown
                auto required
                label='Size'
                onChange={this.handleChange.bind(this, 'size')}
                source={Util.companySizesSource}
                name='size'
                value={this.state.size}
                icon='business' />
              <IconButton icon='navigate_next' className='pull-right' onClick={this.next} />
            </section>
          </Tab>
          <Tab label='Company Representative'>
            <section>
              <Input type='text' label='Name' name='rep_name' icon='textsms' required value={this.state.rep_name} onChange={this.handleChange.bind(this, 'rep_name')} />
              <Input type='password' label='Password' name='password' icon='lock' required value={this.state.password} onChange={this.handleChange.bind(this, 'password')} />
              <Input type='email' label='Email address' icon='email' required value={this.state.email} onChange={this.handleChange.bind(this, 'email')} />
              <Input type='tel' label='Phone' name='phone' icon='phone' required value={this.state.phone} onChange={this.handleChange.bind(this, 'phone')} />
              <IconButton icon='navigate_next' className='pull-right' onClick={this.next} />
            </section>
          </Tab>
          <Tab label='Company Plan'>
            <section>
              <RadioGroup name='plan' value={this.state.plan} onChange={this.handleChange.bind(this, 'plan')} required>
                <div className='row'>
                  {/* http://github.com/react-toolbox/react-toolbox/issues/1361 */}
                  <Tier1 />
                  <Tier2 />
                  <Tier3 />
                </div>
              </RadioGroup>
              <Button icon='bookmark' label='Submit' raised primary onClick={this.submitData}/>
            </section>
          </Tab>
        </Tabs>
        <SiteSnackbar active={this.state.snackbarActive} label={this.state.snackbarLabel} toggleHandler={() => this.handleToggle()} />
        {this.generateRedirect()}
      </div>
    )
  }
}


const CompanyReg = () => (
  <div>
    <h1>Company Registration</h1>
    <CompanyRegForm />
  </div>
);

export default CompanyReg;
