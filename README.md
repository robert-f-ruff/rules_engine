# Rules Engine
The Rules Engine evaluates a series of rules against a set of data. Each rule has a set of criteria, that defines when a rule is applicable, and a series of actions, that define what the engine should do when the associated rule is applicable. The engine receives data via a REST API, evaluates the criteria against that data, and then executes the appropriate actions.

Users use a web page to define the rules evaluated by the engine. They select one or more pre-defined criterion and action. Each action is customizable by specifying parameters for it. The rule is then stored in a database. Software developers use an administrator console to define each criterion and action.

For additional information, such as the system design, refer to the [project's page on GitHub](https://robert-f-ruff.github.io).

# Purpose
The goal of this project is to demonstrate my knowledge of modern programming languages and system design. Reading a book and completing an online certification gave me a good starting point on my reskilling journey, but creating a system from scratch has locked that knowledge in and exposed me to problems and issues that never came up while completing fill-in-the-blank assignments.

# Development Setup

## Prerequisites
Install the following tools:
- [Python v3.13](https://www.python.org)
- [Java Development Kit (JDK) 21](https://www.oracle.com/java/technologies/downloads/)
- [Apache Maven v3.9.9](https://maven.apache.org/download.cgi)
- Latest version of [Docker Desktop](https://www.docker.com/products/docker-desktop/)

## Gather Secrets
During setup, the initialization script will walk you through populating the text files in the secrets directory. Below is the list of files and each file's purpose. Deciding what each secret should be now will ensure a smooth initialization.
- `secrets/db_host.txt`: This stores the address of the database server; an appropriate default value is populated by the script.
- `secrets/db_host_port.txt`: This stores the port number the database server accepts connetions on.
- `secrets/db_root_password.txt`: This stores the password for the database's root user.
- `secrets/db_user_name.txt`: This stores the username the applications use to access the database server.
- `secrets/db_user_password.txt`: This stores the password for the applications' user account.
- `secrets/django_secret_key.txt`: This stores the key Django uses to secure signed data.
- `secrets/django_super_user_email.txt`: This stores the email address to assign to the super user account.
- `secrets/django_super_user_name.txt`: This stores the username to assign to the super user account.
- `secrets/django_super_user_password.txt`: This stores teh password for the super user account.
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

  ```Shell
  ./build.sh -f backend/engine/docker-compose-service.yml
  ```

- To launch the full system, with the supporting services:

  ```Shell
  ./build.sh -f docker-compose-demonstration.yml
  ```

## Open the Frontend Web Pages

- The Rules Manager page is used to manage the ruleset. Navigate to [http://127.0.0.1:8000/rules/](http://127.0.0.1:8000/rules/).

- The Django Admin page is used to define criteria and actions. Navigate to [http://127.0.0.1:8000/admin/](http://127.0.0.1:8000/admin/).

## Enhancing the Rules Engine

The following sections explain how to add a new criterion and a new action to the Rules Engine.

### Add a New Criterion

1. Select the appropriate class in the logic package:
   - The ObservationLogic class evaluates patient-related measurement data, such as weight.
   - The PatientLogic class evaluates patient-related attribute data, such as age.
   - Create a new class in the logic package that implements the Logic interface:
     - The evaluate method should verify that the requested criterion exists in the registry, cast the data object into the correct class and store a class reference to it, then execute the method reference in the registry, passing it the check (or compare) value, and then return the boolean value returned by the method.
2. Add a private method to evaluate the data:
   - The method should access the class data object reference and compare the checkValue parameter to the appropriate data object attribute.
   - Returns the result of the comparison as a boolean value.
3. Update the class registry: The registry maps class method references to names accessed in the rules_criterion.logic database field.
   - The registry key is the method name, and the value is the method reference.
   - The registry is initialized in the default class constructor.
4. Add an entry to the rules_criterion database table:
   1. Navigate to the [Django Admin](http://127.0.0.1:8000/admin/) page.
   2. Click on the Add button to the right of Criteria under the Rules section in the left-side navigation panel.
      - The name field should describe the type of comparison that will be performed and should contain the type of data that is expected. For example, `Patient is Female`.
      - The logic field stores the logic string and is formatted as:

      ```Java
      Class_Name.method_name=check_value
      ```

      where `Class_Name` is the name of the class in the logic package, `method_name` is the name of the method created in step #2, and `check_value` is the value to compare the data attribute to. For example:

      ```Java
      PatientLogic.ageGreaterThan=40
      ```

   3. Click on the Save button in the Add criterion panel.
5. Update the `frontend/fixtures/actions_parameters.json` file to include the new criterion:

   ```Shell
   python3 frontend/manage.py dumpdata --exclude admin --exclude auth --exclude contenttypes --exclude sessions --exclude messages --exclude staticfiles > ./rules/fixtures/actions_parameters.json
   ```

### Add a New Action

1. Create a new class in the actions package that implements the Action interface:
   - The parameters for the action should be stored in private class references.
2. Add entries to the following database tables:
   1. Navigate to the [Django Admin](http://127.0.0.1:8000/admin/) page.
   2. Click on the Add button to the right of Actions under the Rules section in the left-side navigation section.
      - The function field stores the name of the class created in step #1.
      - The parameter number field stores the display sequence of the parameters in the Rules Manager frontend page.
   3. Click on the plus icon to the right of the Parameter field to add a new parameter.
      - The required field is used by the Rules Manager frontend page to ensure the user enters a value when the action requires this parameter.
      - The help_text field is displayed on the Rules Manager frontend page to explain to the user why the parameter is needed and/or how it is used.
   4. Click on the Save button in the parameter pop-up window.
   5. Click on the Save button in the Change action panel.
3. Update the `frontend/fixtures/actions_parameters.json` file to include the new action and parameters:

   ```Shell
   python3 frontend/manage.py dumpdata --exclude admin --exclude auth --exclude contenttypes --exclude sessions --exclude messages --exclude staticfiles > ./rules/fixtures/actions_parameters.json
   ```
