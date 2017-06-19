# List of all notifications

This list servers as guide to what information are saved to Database when specific notifaction is created.
Parameters are available under variable called `content` and are encoded as JSON.

## ApplicationCreated

### Title

New Application was created for Topic ${topic-name}

### Template

<p>User <a href="${student-url}">${student-name}</a> created new application for your topic <a href="${topic-url}">${topic-name}</a>.</p>
<p>Click <a href="${url}">here</a> to view it.</p>

### Users

* From: Student who is assigned to application.
* Target: Topic creator and all supervisors from same faculty.

### Parameters
 
| name         | description                                        | 
| ------------ | -------------------------------------------------- |
| url          | Url of the new application                         |
| student-name | Name of the student who is assigned to application |
| student-url  | Url of the student who is assigned to application  |
| topic-name   | Name of the topic                                  |
| topic-url    | Url of the topic                                   |

## TaskCreated

### Title

New task was created

### Template

<p>User <a href="${submitter-url}">${submitter-name}</a> created new task <a href="${task-url}">${task-name}</a> for application <a href="${app-url}">${app-name}</a>.</p>

### Users

* From: Submitter.
* Target: All other people that are connected to application (Student, Supervisor, TechLeader)

### Parameters

| name           | description                                        | 
| -------------- | -------------------------------------------------- |
| task-url       | Url of newly created task                          |
| task-name      | Name of newly created task                         |
| submitter-name | Name of the student who is assigned to application |
| submitter-url  | Url of the student who is assigned to application  |
| app-name       | Title of the Application (Topic title)             |
| app-url        | Url of the application                             |

## ApplicationGraded

### Title

Your application has received grade ${grade}.

### Template

<p>User <a href="${submitter-url}">${submitter-name}</a> graded your application <a href="${app-url}">${app-name}</a> with grade <strong>${grade}</strong>.</p>

### Users

* From: Submitter.
* Target: Student.

### Parameters

| name           | description                                        | 
| -------------- | -------------------------------------------------- |
| submitter-name | Name of the student who is assigned to application |
| submitter-url  | Url of the student who is assigned to application  |
| app-name       | Title of the Application (Topic title)             |
| app-url        | Url of the application                             |
| grade          | Grade                                              |

## ProjectUpdated

Fired when project is updated

### Title

Project ${project-name} was updated

### Template

<p>User <a href="${submitter-url}">${submitter-name}</a> updated your project <a href="${project-url}">${project-name}</a>.</p>

### Users

* From: Submitter.
* Target: All creators except the one who triggered it.

### Parameters

| name           | description                                        | 
| -------------- | -------------------------------------------------- |
| submitter-name | Name of the student who is assigned to application |
| submitter-url  | Url of the student who is assigned to application  |
| project-name   | Name of the project                                |
| project-url    | Url of the project                                 |

## SupervisorAdded

Fired when supervisor is added to Topic

### Title

Supervisor was added to your Topic ${topic-name}

### Template

<p>Supervisor <a href="${submitter-url}">${submitter-name}</a> chose to supervise your Topic <a href="${topic-url}">${topic-name}</a>.</p>

### Users

* From: Submitter.
* Target:
   * Topic: Creator

### Parameters

| name           | description                                        | 
| -------------- | -------------------------------------------------- |
| submitter-name | Name of the student who is assigned to application |
| submitter-url  | Url of the student who is assigned to application  |
| topic-name     | Title of the Topic                                 |
| topic-url      | Url of the type                                    |

## ApplicationUpdated

Fired when supervisor/techLeader is changed in application

### Title

Your Application ${app-name} was updated

### Template

<p>User <a href="${submitter-url}">${submitter-name}</a> updated your Application <a href="${app-url}">${app-name}</a>.</p>

### Users

* From: Submitter.
* Target: 

### Parameters

| name           | description                                        | 
| -------------- | -------------------------------------------------- |
| submitter-name | Name of the student who is assigned to application |
| submitter-url  | Url of the student who is assigned to application  |
| app-name       | Title of the Application                           |
| app-url        | Url of the Application                             |

## StatusChanged

Fired when status is changed

### Title

Status of your Application ${app-name} was changed.

### Template

<p>User <a href="${submitter-url}">${submitter-name}</a> changed status of your application <a href="${app-url}">${app-name}</a> from ${before} to ${after}.</p>

### Users

* From: Submitter.
* Target: 

### Parameters

| name           | description                                        | 
| -------------- | -------------------------------------------------- |
| submitter-name | Name of the student who is assigned to application |
| submitter-url  | Url of the student who is assigned to application  |
| app-name       | Title of the Application                           |
| app-url        | Url of the Application                             |
| before         | Before Status                                      |
| after          | After Status                                       |
