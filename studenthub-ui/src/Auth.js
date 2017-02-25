import * as Cookies from 'js-cookie';
import jwt_decode from "jwt-decode";

// "import" jquery
const $ = window.$;

// Auth "service"
const Auth = {

  isAuthenticated() {
    return !this.getToken() ? false : true;
  },

  authenticate(username, password, cb) {

    var settings = {
      "async": true,
      "crossDomain": true,
      "url": "http://localhost:8080/api/auth/login",
      "method": "POST",
      "headers": {
        "content-type": "application/x-www-form-urlencoded",
        "Access-Control-Expose-Headers": "Authorization",
        "cache-control": "no-cache"
      },
      "data": {
        "username": username,
        "password": password
      }
    }

    // console.log(settings);
    const that = this;
    $.ajax(settings).done(function ( data, textStatus, jqXHR) {
      var header = jqXHR.getResponseHeader("Authorization");
      console.log(header);
      that.setToken(header.replace('Bearer ',''));
    });

    setTimeout(cb, 100)
  },

  signout(cb) {
    this.deleteToken();
    setTimeout(cb, 100)
  },

  setToken(token) {
    Cookies.set('sh-token', token);
  },

  getToken() {
    return Cookies.get('sh-token');
  },

  deleteToken() {
    Cookies.remove('sh-token');
  },

  /*
    Returns decoded JWT token: display_name, email, iat, sub, $int_roles, $int_perms
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
      return $.inArray(role, userInfo.$int_roles) >= 0 ? true : false;
    }
  }
}

export default Auth
