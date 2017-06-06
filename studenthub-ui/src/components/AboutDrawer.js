import React from 'react';
import Button from 'react-toolbox/lib/button/Button.js';
import Drawer from 'react-toolbox/lib/drawer/Drawer.js';

import Logo from './Logo.js';
import Util from '../Util.js';
import _t from '../Translations.js';

class AboutDrawer extends React.Component {

  handleLangSwitch = () => {
    var locale = localStorage.getItem("sh-locale") === 'en' ? 'cs' : 'en';
    localStorage.setItem("sh-locale", locale);
    _t.setLocale(locale);
  }

  render() {
    const emailLink = "mailto:" + Util.ADMIN_EMAIL;

    return(
      <Drawer active={this.props.active} onOverlayClick={this.props.onOverlayClick} type="left">
        <Logo />
        <div style={{ padding: '20px' }}>
          <p>Current build version: { Util.PORTAL_VERSION }</p>
          <p>
            <Button label={ _t.translate('Contact us') } icon="feedback" href={ emailLink } />
          </p>
          <p>
            <Button label={ _t.translate('View documentation') } icon="assignment" target="_blank" href="https://student-hub.gitbooks.io/user-guide/" />
          </p>
          <p>
            <Button label={ _t.translate('Report a bug') } icon="bug_report" target="_blank" href="https://github.com/StudentHubCZ/studenthub-portal/issues/new" />
          </p>
          <p>
            <Button label={ localStorage.getItem("sh-locale") === 'en' ? 'Česky' : 'English' } icon="language" onClick={() => this.handleLangSwitch()} />
          </p>
          <hr className="m-y-2" />
          <p>Student Hub &copy; 2017</p>
        </div>
      </Drawer>
    );
  }
}

export default AboutDrawer;
