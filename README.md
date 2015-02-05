This is a demo play web application for SCRUD funtionalities
I. The user story
This is a demo application for writing a basic business funtions (big use cases) as following:
1. Create an user 
2. Update an user 
3. Delete an user
4. Read users information
5. Searching for users
The user story will follow the INVEST nmemonic.

II. The technical usage are included as followings:
1. Scala v2.11.4 language for development
2. Redis with newest version together with a Redis driver. 
Redis is not a RDBMS so it requires a special design (for Key-Value DB).
please see design DB and other application design document inside <app-root>/docs/design folder.
3. Behavior Driven Development, from user story to behavior check and then complete application.
4. Reactive bahavior for both backend and front-end, non-blocking way
5. Front-end is web-based developed by HTML, Coffee and Knockout
6. Based on Play framework 2.1, BST 0.13 and Typesafe activator template.

III. Role definition
1. Normal user (including guest)
2. Administrator
for the first step of the application we should care for normal user

IV. Actions for Role
- For normal user (here in after will be called User), when access to web front-end, can create another user.
- User can update another user information by loading them from the list.
- User can delete another user from the list
- User can search another users information by providing some search condition such as name and/or age
- User can read another users information.
In the future time, user can only modify its information. Adminitrator can do asll as described above.

V. BDD - Behavior checking classes
All behavior checking classes located in default package of test source folder.
1. BaseAcceptanceSpec.scala: Base class for AcceptanceSpec BDD Style
2. BaseUnitSpec.scala: Base class for FlatSpec BDD Style (Unit)
3. CreatingUserFeatureSpec.scala: check behavior of creting user
4. DeletingUserFeatureSpec.scala: check behavior of deleting user
5. GettingUserFeatureSpec.scala: check behavior of getting an user
6. IntegrationSpec.scala: Play automatic generated BDD class
7. ListingUserFeatureSpec.scala: check behavior of listing all user
8. SearchingUserFeatureSpec.scala: check behavior of searching user
9. UpdatingUserFeatureSpec.scala: check behavior of updating user
For easy testing, each file should be tested separately.

VI. Compile, test and run and configure
VI.1. Compile,test and run
As a Typesafe activator web application, you can start application by running 'activator' command as <app-root-folder>
or combine 'activator' command with some option to start the task immediately, here in after are some ways:
Firstly, you change to project folder, apply to all below:
cd <app-root-folder>
1. Do the following command in a terminal (each command in one line, terminated by <enter> key
cd <app-root-folder>
./activator 
compile
test
run
2. ./activator run 
or you can combine as many as you can:
./activator compile test run
or 
./activator -jvm-debug 9999 
reload 
compile 
test 
~run
3. For supporting eclipse project you could do:
./activator 
eclipse with-source=true
After that you can import eclipse project from project folder with new .project and .classpath files

After running the application, you can access the main page by URL:
http://localhost:9000

NOTICE: All of above are supposed that you are working in Linux environment.
VI.2. Configure 
Because this project is based on SBT so most configuration should be found at <project-root-folder>/build.sbt
For route definition (please see more on playframework formal website) please see in file <project-root-folder>/conf/routes
For play plugin, please see in file <project-root-folder>/conf/play.plugin

For more detail please contact: nghia.n.v2007@gmail.com








