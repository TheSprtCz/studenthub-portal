# StudentHub

## CI

[![Build Status](https://travis-ci.org/StudentHubCZ/studenthub-portal.svg?branch=master)](https://travis-ci.org/StudentHubCZ/studenthub-portal)

## How to start the StudentHub application

1. Run `mvn clean package` to build your application
1. Populate DB schema with `java -jar studenthub-portal/target/studenthub-portal-1.0-SNAPSHOT.jar db migrate config.yml`
1. Seed DB with admin user `java -jar studenthub-portal/target/studenthub-portal-1.0-SNAPSHOT.jar db migrate config.yml --migrations studenthub-portal/seed.sql`
1. (Optional) configure SMTP client (see instructions below)
1. Start application with `java -jar studenthub-portal/target/studenthub-portal-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

### SMTP Configuration

Student Hub Portal uses SMTP protocol for sending email notifications. You can configure it via the following env variables:

| ENV VAR         | PURPOSE        | EXAMPLE             |
| --------------- |----------------| --------------------|
| SMTP_FROM_EMAIL | Sender email   | admin@example.com   |
| SMTP_FROM_NAME  | Sender name    | Student Hub Admin   |
| SMTP_SERVER     | Server address | smtp.gmail.com      |
| SMTP_PORT       | Server port    | 587                 |
| SMTP_USERNAME   | Username       | -                   |
| SMTP_PASSWORD   | Password       | -                   |


## Health Check

To see your applications health enter url `http://localhost:8081/healthcheck`

## How to contribute

* Write to us at admin@studenthub.cz
* Create a GitHub [issue](https://github.com/StudentHubCZ/studenthub-portal/issues/new)
* Send a [Pull-Request](https://github.com/StudentHubCZ/studenthub-portal/compare)

## License

This project is licensed under the terms of the GNU General Public License v 3.0. For more information see [LICENSE](https://github.com/StudentHubCZ/studenthub-portal/blob/master/LICENSE).
