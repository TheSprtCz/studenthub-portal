import React from 'react';
import { Redirect } from 'react-router';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';

import SiteSnackbar from '../components/SiteSnackbar.js';
import _t from '../Translations.js';

class ForgotPasswordView extends React.Component {
  state = { email: '', snackbarActive: false, snackbarLabel: '', redirect: false };

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  };

  handleSubmit = () => {
    var data = new URLSearchParams();
    data.append("email", this.state.email);

    fetch('/api/account/resetPassword', {
      method: 'post',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: data
    }).then(function(response) {
      if (response.ok) {
        this.setState({
          snackbarLabel: "Your password has now been reset. A new activation link has been sent to your email.",
          snackbarActive: true
        });
        setTimeout(function(){
          this.setState({ redirect: true });
        }.bind(this), 2000);
      }
      else if (response.status === 404) {
        this.setState({
          snackbarLabel: "No such email adress has been found!",
          snackbarActive: true
        });
      }
      else {
        this.setState({
          snackbarLabel: "An error occured! Your request couldn't be processed. It's possible that you have a problem with your internet connection or that the server is not responding.",
          snackbarActive: true
        });
        throw new Error('There was a problem with network connection. POST request could not be processed!');
      }
    }.bind(this));
  };

  handleToggle = () => {
    this.setState({ snackbarActive: false });
  };

  generateRedirect = () => {
    if(this.state.redirect === false) return;
    else return (<Redirect to="/signin" />);
  }

  render () {
    return (
      <section className='text-center col-md-offset-3 col-md-6'>
        <h1>{ _t.translate('Password retrieval') }</h1>
        <Input type='email' label={ _t.translate('Email address') } hint='Email adress of the retrieved account' icon='email' required
          value={this.state.email} onChange={this.handleChange.bind(this, 'email')} />
        <br />
        <Button icon='send' label={ _t.translate('Retrieve') } raised primary onClick={this.handleSubmit}/>
        {this.generateRedirect()}
        <SiteSnackbar active={this.state.snackbarActive} toggleHandler={() => this.handleToggle()} label={this.state.snackbarLabel} />
      </section>
    );
  }
}

export default ForgotPasswordView;
