---
sidebar_position: 1
title: Getting started
---

[//]: # (Copyright 2024 Paion Data)

[//]: # (Licensed under the Apache License, Version 2.0 &#40;the "License"&#41;;)
[//]: # (you may not use this file except in compliance with the License.)
[//]: # (You may obtain a copy of the License at)

[//]: # (    http://www.apache.org/licenses/LICENSE-2.0)

[//]: # (Unless required by applicable law or agreed to in writing, software)
[//]: # (distributed under the License is distributed on an "AS IS" BASIS,)
[//]: # (WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.)
[//]: # (See the License for the specific language governing permissions and)
[//]: # (limitations under the License.)

So You Want An API?
-------------------

:::info Please make sure JDK 17, Maven, and Docker Engine have been installed 🤗

- We offer [instruction](setup#installing-java--maven-on-mac) on how to install JDK 17 and Maven
- We also offer [links to Docker Engine installation](setup#installing-docker-engine)

:::

The easiest way to start with Aristotle is using its Docker:

```bash
git clone git@github.com:paion-data/aristotle.git
cd aristotle
mvn clean package
docker compose up --build --force-recreate
```

We can then access the API documentation at `http://localhost:8080/doc.html` on our local server.
