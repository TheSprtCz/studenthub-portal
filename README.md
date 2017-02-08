# StudentHub

CI
---
[![Build Status](https://travis-ci.org/StudentHubCZ/studenthub-portal.svg?branch=master)](https://travis-ci.org/StudentHubCZ/studenthub-portal)

How to start the StudentHub application
---

1. Run `mvn clean package` to build your application
1. Populate DB schema with `java -jar target/portal-1.0-SNAPSHOT.jar db migrate config.yml`
1. Start application with `java -jar target/portal-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`

How to contribute
---

* Write to us at info@studenthub.cz
* Create a GitHub [issue](https://github.com/StudentHubCZ/studenthub-portal/issues/new)
* Send a [Pull-Request](https://github.com/StudentHubCZ/studenthub-portal/compare)

License
---
This project is licensed under the terms of the GNU General Public License v 3.0. For more information see [LICENSE](https://github.com/StudentHubCZ/studenthub-portal/blob/master/LICENSE).
