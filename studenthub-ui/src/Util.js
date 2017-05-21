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
  { value: 'STUDENT', label: 'Student' },
  { value: 'AC_SUPERVISOR', label: 'Academic Supervisor'}
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
    { value: 'BACHELOR', label: 'Bachelor Degree' },
    { value: 'MASTER', label: 'Master Degree'},
    { value: 'PhD', label: 'PhD Degree'}
]

Util.TOPICS_PER_PAGE = 5;

Util.TERMS_OF_USE = 'Please read these Terms and Conditions ("Terms", "Terms and Conditions") carefully before using the http://www.mywebsite.com (change this) website and the My Mobile App (change this) mobile application (the "Service") operated by My Company (change this) ("us", "we", or "our").'

Util.PERSONAL_DATA_PROCESSING = 'Please read these Terms and Conditions ("Terms", "Terms and Conditions") carefully before using the http://www.mywebsite.com (change this) website and the My Mobile App (change this) mobile application (the "Service") operated by My Company (change this) ("us", "we", or "our").';

Util.PORTAL_VERSION = "1.0";

Util.TOKEN_COOKIE_NAME = "sh-token";

export default Util;
