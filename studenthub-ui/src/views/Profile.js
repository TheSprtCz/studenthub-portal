import React from 'react';
import { withRouter } from 'react-router-dom';
import Avatar from 'react-toolbox/lib/avatar/Avatar.js';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';
import Dropdown from 'react-toolbox/lib/dropdown/Dropdown.js';
import Tab from 'react-toolbox/lib/tabs/Tab.js';
import Tabs from 'react-toolbox/lib/tabs/Tabs.js';
import Chip from 'react-toolbox/lib/chip/Chip.js';

import SiteSnackbar from '../components/SiteSnackbar.js';

import Auth from '../Auth.js';
import Util from '../Util.js';
import _t from '../Translations.js'

const gravatar = require("gravatar")

const LogoutButton = withRouter(({ history }) => (
  <Button raised primary icon="person" onClick={() => { Auth.signout(() => history.push('/')) }}>
    { _t.translate('Sign Out') }
  </Button>
))

class ProfileEditView extends React.Component {
  state = { email: '', avatarUrl: '', name: '', phone: '', roles: [],  tags: '',
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
      var tagsState = "";
      if(json.tags !== null)
        for(let i = 0; i < json.tags.length; i++) {
          tagsState += json.tags[i]+"; ";
        }

      this.setState({
        faculty: json.faculty,
        company: json.company,
        name: json.name,
        email:	json.email,
        avatarUrl: gravatar.url(json.email, {s: '300', protocol: 'https'}),
        phone: Util.isEmpty(json.phone) ? "" : json.phone,
        roles: json.roles,
        tags: tagsState,
        lastLogin: json.lastLogin
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
        name: this.state.name,
        roles: this.state.roles,
        lastLogin: this.state.lastLogin,
        faculty: this.state.faculty,
        company: this.state.company,
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

    if(stringTags.indexOf(";") === stringTags.length-1)
      stringTags = stringTags.substring(0, stringTags.indexOf(";"));

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
      <div className="row">
        <div className="col-md-2 text-center">
          <Avatar style={{width: '6em', height: '6em', marginBottom: '1em'}} image={this.state.avatarUrl} title={ _t.translate('Your Gravatar') } />
          {this.state.roles.map( (role) => <Chip key={role}> { _t.translate(role) } </Chip> )}
          <LogoutButton className="pull-left" />
        </div>
        <div className="col-md-10">
          <Input type='email' icon='email' label='Email'  hint="Change your email" required value={this.state.email} onChange={this.handleChange.bind(this, 'email')} />
          <Input type='text' name='name' label={ _t.translate('Name') } hint="Change your name" icon='person' required value={this.state.name} onChange={this.handleChange.bind(this, 'name')} />
          <Input type='tel' name='phone' label={ _t.translate('Phone') } icon='phone' hint="Change your phone number" required value={this.state.phone} onChange={this.handleChange.bind(this, 'phone')} maxLength={9} />
          <Input type='text' name='faculty' label={ _t.translate('Faculty') } icon='business' disabled value={Util.isEmpty(this.state.faculty) ? "None" : this.state.faculty.name} />
          <Input type='text' name='company' label={ _t.translate('Company') } icon='business' disabled value={Util.isEmpty(this.state.company) ? "None" : this.state.company.name} />
          <Input type='hidden' name='roles' label={ _t.translate('Role') } icon='person' disabled value={this.state.roles.toString()} />
          <Input type='text' name='tags' label={ _t.translate('Tags') } icon='flag' hint="Divide tags using ;" value={this.state.tags} onChange={this.handleChange.bind(this, 'tags')} />
          <Button icon='edit' label={ _t.translate('Save changes') } raised primary className='pull-right' onClick={this.handleSubmit}/>
          <SiteSnackbar active={this.state.snackbarActive} label={this.state.snackbarLabel} toggleHandler={() => this.toggleSnackbar()} />
        </div>
      </div>
    );
  }
}

class CompanyEditView extends React.Component {
  state = { id: 0, name: '', city: '', country: '',  url: '', logoUrl: '', size: '',
    planName: '', planDescription: '', planTopicLimit: 10, snackbarActive: false, snackbarLabel: '' };

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
        planName: json.company.plan.name,
        planDescription: json.company.plan.description,
        planTopicLimit: json.company.plan.maxTopics
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
          plan: { name: this.state.planName, description: this.state.planDescription,
            maxTopics: this.state.planTopicLimit }
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
      <div className="row">
        <div className="col-md-2">
          <img style={{maxWidth: '100%'}} src={this.state.logoUrl} alt={ _t.translate('Your logo')}/>
        </div>
        <div className="col-md-10">
          <Input type='name' label={ _t.translate('Name') } icon='textsms'  hint="Change company name" required value={this.state.name} onChange={this.handleChange.bind(this, 'name')} />
          <Input type='text' label={ _t.translate('City') } icon='location_city'  hint="Change company city headquarters" value={this.state.city} onChange={this.handleChange.bind(this, 'city')} />
          <Dropdown
            auto required
            onChange={this.handleChange.bind(this, 'country')}
            source={Util.countriesSource}
            name='country'
            value={this.state.country}
            icon='public'
            label={ _t.translate('Country') } />
          <Input type='url' label={ _t.translate('Web page') } icon='web'  hint="Change website url" value={this.state.url} onChange={this.handleChange.bind(this, 'url')} />
          <Input type='url' label='Logo' icon='photo'  hint="Change logo" value={this.state.logoUrl} onChange={this.handleChange.bind(this, 'logoUrl')} />
          <Dropdown
            auto required
            onChange={this.handleChange.bind(this, 'size')}
            source={Util.companySizesSource}
            name='size'
            value={this.state.size}
            icon='business'
            label={ _t.translate('Size') } />
            <Input type='name' label={ _t.translate('Plan name') } icon='assignment'  hint="Change company plan name" required
              value={this.state.planName} onChange={this.handleChange.bind(this, 'planName')} />
            <Input type='text' label={ _t.translate('Plan description') } icon='description'  hint="Change company plan description"
              value={this.state.planDescription} multiline rows={3} onChange={this.handleChange.bind(this, 'planDescription')} />
            <Input type='number' min="0" label={ _t.translate('Max topics for plan') } icon='format_list_numbered' hint="Change company plan topic limit"
              value={this.state.planTopicLimit} onChange={this.handleChange.bind(this, 'planTopicLimit')} reqiured />
          <Button icon='edit' label={ _t.translate('Save changes') } raised primary className='pull-right' onClick={this.handleSubmit}/>
          <SiteSnackbar active={this.state.snackbarActive} label={this.state.snackbarLabel} toggleHandler={() => this.toggleSnackbar()} />
        </div>
      </div>
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
          <Tab label={ _t.translate('User profile') }><ProfileEditView /></Tab>
          <Tab label={ _t.translate('Company profile') }><CompanyEditView /></Tab>
        </Tabs>
      </section>
    );
  }
}

const ProfileView = () => (
  <div>
    <h1>
      { _t.translate('Profile') }
    </h1>
    {(Auth.hasRole("COMPANY_REP")) ? <ProfileViewWithTabs /> : <ProfileEditView />}
  </div>
);

export default ProfileView;
