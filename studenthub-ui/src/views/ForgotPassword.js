import React from 'react';
import { Redirect } from 'react-router';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';

import SiteSnackbar from '../components/SiteSnackbar.js';

class ForgotPasswordView extends React.Component {
  state = { email: '', snackbarActive: false, snackbarLabel: '', redirect: false };

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  };

  handleSubmit = () => {
    //TODO: Send a verification request to server.
    this.setState({
      snackbarActive: true,
      snackbarLabel: "Not yet supported, please wait!"
    });
    setTimeout(function() {
      this.setState({ redirect: true });
    }.bind(this), 1000);
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
        <h1>Password Retrieval</h1>
        <Input type='email' label='Email address' hint='Email adress of the retrieved account' icon='email' required value={this.state.email} onChange={this.handleChange.bind(this, 'email')} />
        <br />
        <Button icon='send' label='Retrieve' raised primary onClick={this.handleSubmit}/>
        {this.generateRedirect()}
        <SiteSnackbar active={this.state.snackbarActive} toggleHandler={() => this.handleToggle()} label={this.state.snackbarLabel} />
      </section>
    );
  }
}

export default ForgotPasswordView;
