import React from 'react';
import { withRouter } from 'react-router-dom';

import LoginForm from '../components/LoginForm.js';

const LoginFormRouter = withRouter(LoginForm);

class SignIn extends React.Component {
  render () {
    return (
      <div>
        <LoginFormRouter />
      </div>
    );
  }
}

export default SignIn
