import React from 'react';
import Input from 'react-toolbox/lib/input/Input.js';
import Button from 'react-toolbox/lib/button/Button.js';

import Util from '../Util.js';

class ForgotPasswordView extends React.Component {
  state = { email: '', redirect: false };

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  };

  handleSubmit = () => {
    //TODO: Send a verification request to server.
    Util.notify("info", "", "Not yet supported, please wait!");
    setTimeout(function() {
      this.setState({ redirect: true });
    }.bind(this), 1000);
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
        <Button icon='send' label='Retrieve' raised primary onClick={this.handleSubmit}/>
        {this.generateRedirect()}
      </section>
    );
  }
}

export default ForgotPasswordView;
