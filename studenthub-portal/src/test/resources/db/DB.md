# Test Database Structure

Test Database contains these entries:

## Countries

| tag    | name           |
| ------ | -------------- |
| CZ     | Czech Republic |
| SK     | Slovakia       |
| HU     | Hungary        |

## Universities

| id  | name                               | logoUrl                | city       | country | url          |
| --- | ---------------------------------- | ---------------------- | ---------- | ------  | ------------ |
| 1   | Masaryk University                 | www.muni.cz/img.png    | Brno       | CZ      | www.muni.cz  |
| 2   | Brno University of Technology      | www.vut.cz/logo.png    | Brno       | CZ      | www.vut.cz   |
| 3   | Czech Technology University        | www.cvut.cz/image.jpg  | Praha      | CZ      | www.cvut.cz  |
| 4   | Slovak Technical University        | www.stuba.sk/logo.jpg  | Bratislava | SK      | www.stuba.sk |
| 5   | Comenius University in Bratislava  | www.uniba.sk/logo.gif  | Bratislava | SK      | www.uniba.sk |

### Faculties of Masaryk University

| id  | name                               |
| --- | ---------------------------------- |
| 1   | Faculty of Informatics             |
| 2   | Faculty of Arts                    |
| 3   | Faculty of Medicine                |
| 4   | Faculty of Law                     |

### Faculties of Brno University of Technology

| id  | name                                                                   |
| --- | ---------------------------------------------------------------------- |
| 5   | Faculty of Information Technology                                      |
| 6   | Faculty of Electrical Engineering and Communication                    |
| 7   | Faculty of Chemistry                                                   |

### Faculties of Czech Technology University

| id   | name                                                                   |
| ---- | ---------------------------------------------------------------------- |
| 8    | Faculty of Electrical Engineering                                      |
| 9    | Faculty of Mechanical Engineering                                      |
| 10   | Faculty of Architecture                                                |

### Faculties of Slovak Technical University

| id   | name                                                                   |
| ---- | ---------------------------------------------------------------------- |
| 11   | Faculty of Electrical Engineering and Information Technology           |
| 12   | Faculty of Chemical and Food Technology                                |
| 13   | Faculty of Civil Engineering                                           |

## Company Plans

Infinite is represented as 0 in DB

| name   | maxTopics | description      |
| ------ | --------- | ---------------- |
| TIER_1 | 3         | For basic use    |
| TIER_2 | 5         | Meh              |
| TIER_3 | 10        | Good enough      |
| TIER_4 | INFINITE  | Godlike          |

## Companies

| id  | name                               | logoUrl                | city       | country | url            | size      | plan    |
| --- | ---------------------------------- | ---------------------- | ---------- | ------  | -------------- | --------- | ------- |
| 1   | Company One                        | c1.com/logo.png        | Brno       | CZ      | www.c1.com     | CORPORATE | TIER_3  | 
| 2   | Company Two                        | c2.com/logo.jpg        | Bratislava | SK      | www.c2.com     | MEDIUM    | TIER_2  | 
| 3   | Company Three                      | c3.com/img.jpg         | Prague     | CZ      | www.c3.com     | SMALL     | TIER_3  |
| 4   | Company Four                       | c4.com/picture.jpg     | Brno       | CZ      | www.c4.com     | STARTUP   | TIER_1  |
| 5   | Company Five                       | c5.com/us.jpg          | Prague     | CZ      | www.c5.com     | MEDIUM    | TIER_3  |
| 6   | Company Six                        | c6.com/us.gif          | Olomouc    | CZ      | www.c6.com     | CORPORATE | TIER_2  |
| 7   | Company Seven                      | c7.com/img.png         | Trencin    | SK      | www.c7.com     | MEDIUM    | TIER_3  |
| 8   | Company Eight                      | c8.com/logo.jpg        | Myjava     | Sk      | www.c8.com     | STARTUP   | TIER_1  |

## Users

Password field is not present because it is encoded and irrelevant when testing and password encoding test does not need DB

| id  | username           | email                       | name               | phone        | lastLogin      | company | faculty | roles                        | tags                     |
| --- | ------------------ | --------------------------- | ------------------ | ------------ | -------------- | ------- | ------- | ---------------------------- | ------------------------ | 
| 1   | admin              | admin@example.com           | Admin One          | 123 456 789  | 17.2.2016      | ------- | ------- | [ADMIN]                      | ------------------------ |
| 2   | supervisor1        | supervisor1@example.com     | Supervisor One     | 258 457 987  | 19.3.2017      | ------- | 1       | [SUPERVISOR]                 | [Java,Ruby,MU]           |
| 3   | supervisor2        | supervisor2@example.com     | Supervisor Two     | 147 875 369  | 18.2.2016      | ------- | 5       | [SUPERVISOR]                 | [Python]                 |
| 4   | supervisor3        | supervisor3@example.com     | Supervisor Three   | 268 745 137  | 5.3.2017       | ------- | 8       | [SUPERVISOR]                 | [Python, Java, C, C#]    |
| 5   | supervisor4        | supervisor4@example.com     | Supervisor Four    | 349 879 152  | 4.2.2017       | ------- | 2       | [SUPERVISOR]                 | [Basic, Bash]            |
| 6   | supervisor5        | supervisor5@example.com     | Supervisor Five    | 777 888 999  | 25.7.2016      | ------- | 2       | [SUPERVISOR]                 | [Java, Bash              |
| 7   | supervisor6        | supervisor6@example.com     | Supervisor Six     | 111 222 333  | 23.10.2016     | ------- | 6       | [SUPERVISOR]                 | [C++, JavaScript, React] |
| 8   | leader1            | leader1@example.com         | Leader One         | 875 687 149  | 9.1.2017       | 1       | ------- | [LEADER]                     | [C++, Oracle, Ruby ]     |
| 9   | leader2            | leader2@example.com         | Leader Two         | 578 962 759  | 8.12.2016      | 5       | ------- | [LEADER]                     | [C#, Oracle, Ruby]       |
| 10  | leader3            | leader3@example.com         | Leader Three       | 872 697 159  | 14.1.2017      | 6       | ------- | [LEADER]                     | [Python, Java]           |
| 11  | leader4            | leader4@example.com         | Leader Four        | 246 789 982  | 25.12.2016     | 2       | ------- | [LEADER]                     | [Basic, Ruby]            |
| 12  | student1           | student1@example.com        | Student One        | 654 712 354  | 26.8.2016      | ------- | 1       | [STUDENT]                    | [MU, JavaScript, React]  |
| 13  | student2           | student2@example.com        | Student Two        | 498 752 858  | 15.3.2017      | ------- | 5       | [STUDENT]                    | [Oracle, Python, C]      |
| 14  | student3           | student3@example.com        | Student Three      | 568 426 377  | 31.11.2016     | ------- | 1       | [STUDENT]                    | [C#, C++, C]             |
| 15  | student4           | student4@example.com        | Student Four       | 731 579 246  | 5.9.2016       | ------- | 2       | [STUDENT]                    | [Ruby, W3C]              |
| 16  | rep1               | rep1@example.com            | Rep One            | 154 798 416  | 6.5.2016       | 1       | ------- | [COMPANY_REP]                | ------------------------ |
| 17  | rep2               | rep2@example.com            | Rep Two            | 871 423 698  | 27.4.2016      | 8       | ------- | [COMPANY_REP]                | ------------------------ | 
| 18  | rep3               | rep3@example.com            | Rep Three          | 234 156 774  | 27.1.2017      | 7       | ------- | [COMPANY_REP]                | ------------------------ | 
| 19  | superadmin         | superadmin@example.com      | Super Admin        | 463 147 891  | 5.11.2016      | 1       | 1       | [ALL]                        | ------------------------ |
| 20  | project            | project@example.com         | Project Leader     | 205 709 548  | 10.6.2017      | ------- | ------- | [PROJECT_LEADER]             | ------------------------ |

## Activations

Codes were manually assigned to ease testing

| id  | user  | code       |
| --- | ----- | ---------- |
| 1   | 18    | rep3       |
| 2   | 15    | student4   |

## TopicDegrees

| name        | description      |
| ----------- | ---------------- |
| BACHELOR    | Undergraduate    |
| MASTER      | Graduate         |
| PhD         | Simply PHD       |
| HIGH_SCHOOL | High school      |
| DELETABLE   | Useless          |

## Topics

| id  | title               | secondaryTitle  | enabled | shortAbstract                       | description                | secondaryDescription    | creator | supervisors      | degrees                  | tags                      |
| --- | ------------------- | --------------- | ------- | ----------------------------------- | -------------------------- | ----------------------- | ------- | ---------------- | ------------------------ | ------------------------- |
| 1   | Melon cutter        | Kráječ melounů  | true    | Create simple laser melon cutter    | Laser melon cutter         | Laserový kráječ melounů | 9       | [3,4,6]          | [PhD,MASTER]             | [Laser, Assembler, C]     |
| 2   | Dropwizard          | --------------- | true    | Simple app using Dropwizard stack   | REST endpoints             | RESTové endpointy       | 11      | [7,5,2]          | [BACHELOR, HIGH_SCHOOL]  | [Java, REST, Web]         |
| 3   | Eclipse plugin      | --------------- | true    | Create custom eclipse plugin        | Eclipse                    | Eclipse                 | 10      | [5,7,4]          | [MASTER, BACHELOR]       | [Java, C, Maven]          |
| 4   | React UI            | --------------- | true    | Create nice and functional UI       | JavaScript & React         | ----------------------- | 9       | [6,3,2]          | [HIGH_SCHOOL,PhD]        | [JavaScript, Web]         |
| 5   | Thesis management   | Správa diplomek | false   | Create new TMS                      | Web                        | ----------------------- | 10      | [7]              | [PhD]                    | [Web]                     |
                
## Projects

| id | name                 | description                 | creators     | topics    | faculties | companies |
| -- | -------------------- | --------------------------- | ------------ | --------- | --------- | --------- |
| 1  | Web stuff            | All stuff regarding web     | [8,9]        | [2,4]     | [1,2,6,5] | [2,5]     |
| 2  | Industry things      | Ehm, no idea                | [10]         | [1,5]     | [5,8,6,2] | [6]       |

## Topic Application

(Status is not actually field in DB and is decided by state of other fields)

| id  | topic | officialAssignment                                  | grade | degree     | status      | thesisFinish | thesisStarted | faculty | techLeader | student | supervisor | link                                                             |
| --- | ----- | --------------------------------------------------- | ----- | ---------- | ----------- | ------------ | ------------- | ------- | ---------- | ------- | ---------- | ---------------------------------------------------------------- |
| 1   | 1     | Melon cutter suitable for household use             | ----- | PhD        | IN_PROGRESS | ------------ | 17.9.2016     | 2       | 8          | 12      | 6          | https://www.github.com                                           |
| 2   | 1     | Melon cutter for industrial use                     | F     | MASTER     | FINISHED    | 18.2.2017    | 15.9.2016     | 5       | 9          | 13      | 3          | ---------------------------------------------------------------- |
| 3   | 1     | Laser melon cutter using 20 W laser                 | ----- | PhD        | WAITING     | ------------ | ------------- | 8       | 8          | 13      | 4          | ---------------------------------------------------------------- |
| 4   | 2     | Static HTML page generator, powered by markdown     | A     | BACHELOR   | FINISHED    | 17.3.2017    | 2.10.2016     | 1       | 11         | 14      | 2          | https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet |
| 5   | 2     | Infinite Pi digits generator                        | ----- | HIGH_SCHOOL| WAITING     | ------------ | ------------- | 6       | 11         | 15      | 7          | ---------------------------------------------------------------- |
| 6   | 3     | .JSX editor plugin                                  | ----- | MASTER     | IN_PROGRESS | ------------ | 5.3.2017      | 2       | 10         | 14      | 5          | ---------------------------------------------------------------- |
| 7   | 4     | UI for thesis management system                     | B     | HIGH_SCHOOL| FINISHED    | 10.4.2017    | 18.12.2016    | 1       | 9          | 13      | 2          | https://github.com/StudentHubCZ/studenthub-portal                |

## Tasks

| id  | application | title                              | deadline   | completed |
| --- | ----------- | ---------------------------------- | ---------- | --------- |
| 1   | 1           | Reduce size                        | 25.11.2016 | true      |
| 2   | 1           | Update software                    | 7.2.2017   | false     |
| 3   | 1           | Test yellow melons                 | 30.1.2017  | true      |
| 4   | 3           | Upgrade to better laser            | 31.12.2016 | true      |
| 5   | 4           | Add support for GFM                | 12.11.2016 | false     |
| 6   | 6           | Create prototype                   | 18.5.2017  | false     |
