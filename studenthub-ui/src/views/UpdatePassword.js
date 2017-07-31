import React from 'react';
import { Redirect } from 'react-router';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';

import SiteSnackbar from '../components/SiteSnackbar.js';

import Auth from '../Auth.js';
import _t from '../Translations.js';

class UpdatePasswordView extends React.Component {
  state = { oldPwd: '', newPwd: '', snackbarActive: false, snackbarLabel: '', redirect: false };

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  };

  handleSubmit = () => {
    fetch('/api/users/' + Auth.getUserInfo().sub + '/password', {
      method: 'put',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        newPwd: this.state.newPwd,
        oldPwd: this.state.oldPwd
      })
    }).then(function(response) {
        if(response.ok) {
          this.setState({
            snackbarLabel: "Your password has been succesfully changed!",
            snackbarActive: true
          });
          setTimeout(function(){
            this.setState({ redirect: true });
          }.bind(this), 2000);
      }
      else if (response.status === 404) {
        this.setState({
          snackbarLabel: "You have written your old password incorrectly!",
          snackbarActive: true
        });
      }
      else {
        this.setState({
          snackbarLabel: "An error occured! Your request couldn't be processed. It's possible that you have a problem with your internet connection or that the server is not responding.",
          snackbarActive: true
        });
        throw new Error('There was a problem with network connection. PUT request could not be processed!');
      }
    }.bind(this));
  }

  handleToggle = () => {
    this.setState({ snackbarActive: false });
  }

  generateRedirect = () => {
    if(this.state.redirect === false) return;
    else return (<Redirect to="/profile" />);
  }

  render () {
    return (
      <section className='text-center col-md-offset-3 col-md-6'>
        <h1>{ _t.translate('Password update') }</h1>
        <Input type='password' label={ _t.translate('Old password') } hint='Your current password' icon='lock' required
          value={this.state.oldPwd} onChange={this.handleChange.bind(this, 'oldPwd')} />
        <Input type='password' label={ _t.translate('New password') } hint='Your desired new password' icon='lock' required
          value={this.state.newPwd} onChange={this.handleChange.bind(this, 'newPwd')} />
        <br />
        <Button icon='send' label={ _t.translate('Change') } raised primary onClick={this.handleSubmit}/>
        {this.generateRedirect()}
        <SiteSnackbar active={this.state.snackbarActive} toggleHandler={() => this.handleToggle()} label={this.state.snackbarLabel} />
      </section>
    );
  }
}

export default UpdatePasswordView;
