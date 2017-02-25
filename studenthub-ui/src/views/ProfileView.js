import React from 'react';
import { withRouter } from 'react-router-dom';
import Auth from '../Auth.js';

const LogoutButton = withRouter(({ push }) => (
  <button onClick={() => { Auth.signout(() => push('/')) }}>Sign out</button>
))

// profile page
class ProfileView extends React.Component {
  render() {
    return (
      <div>
        <h2>Protected Profile page!</h2>
        <LogoutButton />
      </div>
    )
  }
}

export default ProfileView
