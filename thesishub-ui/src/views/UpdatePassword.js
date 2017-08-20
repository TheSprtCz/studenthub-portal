import React from 'react';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';

import Auth from '../Auth.js';
import _t from '../Translations.js';
import Util from '../Util.js';

class UpdatePasswordView extends React.Component {
  state = { oldPwd: '', newPwd: '' };

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  };

  handleSubmit = () => {
    fetch('/api/account/' + Auth.getUserInfo().sub + '/password', {
      method: 'put',
      credentials: 'same-origin',
      headers: { "Content-Type" : "application/json" },
      body: JSON.stringify({
        newPwd: this.state.newPwd,
        oldPwd: this.state.oldPwd
      })
    }).then(function(response) {
        if (response.ok) {
          Util.notify("info", "", "Your password has been succesfully changed!");
          setTimeout(function(){
            this.setState({ redirect: true });
          }.bind(this), 2000);
      }
      else if (response.status === 400) {
        Util.notify("error", "", "Your password doesn't match our records.");
      }
      else {
        Util.notify("error", "", "An error occured! Your request couldn't be processed. It's possible that you have a problem with your internet connection or that the server is not responding.");
        throw new Error('There was a problem with network connection. PUT request could not be processed!');
      }
    }.bind(this));
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
      </section>
    );
  }
}

export default UpdatePasswordView;
