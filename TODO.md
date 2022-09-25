#TODOLIST

###MUST-DOS

* [x] Create test for QuestionDao - listAll.
* [X] Create main method for running the server
* [ ] Make datasource read or use default
* [X] Read question from input
* [X] Listing of questions must include drop-down menu with options
* [X] Listing all answers to a question
* [X] Option to change text and title of question.
* [X] When adding a option, it needs to get the questionID from the relevant question. using "option value=?" ?
###SEMI-ADVANCED FUNCTIONS
* [X] abstract dao
* [X] Functionality to answer a question
* [X] Adding a new questionnaire
* [X] backend input validation
* [X] Complete list single answer
* [X] Add survey as an element of the solution. Change questionclass/dao/controller to work with  surveyID
* [ ] Complete readme, add uml, build jar, add maven actions, badge, 
* IF TIME:
#### EXTRAS
* [X] Delete question from survey with corresponding values in child tables
* [X] Make filetarget "/" lead to "index.html"
* [X] Make the server accept nordic / special characters (list answers)
* [X] More than 3 tables
* [ ] Registering a user before answering questions. Using cookie headerfields?
* [ ] UML diagram in readme
* [ ] adding range as option

###Possible new tests[ ] 

#WAY FORWARD
* [ ] Nordiske karakterer i testene. - readbody http message. Gjelder ShouldCreateNewQuestion
* [ ] Duplicate option texts?
* [ ] ErrorHandling i handleClients()
* [ ] AbstractDAO??? Stor oppgave!!!
* [X] Se på update question. Should handle only text change.
