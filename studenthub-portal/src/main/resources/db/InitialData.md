# Initial data Database Structure

Initial Database migration file contains these entries:

## Universities

| id  | name                               | logoUrl                | city       | country | url          |
| --- | ---------------------------------- | ---------------------- | ---------- | ------  | ------------ |
| 1   | Masaryk University                 | www.muni.cz/img.png    | Brno       | CZ      | www.muni.cz  |

### Faculties of Masaryk University

| id  | name                               |
| --- | ---------------------------------- |
| 1   | Faculty of Informatics             |

## Companies

| id  | name                               | logoUrl                | city       | country | url            | size      | plan    |
| --- | ---------------------------------- | ---------------------- | ---------- | ------  | -------------- | --------- | ------- |
| 1   | Company One                        | c1.com/logo.png        | Brno       | CZ      | www.c1.com     | CORPORATE | TIER_3  | 

## Users

All users have password "test".

| id  | username           | email                       | name               | phone        | lastLogin      | company | faculty | roles                        | tags                     |
| --- | ------------------ | --------------------------- | ------------------ | ------------ | -------------- | ------- | ------- | ---------------------------- | ------------------------ | 
| 1   | superadmin         | superadmin@example.com      | Admin Admin        | 463 147 891  | 5.11.2016      | ------- | ------- | [ALL]                        | ------------------------ |
| 2   | supervisor         | supervisor@example.com      | Supervisor One     | 258 457 987  | 19.3.2017      | ------- | 1       | [SUPERVISOR]                 | [Java,Ruby,MU]           |
| 3   | leader             | leader@example.com          | Leader One         | 875 687 149  | 9.1.2017       | 1       | ------- | [LEADER]                     | [C++, Oracle, Ruby ]     |
| 4   | student            | student@example.com         | Student One        | 654 712 354  | 26.8.2016      | ------- | 1       | [STUDENT]                    | [MU, JavaScript, React]  |
| 5   | rep                | rep@example.com             | Rep One            | 154 798 416  | 6.5.2016       | 1       | ------- | [COMPANY_REP]                | ------------------------ |

## Topics

| id  | title               | enabled | shortAbstract                       | description                | secondaryDescription     | creator | supervisors      | degrees                  | tags                      |
| --- | ------------------- | ------- | ----------------------------------- | -------------------------- | -----------------------  | ------- | ---------------- | ------------------------ | ------------------------- |
| 1   | Dropwizard          | true    | Simple app using Dropwizard stack   | REST endpoints             | RESTové endpointy        | 3       | [2]              | [BACHELOR, MASTER]       | [Java, REST, Web]         |
| 2   | React UI            | false   | Create nice and functional UI       | JavaScript & React         | ------------------------ | 3       | [2]              | [HIGH_SCHOOL,PhD]        | [JavaScript, Web]         |

## Topic Application

(Status is not actually field in DB and is decided by state of other fields)

| id  | topic | officialAssignment                                  | grade | degree     | status      | thesisFinish | thesisStarted | faculty | techLeader | student | supervisor |
| --- | ----- | --------------------------------------------------- | ----- | ---------- | ----------- | ------------ | ------------- | ------- | ---------- | ------- | ---------- |
| 1   | 1     | Static HTML page generator, powered by markdown     | A     | BACHELOR   | FINISHED    | 17.3.2017    | 2.10.2016     | 1       | 3          | 4       | 2          |
| 2   | 2     | UI for thesis management system                     | ----- | HIGH_SCHOOL| CREATED     | ------------ | 18.12.2016    | 1       | 3          | 4       | 2          |

## Tasks

| id  | application | title                              | deadline   | completed |
| --- | ----------- | ---------------------------------- | ---------- | --------- |
| 1   | 1           | Reduce size                        | 25.11.2016 | false     |

