import React from 'react';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';
import _t from '../Translations.js';
import Util from '../Util.js';

class ForgotPasswordView extends React.Component {
  state = { email: '' };

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
        Util.notify("info", "", "Your password has now been reset. A new activation link has been sent to your email.");
        setTimeout(function(){
          this.setState({ redirect: true });
        }.bind(this), 2000);
      } else {
        Util.notify("error", "", "An error occured! Your request couldn't be processed. It's possible that you have a problem with your internet connection or that the server is not responding.");
        throw new Error('There was a problem with network connection. POST request could not be processed!');
      }
    }.bind(this));
  };

  render () {
    return (
      <section className='text-center col-md-offset-3 col-md-6'>
        <h1>{ _t.translate('Password retrieval') }</h1>
        <Input type='email' label={ _t.translate('Email address') } hint='Email adress of the retrieved account' icon='email' required
          value={this.state.email} onChange={this.handleChange.bind(this, 'email')} />
        <br />
        <Button icon='send' label={ _t.translate('Retrieve') } raised primary onClick={this.handleSubmit}/>
      </section>
    );
  }
}

export default ForgotPasswordView;
