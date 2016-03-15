
Introduction to springboot
==============
You can have some _Springboot 101_ in 

* [Josh Long on The San Francisco Java User Group](https://www.youtube.com/watch?v=sbPSjI4tt10)


# About the example

## Running

    ./mvnw spring-boot:run
    
Once, everything is up and running, go to 

    http://localhost:8080/

## Testing

    ./mvnw test

## Exploring the exposed api

### Getting data

When you ping the root node, you get back a collection of links wrapped up in a http://stateless.co/hal_specification.html


    $ curl localhost:8080/api
    {
       "_links" : {
         "employees" : {
           "href" : "http://localhost:8080/api/employees"
         },
         "profile" : {
           "href" : "http://localhost:8080/api/profile"
         }
       }
    }

Where,
* *_links* is a the collection of links available.
* *employees* points to an aggregate root for the employee objects defined by the `EmployeeRepository` interface.
* *profile* is an IANA-standard relation and points to discoverable metadata about the entire service. We'll explore this in a later section.


You can further dig into this service by navigating the *employees* link.


    $ curl localhost:8080/api/employees
    {
      "_embedded" : {
        "employees" : [ {
          "firstName" : "Frodo",
          "lastName" : "Baggins",
          "description" : "ring bearer",
          "_links" : {
            "self" : {
              "href" : "http://localhost:8080/api/employees/1"
            }
          }
        } ]
      }
    }


At this stage, you are viewing the entire collection of employees.

What's included along with the data you pre-loaded earlier is a `_links` attribute with a `self` link. This is the canonical 
link for that particular employee. What is canonical? It means free of context. For example, the same user could be fetched 
through a link like /api/orders/1/processor, in which the employee is assocated with processing a particular order. Here, 
there is no relationship to other entities.

IMPORTANT: Links are a critical facet of REST. They provide the power to navigate to related items. It makes it possible 
for other parties to navigate around your API without having to rewrite things everytime there is a change. Updates in 
the client is a common problem when the clients hard code paths to resources. Restructuring resources can cause big 
upheavals in code. If links are used and instead the navigation route is maintained, then it becomes easy and flexible 
to make such adjustments.

You can decide to view that one employee if you wish.


    $ curl localhost:8080/api/employees/1
    {
      "firstName" : "Frodo",
      "lastName" : "Baggins",
      "description" : "ring bearer",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/api/employees/1"
        }
      }
    }


Little change here, except that there is no need for the `_embedded` wrapper since there is only domain object.

### Inserting data

That's all and good, but you are probably itching to create some new entries.


    $ curl -X POST localhost:8080/api/employees -d '{"firstName": "Bilbo", "lastName": "Baggins", "description": "burglar"}' -H 'Content-Type:application/json'
    {
      "firstName" : "Bilbo",
      "lastName" : "Baggins",
      "description" : "burglar",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/api/employees/2"
        }
      }
    }


You can also PUT, PATCH, and DELETE as shown in https://spring.io/guides/gs/accessing-data-rest/[this related guide]. 
But let's not dig into that. You have already spent way too much time interacting with this REST service manually. Don't 
you want to build a slick UI instead?


### Setting up a custom UI controller

Spring Boot makes it super simple to stand up a custom web page. First, you need a Spring MVC controller.

`src/main/java/com/greglturnquist/payroll/HomeController.java`


* `@Controller` marks this class as a Spring MVC controller.
* `@RequestMapping` flags the `index()` method to support the `/` route.
* It returns `index` as the name of the template, which Spring Boot's autoconfigured view resolver will map to `src/main/resources/templates/index.html`.

#### Defining an HTML template

You are using Thymeleaf, although you won't really use many of its features.


The key part in this template is the `<div id="react"></div>` component in the middle. It is where you will direct React to plug in the rendered output.

##### Loading JavaScript modules

This tutorial won't go into extensive detail on how it uses https://webpack.github.io/[webpack] to load JavaScript modules. But thanks to the *frontend-maven-plugin*, you don't _have_ to install any of the node.js tools to build and run the code.

The following JavaScript modules will be used:

* webpack
* babel
* react.js
* rest.js

With the power of babel, the JavaScript is written in ES6.

If you're interested, the paths for the JavaScript moodules are defined in `src/main/resources/static/webpack.config.js`

This is then used by webpack to generate a JavaScript bundle, which is loaded inside the template. 

NOTE: Want to see your JavaScript changes automatically? Move into the `src/main/resource/static`, and run `npm run-script watch` 
to put webpack into watch mode. It will regenerate bundle.js as you edit the source. Assuming you've 
`http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto-hotswapping`, 
*spring-boot-devtools* combined with this should speed up changes.

With all that in place, you can focus on the React bits which are fetched after the DOM is loaded. It's broken down into parts as below:

Since you are using webpack to assemble things, go ahead and fetch the modules you need:

