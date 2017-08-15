# Initial data Database Structure

Initial Database migration file contains these entries:

## Countries

| tag    | name           |
| ------ | -------------- |
| CZ     | Czech Republic |
| SK     | Slovakia       |

## Universities

| id  | name                               | logoUrl                | city       | country | url          |
| --- | ---------------------------------- | ---------------------- | ---------- | ------  | ------------ |
| 1   | Masaryk University                 | www.muni.cz/img.png    | Brno       | CZ      | www.muni.cz  |

### Faculties of Masaryk University

| id  | name                               |
| --- | ---------------------------------- |
| 1   | Faculty of Informatics             |

## Company Plans

| name   | maxTopics | description      |
| ------ | --------- | ---------------- |
| TIER_1 | 3         | For basic use    |

## Companies

| id  | name                               | logoUrl                | city       | country | url            | size      | plan    |
| --- | ---------------------------------- | ---------------------- | ---------- | ------  | -------------- | --------- | ------- |
| 1   | Company One                        | c1.com/logo.png        | Brno       | CZ      | www.c1.com     | CORPORATE | TIER_1  | 

## Users

All users have password "test".

| id  | username           | email                       | name               | phone        | lastLogin      | company | faculty | roles                        | tags                     |
| --- | ------------------ | --------------------------- | ------------------ | ------------ | -------------- | ------- | ------- | ---------------------------- | ------------------------ | 
| 1   | superadmin         | superadmin@example.com      | Admin Admin        | 463 147 891  | 5.11.2016      | 1       | 1       | [ALL]                        | ------------------------ |
| 2   | supervisor         | supervisor@example.com      | Supervisor One     | 258 457 987  | 19.3.2017      | ------- | 1       | [SUPERVISOR]                 | [Java,Ruby,MU]           |
| 3   | leader             | leader@example.com          | Leader One         | 875 687 149  | 9.1.2017       | 1       | ------- | [LEADER]                     | [C++, Oracle, Ruby ]     |
| 4   | student            | student@example.com         | Student One        | 654 712 354  | 26.8.2016      | ------- | 1       | [STUDENT]                    | [MU, JavaScript, React]  |
| 5   | rep                | rep@example.com             | Rep One            | 154 798 416  | 6.5.2016       | 1       | ------- | [COMPANY_REP]                | ------------------------ |
| 6   | project            | project@example.com         | Project Leader     | 205 789 460  | 12.6.2017      | ------- | ------- | [PROJECT_LEADER]             | ------------------------ |
| 7   | ambassador         | ambassador@example.com      | Ambassador One     | 890 473 058  | 20.6.2017      | ------- | 1       | [UNIVERSITY_AMB]             | ------------------------ | 

## Topics

| id  | title               | secondaryTitle | enabled | highlighted | shortAbstract                       | description                | secondaryDescription     | creator | supervisors      | degrees                  | tags                      |
| --- | ------------------- | -------------- | ------- | ----------- |  ----------------------------------- | -------------------------- | -----------------------  | ------- | ---------------- | ------------------------ | ------------------------- |
| 1   | Dropwizard          | -------------- | true    | false       | Simple app using Dropwizard stack   | REST endpoints             | RESTové endpointy        | 3       | [2]              | [BACHELOR, MASTER]       | [Java, REST, Web]         |
| 2   | React UI            | Reactové UI    | false   | true        | Create nice and functional UI       | JavaScript & React         | ------------------------ | 3       | [2]              | [HIGH_SCHOOL,PhD]        | [JavaScript, Web]         |

## Projects

| id | name                 | description                 | creators     | topics    | faculties | companies |
| -- | -------------------- | --------------------------- | ------------ | --------- | --------- | --------- |
| 1  | Web stuff            | All stuff regarding web     | [3]          | [1]       | [1]       | [1]       |

## Topic Application

(Status is not actually field in DB and is decided by state of other fields)

| id  | topic | officialAssignment                                  | grade | degree     | status      | thesisFinish | thesisStarted | faculty | techLeader | student | supervisor | link                          |
| --- | ----- | --------------------------------------------------- | ----- | ---------- | ----------- | ------------ | ------------- | ------- | ---------- | ------- | ---------- | ----------------------------- |
| 1   | 1     | Static HTML page generator, powered by markdown     | A     | BACHELOR   | FINISHED    | 17.3.2017    | 2.10.2016     | 1       | 3          | 4       | 2          | ----------------------------- |
| 2   | 2     | UI for thesis management system                     | ----- | HIGH_SCHOOL| CREATED     | ------------ | 18.12.2016    | 1       | 3          | 4       | 2          | https://www.github.com        |

## Tasks

| id  | application | title                              | deadline   | completed |
| --- | ----------- | ---------------------------------- | ---------- | --------- |
| 1   | 1           | Reduce size                        | 25.11.2016 | false     |

