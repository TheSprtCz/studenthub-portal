import _t from './Translations';
import { NotificationManager } from 'react-notifications';

var config = require('./config.json');

var Util = function() { };

/**
 * Checks whether an object is null or undefined or an empty array
 * @param  object to check
 * @return boolean
 */
Util.isEmpty = function(object) {
  return (object === null) || (typeof object === 'undefined')
    || (object instanceof Array && object.length <= 0)
}

/**
 * Searches a given array for the first available ID value.
 * @param  array  the array to be searched
 * @return        first free id value
 */
Util.findFreeID = function(array) {
  var id;

  for (var i = 0; i < array.length; i++) {
    if(typeof array[i].id === "undefined") {
      id = i;
      break;
    }
    else id = array.length;
  }
  return id;
};

/**
 * Checks whether desired data value is correct.
 * @param data            data to be checked
 * @param errorMessage    associated error message
 * @return                errorMessage if incorect, "" otherwise
 */
Util.checkData = function(data, errorMessage) {
  if(data === "" || typeof data === "undefined")
    return errorMessage;
  else return "";
}

/**
 * Creates a new notification.
 * @param type            notification type
 * @param message         notification message
 * @param title           notification title
 */
Util.notify = (type, message, title) => {
  switch (type) {
    case 'info':
      NotificationManager.info(message, title, 5000);
      break;
    case 'success':
      NotificationManager.success(message, title, 5000);
      break;
    case 'warning':
      NotificationManager.warning(message, title, 5000);
      break;
    case 'error':
      NotificationManager.error(message, title, 5000);
      break;
    default:
      NotificationManager.info(message, title, 5000);
      break;
  };
};

/**
 * Holds user role codes
 * @type {Enum}
 */
Util.userRoles = {
  superviser: "AC_SUPERVISOR",
  admin: "ADMIN",
  companyRep: "COMPANY_REP",
  ambassador: "UNIVERSITY_AMB",
  student: "STUDENT",
  techLeader: "TECH_LEADER",
  proLeader: "PROJECT_LEADER"
}

Util.rolesSourceCompany = [
  { value: 'TECH_LEADER', label: _t.translate('Technical leader')},
  { value: 'COMPANY_REP', label: _t.translate('Company Representative') }
]

Util.rolesSourceUniversity = [
  { value: 'UNIVERSITY_AMB', label: _t.translate('University ambassador')},
  { value: 'AC_SUPERVISOR', label: _t.translate('Academic supervisor')}
]

/**
 * Holds Company size codes
 * @type {Enum}
 */
Util.companySizes = {
  startUp: "STARTUP",
  small: "SMALL",
  medium: "MEDIUM",
  corp: "CORPORATE"
}

/**
 * Holds Company size codes for source use
 * @type {Enum}
 */
Util.companySizesSource = [
  { value: 'STARTUP', label: 'Startup' },
  { value: 'SMALL', label: 'Small' },
  { value: 'MEDIUM', label: 'Medium'},
  { value: 'CORPORATE', label: 'Corporate' }
]

Util.gradesSource = [
    { value: 'A', label: 'A' },
    { value: 'B', label: 'B'},
    { value: 'C', label: 'C'},
    { value: 'D', label: 'D'},
    { value: 'E', label: 'E'},
    { value: 'F', label: 'F'},
    { value: 'NOT_FINISHED', label: 'Not Finished'}
]

Util.TOPICS_PER_PAGE = 5;
Util.TOPICS_PER_PAGE_TABLE = 15;
Util.APPLICATIONS_PER_PAGE = 8;
Util.USERS_PER_PAGE = 8;
Util.UNIVERSITIES_PER_PAGE = 15;
Util.FACULTIES_PER_PAGE = 20;
Util.COMPANIES_PER_PAGE = 15;
Util.PLANS_PER_PAGE = 15;
Util.TERMS_OF_USE = config.termsOfUse;
Util.PERSONAL_DATA_PROCESSING = config.personalDataProc;
Util.PORTAL_VERSION = config.version;
Util.PORTAL_NAME = config.name;
Util.TOKEN_COOKIE_NAME = "sh-token";
Util.ADMIN_EMAIL = config.adminEmail;

export default Util;
