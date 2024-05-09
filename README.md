# Rules Engine
The Rules Engine evaluates a series of rules against a set of data. Each rule has a set of criteria, that defines when a rule is applicable, and a series of actions, that define what the engine should do when the associated rule is applicable. The engine receives data via a REST API, evaluates the criteria against that data, and then executes the appropriate actions.

Users use a web page to define the rules evaluated by the engine. They select one or more pre-defined criterion and action. Each action is customizable by specifying parameters for it. The rule is then stored in a database. Software developers use an administrator console to define each criterion and action.

For additional information, such as the system design, refer to the [project's page on GitHub](https://robert-f-ruff.github.io).

# Purpose
The goal of this project is to demonstrate my knowledge of modern programming languages and system design. Reading a book and completing an online certification gave me a good starting point on my reskilling journey, but creating a system from scratch has locked that knowledge in and exposed me to problems and issues that never came up while completing fill-in-the-blank assignments.

# Development Setup

## Prerequisites
Install the following tools:
- [Python v3.12](https://www.python.org)
- [Java Development Kit (JDK) 21](https://www.oracle.com/java/technologies/downloads/)
- [Apache Maven v3.9.6](https://maven.apache.org/download.cgi)
- Latest version of [Docker Desktop](https://www.docker.com/products/docker-desktop/)

### WildFly Configure v1.0.0
The WildFly Configure utility is required to ensure that the backend engine container successfully starts.

1. Download the utility's source code from the utility's [GitHub repository](https://github.com/robert-f-ruff/wildfly_configure).
2. Follow the instructions in the [Development Setup](https://github.com/robert-f-ruff/wildfly_configure#development-setup) section in the repository's README.md file to build and distribute the utility.

## Gather Secrets
During setup, the initialization script will walk you through populating the text files in the secrets directory. Below is the list of files and each file's purpose. Deciding what each secret should be now will ensure a smooth initialization.
- `secrets/db_host.txt`: This stores the address of the database server; an appropriate default value is populated by the script.
- `secrets/db_host_port.txt`: This stores the port number the database server accepts connetions on.
- `secrets/db_root_password.txt`: This stores the password for the database's root user.
- `secrets/db_user_name.txt`: This stores the username the applications use to access the database server.
- `secrets/db_user_password.txt`: This stores the password for the applications' user account.
- `secrets/django_secret_key.txt`: This stores the key Django uses to secure signed data.
- `secrets/engine_host.txt`: This stores the address to use in connecting to the backend Java application; an appropriate default value is populated by the script.
- `secrets/engine_reload_key.txt`: This stores the key the backend Java application uses to verify reload requests.
- `secrets/mail_from_address.txt`: This stores the email address to use in connecting to the email service (should be fake).
- `secrets/mail_host_port.txt`: This stores the port number on the email server that the Simple Mail Transport Protocol (SMTP) service listens on; an appropriate default value is populated by the script.
- `secrets/mail_host.txt`: This stores the address to use in connecting to the email server; an appropriate default value is populated by the script.
- `secrets/mail_server_password.txt`: This stores the password for the email account used in connecting to the email service.

## Setup Environment
A Docker Compose file (`docker-compose-supporting_services.yml`) is provided that will launch the supporting services:
- Database: [MySQL](https://dev.mysql.com/)
- Email: [GreenMail](https://greenmail-mail-test.github.io/greenmail/#)

> [!IMPORTANT]
> Docker Desktop must be running **before** proceeding with setup.

1. Install the MySQL Client for Python:
   - On an Apple system:
     1. Install the package using Homebrew:

     ```Shell
     brew install mysql-client pkg-config
     ```

     2. Review the Homebrew output. You should see a line starting with `For pkg-config to find mysql-client you may need to set:`. Execute the `export` command that appears on the following line.
   - On a Linux system:

     ```Shell
     apt-get install -y python3-dev default-libmysqlclient-dev build-essential pkg-config
     ```

2. Initialize the development environment by executing the following script:

   ```Shell
   ./initialize_environment.sh
   ```
3. Obtain and configure access to the Red Hat Quay Container Registry:
   1. If you do not have a Red Hat account, sign up for a free account by clicking on the SIGN IN button on the [registry's homepage](https://quay.io/tutorial/).
   2. The [tutorial](https://quay.io/tutorial/) contains instructions on adding the registry to Docker.

## Launch the Application Containers
Before launching the system, both in full or in part, verify the contents of the `secrets/db_host.txt`, `secrets/engine_host.txt`, and `secrets/mail_host.txt` files are correct according to the following tables. 
<table>
   <caption>Values for the db_host.txt File</caption>
   <thead>
      <tr>
         <th scope="col">Component</th>
         <th scope="col">Environment</th>
         <th scope="col">Value</th>
      </tr>
   </thead>
   <tbody>
      <tr>
         <th scope="row" rowspan="3">Frontend</th>
         <td>Local Desktop</td>
         <td>127.0.0.1</td>
      </tr>
      <tr>
         <td>Testing</td>
         <td>127.0.0.1</td>
      </tr>
      <tr>
         <td>Docker Container</td>
         <td>db</td>
      </tr>
      <tr>
         <th scope="row" rowspan="2">Backend</th>
         <td>Testing</td>
         <td>db_test</td>
      </tr>
      <tr>
         <td>Docker Container</td>
         <td>db</td>
      </tr>
      <tr>
         <th scope="row">Full System</th>
         <td>Docker Container</td>
         <td>db</td>
      </tr>
   </tbody>
</table>

<table>
   <caption>Values for the engine_host.txt File</caption>
   <thead>
      <tr>
         <th scope="col">Component</th>
         <th scope="col">Environment</th>
         <th scope="col">Value</th>
      </tr>
   </thead>
   <tbody>
      <tr>
         <th scope="row" rowspan="2">Frontend</th>
         <td>Local Desktop</td>
         <td>127.0.0.1:8080</td>
      </tr>
      <tr>
         <td>Docker Container</td>
         <td>engine_service:8080</td>
      </tr>
      <tr>
         <th scope="row">Full System</th>
         <td>Docker Container</td>
         <td>engine_service:8080</td>
      </tr>
   </tbody>
</table>

<table>
   <caption>Values for the mail_host.txt File</caption>
   <thead>
      <tr>
         <th scope="col">Component</th>
         <th scope="col">Environment</th>
         <th scope="col">Value</th>
      </tr>
   </thead>
   <tbody>
      <tr>
         <th scope="row" rowspan="2">Backend</th>
         <td>Testing</td>
         <td>email_test</td>
      </tr>
      <tr>
         <td>Docker Container</td>
         <td>email</td>
      </tr>
      <tr>
         <th scope="row">Full System</th>
         <td>Docker Container</td>
         <td>email</td>
      </tr>
   </tbody>
</table>


- To launch the supporting services:

   ```Shell
   docker compose -f docker-compose-supporting_services.yml up --build --detach
   ```

- To launch both the Python frontend and the Java backend:

  ```Shell
  ./build.sh -f docker-compose-deployment.yml
  ```

- To launch just the Python frontend:

  ```Shell
  ./build.sh -f frontend/docker-compose-service.yml
  ```

- To launch just the Java backend:
  > :warning: If you have not launched the Python frontend at least once before launching the Java backend, execute the following commands **before** launching the backend:
  > 
  > ```Shell
  > python3 frontend/manage.py migrate
  > python3 frontend/manage.py loaddata actions_parameters
  > ```

  ```Shell
  docker compose -f backend/engine/docker-compose-service.yml up --build --detach
  ```

- To launch the full system, with the supporting services:

  ```Shell
  ./build.sh -f docker-compose-demonstration.yml
  ```
