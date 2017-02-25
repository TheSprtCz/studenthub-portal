import React from 'react';
import { withRouter } from 'react-router-dom';
import LoginForm from '../components/LoginForm.js';

// we need to pass router props
const LoginFormRouter = withRouter(LoginForm);

// login page
class LoginView extends React.Component {
  render() {
    return (
      <div>
        <LoginFormRouter />
      </div>
    )
  }
}

export default LoginView
