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
        {/* <p>You must log in to view the page at {from.pathname}</p> */}
        <h2 className='form-signin-heading text-center'>Please sign in</h2>
        <form id='loginForm' className='form-signin'>
          <label htmlFor="inputUsername" className="sr-only">Email address</label>
          <input type="email" name="username" id="inputUsername" className="form-control" placeholder="Email address" required autoFocus onChange={this.handleChange} />
          <label htmlFor="inputPassword" className="sr-only">Password</label>
          <input type="password" id="inputPassword" name="password" className='form-control' placeholder="Password" required onChange={this.handleChange} />
          <button className='btn btn-lg btn-primary btn-block' type='submit' onClick={this.handleSubmit}>Sign in</button>
        </form>
      </div>
    )
  }
}

export default LoginForm
