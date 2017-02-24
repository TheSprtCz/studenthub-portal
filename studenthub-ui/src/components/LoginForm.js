import React from 'react';
import { Redirect } from 'react-router-dom';
import Auth from '../Auth.js';

class LoginForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {username: '', password: '', redirectToReferrer: false};

    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(event) {
    const value = event.target.value;
    const name = event.target.name;

    this.setState({
      [name]: value
    });
  }

  handleSubmit(event) {
    event.preventDefault();
    Auth.authenticate(this.state.username, this.state.password, () => {
      this.setState({ redirectToReferrer: true })
    })
  }

  render() {
    const { from } = this.props.location.state || { from: { pathname: '/' } }
    const { redirectToReferrer } = this.state

    if (redirectToReferrer) {
      return (
        <Redirect to={from}/>
      )
    }

    return (
      <div>
        <p>You must log in to view the page at {from.pathname}</p>
        <form id='loginForm'>
          <input type="text" name="username" onChange={this.handleChange} />
          <input type="password" name="password" onChange={this.handleChange} />
          <button onClick={this.handleSubmit}>Log in</button>
        </form>
      </div>
    )
  }
}

export default LoginForm
