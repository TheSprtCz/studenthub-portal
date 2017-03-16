/* admin@studenthub.cz | admin123 */
insert into USERS(ID, USERNAME, EMAIL, PASSWORD, NAME) values (1, 'admin', 'admin@studenthub.cz', '$6$wvCyLU76$WvjE9aDoas8BT9T9teX1qrn3jys/cVXRr8.FlIN0nMBo4aHcwq/fCRhCvZmGUy/ofPMS60d6z8Ia1LQ3WIkAj1', 'Student Hub Admin');

insert into USER_ROLES values (1, 'ADMIN');
insert into USER_ROLES values (1, 'AC_SUPERVISOR');
insert into USER_ROLES values (1, 'TECH_LEADER');
insert into USER_ROLES values (1, 'COMPANY_REP');
insert into USER_ROLES values (1, 'STUDENT');