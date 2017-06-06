import React from 'react';
import { Redirect } from 'react-router-dom';
import { Link } from 'react-router-dom';
import Auth from '../Auth.js';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';

import _t from '../Translations.js';

class LoginForm extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      email: '',
      password: '',
      redirectToReferrer: false
    };
  }

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  }

  handleSubmit = () => {
    Auth.authenticate(this.state.email, this.state.password, () => {
      this.setState({ redirectToReferrer: true })
    });
  }

  render() {
    const { from } = this.props.location.state || { from: { pathname: '/' } }
    const { redirectToReferrer } = this.state

    if (redirectToReferrer) {
      return (
        <Redirect push to={from}/>
      )
    }

    return (
      <div className='text-center col-md-offset-3 col-md-6'>
        <h1>{ _t.translate('Sign In') }</h1>
        <Input type='email' label={ _t.translate('Email address') } icon='email' value={this.state.email} onChange={this.handleChange.bind(this, 'email')} required />
        <Input type='password' label={ _t.translate('Password') } icon='lock' value={this.state.password} onChange={this.handleChange.bind(this, 'password')} required />
        <Button raised primary label={ _t.translate('Sign In') } onClick={() => this.handleSubmit()} />
        <p style={{ paddingTop: '30px'}}>{ _t.translate('Sign Up')} <Link to="/signup">{ _t.translate('here') }</Link>.</p>
        {/* <p><Link to="/forgot">{ _t.translate('Forgot your password') }?</Link></p> */}
      </div>
    )
  }
}

export default LoginForm
