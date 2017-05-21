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
  authenticate(username, password, cb) {

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

    // console.log(settings);
    $.ajax(settings).done(function (data, textStatus, jqXHR) {
      console.log(textStatus);
      // TODO: if cookie was not set but Authorization header was returned
    });

    setTimeout(cb, 500)
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

    // console.log(settings);
    $.ajax(settings).done(function (data, textStatus, jqXHR) {
      console.log(textStatus);
      // TODO: if cookie was not un-set
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
