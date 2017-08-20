import * as Cookies from 'js-cookie';
import jwt_decode from "jwt-decode";
import Util from './Util.js';

// "import" jquery
const $ = window.$;

// Auth "service"
const Auth = {

  isAuthenticated() {
    return !this.getToken() ? false : true;
  },

  /**
   * Obtains JWT token from backend which subsequently stores it in Browser cookies
   */
  authenticate(username, password, cb, failCb) {

    var settings = {
      "url": "/api/auth/login",
      "method": "POST",
      "xhrFields": {
        "withCredentials": true
      },
      "headers": {
        "content-type": "application/x-www-form-urlencoded"
      },
      "data": {
        "username": username,
        "password": password
      }
    }

    $.ajax(settings)
      .done(function() {
        if (!this.getToken()) {
          // cookie was not set
          Util.notify('error', 'You need to enable cookies in your browser for Thesis Hub to work.', 'Cookies required!');
        } else {
          // cookie was set
          Util.notify('info', 'You are now succesfully logged in.', 'Succesfully logged in.')
          // trigger succes callback
          setTimeout(cb, 500);
        }
      }.bind(this))
      .fail(function() {
        // auth failed
        Util.notify('error', 'You credentials does not match our record. Please try again.', 'Authentication failed.');
        // trigger failure callback
        setTimeout(failCb, 500);
      });
  },

  /**
   * Sends POST request to logout and delete auth Cookie
   */
  signout(cb) {

    var settings = {
      "url": "/api/auth/logout",
      "method": "POST",
      "xhrFields": {
        "withCredentials": true
      }
    }

    $.ajax(settings)
      .done(function (data, textStatus, jqXHR) {
      // console.log(textStatus);
      Util.notify('info', 'You have been succesfully logged out.', 'Sign Out')
      // if cookie was not un-set
      Cookies.remove(Util.TOKEN_COOKIE_NAME);
    });

    setTimeout(cb, 500)
  },

  getToken() {
    return Cookies.get(Util.TOKEN_COOKIE_NAME);
  },

  /*
    Returns decoded JWT token:
    "sub": "1",
    "exp": 1487091708,
    "iat": 1573491708,
    "name": "Student Hub Admin",
    "email": "admin@studenthub.cz",
    "roles": [
      "STUDENT",
      "COMPANY_REP",
      "AC_SUPERVISOR",
      "ADMIN",
      "TECH_LEADER"
    ]
  */
  getUserInfo() {
    if (!this.getToken()) {
      return null;
    } else {
      return jwt_decode(this.getToken());
    }
  },

  /*
    Return bool depending on whether user posses a certain role
  */
  hasRole(role) {
    const userInfo = this.getUserInfo();
    if (!userInfo) {
      return false;
    } else {
      return $.inArray(role, userInfo.roles) >= 0 ? true : false;
    }
  }
}

export default Auth
