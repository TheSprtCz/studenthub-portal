import _t from './Translations';

var config = require('../config/config.json');

var Util = function() { };

/**
 * Checks whether an object is null or undefined
 * @param  object to check
 * @return boolean
 */
Util.isEmpty = function(object) {
  return (object === null) || (typeof object === 'undefined')
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

 * Holds state codes
 * @type {Enum}
 */
Util.countries = {
  CZ: "CZ",
  SK: "SK"
};

/**
 * Holds state codes for source use
 * @type {Enum}
 */
Util.countriesSource = [
  { value: 'CZ', label: 'Czechia' },
  { value: 'SK', label: 'Slovakia'},
]

/**
 * Holds user role codes
 * @type {Enum}
 */
Util.userRoles = {
  superviser: "AC_SUPERVISOR",
  admin: "ADMIN",
  companyRep: "COMPANY_REP",
  student: "STUDENT",
  techLeader: "TECH_LEADER"
}

Util.rolesSource = [
  { value: 'STUDENT', label: _t.translate('Student') },
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

/**
 * Holds Company plan codes
 * @type {Enum}
 */
Util.companyPlans = {
  t1: "TIER_1",
  t2: "TIER_2",
  t3: "TIER_3"
}

/**
 * Holds Company plan descriptions
 * @type {Enum}
 */
Util.companyPlanDescriptions = {
  t1: "TIER_1 description",
  t2: "TIER_2 description",
  t3: "TIER_3 description"
}

/**
 * Holds Company plan topic limits
 * @type {Enum}
 */
Util.companyPlanTopicLimits = {
  t1: 10,
  t2: 20,
  t3: 30
}

/**
 * Holds Company plan codes for source use
 * @type {Enum}
 */
Util.companyPlansSource = [
  { value: 'TIER_1', label: 'First tier' },
  { value: 'TIER_2', label: 'Second tier' },
  { value: 'TIER_3', label: 'Third tier'}
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

Util.degreesSource = [
    { value: 'HIGH_SCHOOL', label: _t.translate('High school')},
    { value: 'BACHELOR', label: _t.translate('Bachelor')},
    { value: 'MASTER', label: _t.translate('Master')},
    { value: 'PhD', label: _t.translate('PhD')}
]

Util.TOPICS_PER_PAGE = 5;
Util.TERMS_OF_USE = config.termsOfUse;
Util.PERSONAL_DATA_PROCESSING = config.personalDataProc;
Util.PORTAL_NAME = config.name;
Util.PORTAL_VERSION = config.version;
Util.TOKEN_COOKIE_NAME = "sh-token";
Util.ADMIN_EMAIL = config.adminEmail;

export default Util;
