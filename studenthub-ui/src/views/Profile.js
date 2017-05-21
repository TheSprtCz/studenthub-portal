import React from 'react';
import { withRouter } from 'react-router-dom';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';
import Dropdown from 'react-toolbox/lib/dropdown/Dropdown.js';
import Tab from 'react-toolbox/lib/tabs/Tab.js';
import Tabs from 'react-toolbox/lib/tabs/Tabs.js';

import SiteSnackbar from '../components/SiteSnackbar.js';
import Auth from '../Auth.js';
import Util from '../Util.js';

const LogoutButton = withRouter(({ history }) => (
  <Button primary onClick={() => { Auth.signout(() => history.push('/')) }}>Sign out</Button>
))

class ProfileEditView extends React.Component {
  state = { user: { name: "User" }, email: '', phone: '', roles: '',  tags: '',
  lastLogin: [], snackbarActive: false, snackbarLabel: '' };

  componentDidMount() {
    this.getUser();
  }

  getUser = () => {
    fetch('/api/users/' + Auth.getUserInfo().sub, {
      method: 'get',
      credentials: 'same-origin'
    }).then(function(response) {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      var rolesState = "";
      if (json.roles !== null)
        for(let i = 0; i < json.roles.length; i++) {
          rolesState += json.roles[i]+"; ";
          if(i === json.roles.length-1) rolesState = rolesState.substring(0, rolesState.length-2);
        }
      var tagsState = "";
      if(json.tags !== null)
        for(let i = 0; i < json.tags.length; i++) {
          tagsState += json.tags[i]+"; ";
        }
      var lastLoginState = "";
      if(json.lastLogin !== null)
        for(let i = 0; i < json.lastLogin.length; i++) {
          lastLoginState += json.lastLogin[i]+"; ";
        }

      this.setState({
        user: json,
        email:	json.email,
        phone: Util.isEmpty(json.phone) ? "" : json.phone,
        roles: rolesState,
        tags: tagsState,
        lastLogin: lastLoginState
      });
    }.bind(this));
  }

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  };

  handleSubmit = () => {
    fetch('/api/users/' + Auth.getUserInfo().sub, {
      method: 'put',
      credentials: 'same-origin',
      headers: {
        "Content-Type" : "application/json"
      },
      body: JSON.stringify({
        name: this.state.user.name,
        roles: this.state.user.roles,
        lastLogin: this.state.user.lastLogin,
        faculty: this.state.user.faculty,
        company: this.state.user.company,
        email:	this.state.email,
        phone:	this.state.phone,
        tags: this.getTags(),
        username:	this.state.email
      })
    }).then(function(response) {
        if(response.ok) {
          this.setState({
            snackbarLabel: "Your account has been succesfully changed!",
            snackbarActive: true
          });
          this.getUser();
      } else {
        this.setState({
          snackbarLabel: "An error occured! Your request couldn't be processed. It's possible that you have a problem with your internet connection or that the server is not responding.",
          snackbarActive: true
        });
        throw new Error('There was a problem with network connection. PUT request could not be processed!');
      }
    }.bind(this));
  };

  getTags = () => {
    var tags = [];
    var stringTags = this.state.tags;

    while(stringTags.indexOf(";") !== -1) {
      tags.push(stringTags.substring(0, stringTags.indexOf(";")));
      stringTags = stringTags.substring(stringTags.indexOf(";")+1);
    }
    if(stringTags !== "") tags.push(stringTags);

    return tags;
  }

  /**
   * Toggles the visiblity of the Snackbar.
   */
  toggleSnackbar = () => {
    this.setState({
      snackbarActive: !this.state.snackbarActive
    })
  }

  render () {
    return (
      <section className="col-md-offset-1 col-md-10">
        <Input type='email' icon='email' label='Email'  hint="Change your email" required value={this.state.email} onChange={this.handleChange.bind(this, 'email')} />
        <Input type='text' name='name' label='Name' icon='person' disabled value={this.state.user.name} />
        <Input type='tel' name='phone' label='Phone number' icon='phone' hint="Change your phone number" required value={this.state.phone} onChange={this.handleChange.bind(this, 'phone')} maxLength={9} />
        <Input type='text' name='faculty' label='Faculty' icon='business' disabled value={Util.isEmpty(this.state.user.faculty) ? "None" : this.state.user.faculty.name} />
        <Input type='text' name='company' label='Company' icon='business' disabled value={Util.isEmpty(this.state.user.company) ? "None" : this.state.user.company.name} />
        <Input type='text' name='roles' label='Assigned roles' icon='person' disabled value={this.state.roles} />
        <Input type='text' name='tags' label='User tags' icon='flag' hint="Divide tags using ;" value={this.state.tags} onChange={this.handleChange.bind(this, 'tags')} />
        <Button icon='edit' label='Save changes' raised primary className='pull-right' onClick={this.handleSubmit}/>
        <SiteSnackbar active={this.state.snackbarActive} label={this.state.snackbarLabel} toggleHandler={() => this.toggleSnackbar()} />
      </section>
    );
  }
}

