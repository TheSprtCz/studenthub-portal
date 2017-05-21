import React from 'react';
import Button from 'react-toolbox/lib/button/Button.js';
import Drawer from 'react-toolbox/lib/drawer/Drawer.js';

import Logo from './Logo.js';
import ContactAdminButton from './ContactAdminButton.js';
import Util from '../Util.js';

class AboutDrawer extends React.Component {

  render() {
    return(
      <Drawer active={this.props.active} onOverlayClick={this.props.onOverlayClick} type="left">
        <Logo />
        <div style={{ padding: '20px' }}>
          <p>Current build version: { Util.PORTAL_VERSION }</p>
          <p>
            <ContactAdminButton />
          </p>
          <p>
            <Button label="View documentation" icon="assignment" target="_blank" href="https://student-hub.gitbooks.io/user-guide/" />
          </p>
          <p>
            <Button label="Report a bug" icon="bug_report" target="_blank" href="https://github.com/StudentHubCZ/studenthub-portal/issues" />
          </p>
          <hr className="m-y-2" />
          <p>Student Hub &copy; 2017</p>
        </div>
      </Drawer>
    );
  }
}

export default AboutDrawer;
