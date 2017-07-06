import React from 'react';
import { Redirect } from 'react-router-dom';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';

import _t from '../Translations.js';
import Util from '../Util.js';

class ActivationForm extends React.Component {
  state = { password: '', redirect: false };

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  }

  handleSubmit = () => {
    var data = new URLSearchParams();
    data.append("password", this.state.password);

    fetch('/api/account/activate?secret=' + this.props.secret + '&id='+this.props.id, {
      method: 'post',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: data
    }).then(function(response) {
      if (response.ok) {
        Util.notify("success", "You will now be redirected to the sign in page.", "Your account has been succesfully activated!");
        setTimeout(function(){
          this.setState({ redirect: true });
        }.bind(this), 2000);
      } else {
        Util.notify("error", "It's possible that you have a problem with your internet connection or that the server is not responding.", "Your request couldn't be processed.");
        throw new Error('There was a problem with network connection. POST request could not be processed!');
      }
    }.bind(this));
  }

  render() {
    return (
      <div>
        <Input type='password' label={ _t.translate('Password') } hint="Here you can choose your password" icon='lock' value={this.state.password}
          onChange={this.handleChange.bind(this, 'password')} required />
        <Button raised primary label={ _t.translate('Save changes') } onClick={() => this.handleSubmit()} />
        {(this.state.redirect) ? <Redirect to="/signin" /> : ''}
      </div>
    )
  }
}

const ActivationView = ({ match }) => (
  <div className='text-center col-md-offset-3 col-md-6'>
    <h1>{ _t.translate('User activation') }</h1>
    <ActivationForm id={match.params.id} secret={match.params.secret} />
  </div>
);

export default ActivationView
