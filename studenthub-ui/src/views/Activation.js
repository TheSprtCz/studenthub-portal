import React from 'react';
import { Redirect } from 'react-router-dom';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';

import SiteSnackbar from '../components/SiteSnackbar.js';

import _t from '../Translations.js';

class ActivationForm extends React.Component {
  state = { password: '', redirect: false, snackbarActive: false, snackbarLabel: '' };

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
        this.setState({
          snackbarLabel: "Your account has been succesfully activated! You will now be redirected to the sign in page.",
          snackbarActive: true
        });
        setTimeout(function(){
          this.setState({ redirect: true });
        }.bind(this), 2000);
      } else {
        this.setState({
          snackbarLabel: "An error occured! Your request couldn't be processed. It's possible that you have a problem with your internet connection or that the server is not responding.",
          snackbarActive: true
        });
        throw new Error('There was a problem with network connection. POST request could not be processed!');
      }
    }.bind(this));
  }

  handleToggle = () => {
    this.setState({ snackbarActive: !this.state.snackbarActive });
  }

  render() {
    return (
      <div>
        <Input type='password' label={ _t.translate('Password') } hint="Here you can choose your password" icon='lock' value={this.state.password}
          onChange={this.handleChange.bind(this, 'password')} required />
        <Button raised primary label={ _t.translate('Save changes') } onClick={() => this.handleSubmit()} />
        <SiteSnackbar active={this.state.snackbarActive} label={this.state.snackbarLabel} toggleHandler={() => this.handleToggle()} />
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
