import React, { Component } from 'react';
import { Redirect } from 'react-router';
import Tab from 'react-toolbox/lib/tabs/Tab.js';
import Tabs from 'react-toolbox/lib/tabs/Tabs.js';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';
import IconButton from 'react-toolbox/lib/button/IconButton.js';
import Dropdown from 'react-toolbox/lib/dropdown/Dropdown.js';

import Util from '../Util.js';
import _t from '../Translations';

class CompanyRegForm extends Component {

  state = { company_name: '', rep_name: '', city:'', url: '', logo: '', country: '', size: '', phone: '', email: '', password: '',
    planName: '', planDescription: '', planTopicLimit: 10, plans: [], index: 0, planIndex: 0,  redirect: false };

  componentDidMount() {
    this.getPlans();
  }

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  };

  handleTabChange = (i) => {
    this.setState({index: i});
  };

  handlePlanTabChange = (i) => {
    this.setState({planIndex: i});
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
          plan: this.state.plans[this.state.planIndex],
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
                Util.notify("success", "", "Your company and it's company representative have both been successfully created!");
                setTimeout(function(){
                  this.setState({ redirect: true })
                }.bind(this), 1000);
            } else throw new Error('There was a problem with network connection.');
          }.bind(this));
      }.bind(this));
  }

  getPlans() {
    fetch('/api/plans', {
      credentials: 'same-origin',
      method: 'get'
    }).then(function(response) {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      this.setState({
        plans: json
      });
    }.bind(this));
  }

  generateRedirect = () => {
    if(this.state.redirect === false) return;
    else return (<Redirect to="/signin" />);
  }

  render() {
    return(
      <div>
        <Tabs index={this.state.index} onChange={this.handleTabChange}>
          <Tab label={ _t.translate('Basic Company Information') }>
            <section>
              <Input type='text' label={ _t.translate('Name') } name='name' icon='textsms' required value={this.state.company_name}
                onChange={this.handleChange.bind(this, 'company_name')} />
              <Input type='text' label={ _t.translate('City') } name='name' icon='location_city' required value={this.state.city}
                onChange={this.handleChange.bind(this, 'city')} />
              <Dropdown
                auto required
                label={ _t.translate('Country') }
                onChange={this.handleChange.bind(this, 'country')}
                source={Util.countriesSource}
                name='country'
                value={this.state.country}
                icon='public' />
              <Input type='url' label={ _t.translate('Web page') } name='name' icon='web' required value={this.state.url} onChange={this.handleChange.bind(this, 'url')} />
              <Input type='url' label='Logo' name='logo' icon='photo' required value={this.state.logo} onChange={this.handleChange.bind(this, 'logo')} />
              <Dropdown
                auto required
                label={ _t.translate('Size') }
                onChange={this.handleChange.bind(this, 'size')}
                source={Util.companySizesSource}
                name='size'
                value={this.state.size}
                icon='business' />
              <IconButton icon='navigate_next' className='pull-right' onClick={this.next} />
            </section>
          </Tab>
          <Tab label={ _t.translate('Company Representative') }>
            <section>
              <Input type='text' label={ _t.translate('Name') } name='rep_name' icon='textsms' required value={this.state.rep_name}
                onChange={this.handleChange.bind(this, 'rep_name')} />
              <Input type='password' label={ _t.translate('Password') } name='password' icon='lock' required value={this.state.password}
                onChange={this.handleChange.bind(this, 'password')} />
              <Input type='email' label={ _t.translate('Email address') } icon='email' required value={this.state.email} onChange={this.handleChange.bind(this, 'email')} />
              <Input type='tel' label={ _t.translate('Phone') } name='phone' icon='phone' required value={this.state.phone} onChange={this.handleChange.bind(this, 'phone')} />
              <IconButton icon='navigate_next' className='pull-right' onClick={this.next} />
            </section>
          </Tab>
          <Tab label={ _t.translate('Company Plan') }>
            <section>
              <Tabs index={this.state.planIndex} onChange={this.handlePlanTabChange}>
                {this.state.plans.map((item) => (
                  <Tab label={item.name} key={item.name}>
                    <div>
                      <div className="panel panel-default">
                        <div className="panel-heading">
                          <h3 className="panel-title">
                            {item.name}
                          </h3>
                        </div>
                        <div className="panel-body">
                          <p>
                            {item.description}
                          </p>
                          <hr />
                          <p>
                            { _t.translate('This plan limits the number of topics to: ')+item.maxTopics }
                          </p>
                        </div>
                      </div>
                    </div>
                  </Tab>
                ))}
                <Tab label={ _t.translate('New plan') } disabled>
                  <div>
                    <Input type='name' label={ _t.translate('Plan name') } icon='assignment'  hint="Change company plan name" required
                      value={this.state.planName} onChange={this.handleChange.bind(this, 'planName')} />
                    <Input type='text' label={ _t.translate('Plan description') } icon='description'  hint="Change company plan description"
                      value={this.state.planDescription} multiline rows={5} onChange={this.handleChange.bind(this, 'planDescription')} />
                    <Input type='number' min="0" label={ _t.translate('Max topics for plan') } icon='format_list_numbered' hint="Change company plan topic limit"
                      value={this.state.planTopicLimit} onChange={this.handleChange.bind(this, 'planTopicLimit')} required />
                  </div>
                </Tab>
              </Tabs>
              <Button className="pull-right" icon='bookmark' label={ _t.translate('Submit') } raised primary onClick={this.submitData}/>
            </section>
          </Tab>
        </Tabs>
        {this.generateRedirect()}
      </div>
    )
  }
}


const CompanyReg = () => (
  <div>
    <h1>{ _t.translate('Company Registration') }</h1>
    <CompanyRegForm />
  </div>
);

export default CompanyReg;