class CompanyEditView extends React.Component {
  state = { id: 0, name: '', city: '', country: '',  url: '', logoUrl: '',
    size: '', plan: "", snackbarActive: false, snackbarLabel: '' };

  componentDidMount() {
    this.getCompany();
  }

  getCompany = () => {
    fetch('/api/users/' + Auth.getUserInfo().sub, {
      method: 'get',
      credentials: 'same-origin'
    }).then(function(response) {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      if (Util.isEmpty(json.company)) {
        this.setState({
          snackbarActive: true,
          snackbarLabel: "Couldn't find any companies associated with your account!"
        });
        return;
      }
      this.setState({
        id: json.company.id,
        name: json.company.name,
        city:	json.company.city,
        country:	json.company.country,
        url:	json.company.url,
        logoUrl:	json.company.logoUrl,
        size:	json.company.size,
        plan: json.company.plan
      });
    }.bind(this));
  }

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  };

  handleSubmit = () => {
    fetch('/api/companies/' + this.state.id, {
      method: 'put',
      credentials: 'same-origin',
      headers: {
        "Content-Type" : "application/json"
      },
      body: JSON.stringify({
          name: this.state.name,
          city:	this.state.city,
          country:	this.state.country,
          url:	this.state.url,
          logoUrl:	this.state.logoUrl,
          size:	this.state.size,
          plan: this.state.plan
        })
    }).then(function(response) {
        if (response.ok) {
          this.setState({
            snackbarLabel: "Your company has been succesfully changed!",
            snackbarActive: true
          });
          this.getCompany();
      } else {
        this.setState({
          snackbarLabel: "An error occured! Your request couldn't be processed. It's possible that you have a problem with your internet connection or that the server is not responding.",
          snackbarActive: true
        });
        throw new Error('There was a problem with network connection. PUT request could not be processed!');
      }
    }.bind(this));
  };

  /**
   * Toggles the visiblity of the Snackbar.
   */
  toggleSnackbar = () => {
    this.setState({
      snackbarActive: !this.state.snackbarActive
    })
  }

  render () {
    return (
      <section className="col-md-offset-1 col-md-10">
        <Input type='name' label='Name' icon='textsms'  hint="Change company name" required value={this.state.name} onChange={this.handleChange.bind(this, 'name')} />
        <Input type='text' label='City' icon='location_city'  hint="Change company city headquarters" required value={this.state.city} onChange={this.handleChange.bind(this, 'city')} />
        <Dropdown
          auto required
          onChange={this.handleChange.bind(this, 'country')}
          source={Util.countriesSource}
          name='country'
          hint='Select country'
          value={this.state.country}
          icon='public'
          label='Country' />
        <Input type='url' label='Website' icon='web'  hint="Change website url" required value={this.state.url} onChange={this.handleChange.bind(this, 'url')} />
        <Input type='url' label='Logo' icon='photo'  hint="Change logo" required value={this.state.logoUrl} onChange={this.handleChange.bind(this, 'logoUrl')} />
        <Dropdown
          auto required
          onChange={this.handleChange.bind(this, 'size')}
          source={Util.companySizesSource}
          name='size'
          hint='Select company size'
          value={this.state.size}
          icon='business'
          label='Size' />
          <Dropdown
            auto required
            onChange={this.handleChange.bind(this, 'plan')}
            source={Util.companyPlansSource}
            name='plan'
            hint='Select company plan'
            value={this.state.plan}
            icon='business'
            label='Plan' />
        <Button icon='edit' label='Save changes' raised primary className='pull-right' onClick={this.handleSubmit}/>
        <SiteSnackbar active={this.state.snackbarActive} label={this.state.snackbarLabel} toggleHandler={() => this.toggleSnackbar()} />
      </section>
    );
  }
}

class ProfileViewWithTabs extends React.Component {
  state = { index: 0 };

  handleTabChange = (index) => {
    this.setState({index});
  };

  render () {
    return (
      <section>
        <Tabs index={this.state.index} onChange={this.handleTabChange}>
          <Tab label='User profile'><ProfileEditView /></Tab>
          <Tab label='Your company'><CompanyEditView /></Tab>
        </Tabs>
      </section>
    );
  }
}

const ProfileView = () => (
  <div>
    <h1>
      Your profile
    </h1>
    <LogoutButton />
    {(Auth.hasRole("COMPANY_REP")) ? <ProfileViewWithTabs /> : <ProfileEditView />}
  </div>
);

export default ProfileView;
