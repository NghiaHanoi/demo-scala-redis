I. The user story (follow the INVEST nmemonic)
This is a demo application for writing a basic business funtions (big use cases) as following:
1. Create an user 
2. Update an user 
3. Delete an user
4. Read users information
5. Searching for users
The user story will follow the INVEST nmemonic.

II. The technical usage are included as followings:
1. Scala v2.11.4 language for development
2. Redis with newest version together with a Redis driver. This is not a RDBMS so it requires a special design (for Key-Value DB).
3. Behavior Driven Development.
4. Reactive bahavior for both backend and front-end, non-blocking way
5. Front-end is web-based developed by HTML, Coffee and Knockout

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


