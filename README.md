# Productivife

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Productivife is an app that will allow its users to organize their daily activities into smaller tasks displayed as to do items planning at the same time when and where they are planning to do them, giving them a better overview of how their week is looking and plan ahead.

### App Evaluation
- **Category:** Productivity
- **Mobile:** Android mobile. The app is suited for mobile use since it gives users easy access to see their daily tasks
- **Story:** Every project, specially an internship, can be better planned by dividing big tasks into smaller ones, as well as keeping track of them and have them on your mind - with this app interns and students can keep themselves on the loop of what they have to do, when do they have to do it and where should they go.
- **Market:** Interns and students
- **Habit:** The users would open this app every day to check their tasks for the day as well as continuously add whatever new chore they have to do.
- **Scope:** The app would have the core feature of being able to see your to-do items prioritized by due-date or priority, add new items to the list of things you need to get done, see the list displayed on a calendar.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* The app will interact with a database in Firebase where the to do items will be stored, as well as the users
* The app will interact with the Google Maps API to display the places where the to do items will take place
* The user will be able to log in/log out of the app (Login System)
* The user can sign up with a new user profile
* The app has multiple views
  * Home: where users can create new items, see their planned items, filter them. (An activity with each own layout will be created for each of this, as well as the fragment where the main “to do” menu will be displayed)
  * Calendar tab: where users will see their items in a calendar format
* The app will use the double tap gesture on a to do item to mark it as a completed task.
* The app will incorporate the Bootstrap library as well as some Lottie’s animations to add visual polish
* The app will use fade in/fade out animations
* Allow users to sign in through facebook
* The app will use a filtering algorithm to allow users to see the to-do items within priority, dates/time or a combination filter of priority+due date. As well as sorting the items by due dates.
* The app will include an animation (built from scratch) when the to do item is erased


**Optional Nice-to-have Stories**
* Sync the app's calendar with your google calendar
* Add email verification for new users
* Add a map view
* Allow users to have a description for their to-do items where photos can be added

### 2. Screen Archetypes

* Landing page
* Login page
   * The app will interact with a database in Firebase where the to do items will be stored, as well as the users
   * The users will be able to log in/log out of the app (Login system)
   * Allow users to sign in through facebook
* Sign up page
   * The app will interact with a database in Firebase and FirebaseAuth
   * The user can sign up with a new user profile
   * Add email verification for new users
 * Home
 * To-Do list
    * The app will interact with a database in Firebase where the to do items will be stored, as well as the users
    * The app will use the double tap gesture on a to do item to mark it as a completed task.
    * The app will use a filtering algorithm to allow users to see a certain cathegory or combination of cathegories of items. (For example only show high priority tasks due on that week) As well as sorting the items firstly by due date and then by priority
  * To-Do creation
    * The app will interact with a database in Firebase where the to do items will be stored, as well as the users
    * Allow users to have a description for their to-do items where photos can be added (Optional)
  * Calendar view
    * The app will interact with a database in Firebase where the to-do items will be stored, as well as the users
    * Syn the app's calendar with your google calendar (Optional)

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home
* Calendar

**Flow Navigation** (Screen to Screen)

* Home
   * See the to-do list (filtered or unfiltered)
   * See detailed view of a certain item
   * Create new item
   * Update an item
* Calendar tab
   * Display the title of the to do items on the calendar view

## Wireframes


### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype
https://www.canva.com/design/DAFELdQBRdM/9lQtuo48ZStk30Dh3FbNSA/view?mode=prototype 

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
