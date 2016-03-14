[[react-and-spring-data-rest-part-1]]
= Part 1 - Basic Features
:sourcedir: https://github.com/spring-guides/tut-react-and-spring-data-rest/tree/master

Welcome Spring community,

In this section, you will see how to get a bare-bones Spring Data REST application up and running quickly. Then you will build a simple UI on top of it using Facebook's React.js toolset.

== Step 0 - Setting up your environment

Feel free to {sourcedir}/basic[grab the code] from this repository and follow along.

If you want to do it yourself, visit http://start.spring.io and pick these items:

* Rest Repositories
* Thymeleaf
* JPA
* H2
* Lombok (May want to ensure your IDE has support for this as well.)

This demo uses Java 8, Maven Project, and the latest stable release of Spring Boot. It also uses React.js coded in http://es6-features.org/[ES6]. This will give you a clean, empty project. From there, you can add the various files shown explicitly in this section, and/or borrow from the repository listed above.

== In the beginning...

In the beginning there was data. And it was good. But then people wanted to access the data through various means. Over the years, people cobbled together lots of MVC controllers, many using Spring's powerful REST support. But doing over and over cost a lot of time.

Spring Data REST addresses how simple this problem can be if some assumptions are made:

* The developer uses a Spring Data project that supports the repository model.
* The system uses well accepted, industry standard protocols, like HTTP verbs, standardized media types, and IANA-approved link names.

=== Declaring your domain

The cornerstone of any Spring Data REST-based application are the domain objects. For this section, you will build an application to track the employees for a company. Kick that off by creating a data type like this:

.src/main/java/com/greglturnquist/payroll/Employee.java
[source,java]
----
include::src/main/java/com/greglturnquist/payroll/Employee.java[tag=code]
----

* `@Entity` is a JPA annotation that denotes the whole class for storage in a relational table.
* `@Id` and `@GeneratedValue` are JPA annotations to note the primary key and that is generated automatically when needed.
* `@Data` is a Project Lombok annotation to autogenerate getters, setters, constructors, toString, hash, equals, and other things. It cuts down on the boilerplate.

This entity is used to track employee information. In this case, their name and job description.

NOTE: Spring Data REST isn't confined to JPA. It supports many NoSQL data stores, but you won't be covering those here.

== Defining the repository

Another key piece of a Spring Data REST application is to create a corresponding repository definition.

.src/main/java/com/greglturnquist/payroll/EmployeeRepository.java
[source,java]
----
include::src/main/java/com/greglturnquist/payroll/EmployeeRepository.java[tag=code]
----

* The repository extends Spring Data Commons' `CrudRepository` and plugs in the type of the domain object and its primary key

That is all that is needed! In fact, you don't even have to annotate this if it's top-level and visible. If you use your IDE and open up `CrudRepository`, you'll find a fist full of pre-built methods already defined.

NOTE: You can define http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.definition[your own repository] if you wish. Spring Data REST supports that as well.

== Pre-loading the demo

To work with this application, you need to pre-load it with some data like this:

.src/main/java/com/greglturnquist/payroll/DatabaseLoader.java
[source,java]
----
include::src/main/java/com/greglturnquist/payroll/DatabaseLoader.java[tag=code]
----

* This class is marked with Spring's `@Component` annotation so that it is automatically picked up by `@SpringBootApplication`.
* It implements Spring Boot's `CommandLineRunner` so that it gets run after all the beans are created and registered.
* It uses constructor injection and autowiring to get Spring Data's automatically created `EmployeeRepository`.
* The `run()` method is invoked with command line arguments, loading up your data.

One of the biggest, most powerful features of Spring Data is its ability to write JPA queries for you. This not only cuts down on your development time, but also reduces the risk of bugs and errors. Spring Data http://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.details[looks at the name of methods] in a repository class and figures out the operation you need including saving, deleting, and finding.

That is how we can write an empty interface and inherit already built save, find, and delete operations.

== Adjusting the root URI

By default, Spring Data REST hosts a root collection of links at `/`. Because you will host a web UI on the same path, you need to change the root URI.

.src/main/resources/application.properties
----
include::src/main/resources/application.properties[]
----

== Launching the backend

The last step needed to get a fully operational REST API off the ground is to write a `public static void main` using Spring Boot:

.src/main/java/com/greglturnquist/payroll/ReactAndSpringDataRestApplication.java
[source,java]
----
include::src/main/java/com/greglturnquist/payroll/ReactAndSpringDataRestApplication.java[tag=code]
----

Assuming the previous class as well as your Maven build file were generated from http://start.spring.io, you can now launch it either by running that `main()` method inside your IDE, or type `./mvnw spring-boot:run` on the command line. (mvnw.bat for Windows users).

NOTE: If you aren't up-to-date on Spring Boot and how it works, you should consider watch one of https://www.youtube.com/watch?v=sbPSjI4tt10[Josh Long's introductory presentations]. Did it? Press on!

== Touring your REST service

With the app running, you can check things out on the command line using http://curl.haxx.se/[cURL] (or any other tool you like).

----
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
----

When you ping the root node, you get back a collection of links wrapped up in a http://stateless.co/hal_specification.html[HAL-formatted JSON document].

* *_links* is a the collection of links available.
* *employees* points to an aggregate root for the employee objects defined by the `EmployeeRepository` interface.
* *profile* is an IANA-standard relation and points to discoverable metadata about the entire service. We'll explore this in a later section.

You can further dig into this service by navigating the *employees* link.

----
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
----

At this stage, you are viewing the entire collection of employees.

What's included along with the data you pre-loaded earlier is a *_links* attribute with a *self* link. This is the canonical link for that particular employee. What is canonical? It means free of context. For example, the same user could be fetched through a link like /api/orders/1/processor, in which the employee is assocated with processing a particular order. Here, there is no relationship to other entities.

IMPORTANT: Links are a critical facet of REST. They provide the power to navigate to related items. It makes it possible for other parties to navigate around your API without having to rewrite things everytime there is a change. Updates in the client is a common problem when the clients hard code paths to resources. Restructuring resources can cause big upheavals in code. If links are used and instead the navigation route is maintained, then it becomes easy and flexible to make such adjustments.

You can decide to view that one employee if you wish.

----
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
----

Little change here, except that there is no need for the *_embedded* wrapper since there is only domain object.

That's all and good, but you are probably itching to create some new entries.

----
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
----

You can also PUT, PATCH, and DELETE as shown in https://spring.io/guides/gs/accessing-data-rest/[this related guide]. But let's not dig into that. You have already spent way too much time interacting with this REST service manually. Don't you want to build a slick UI instead?

== Setting up a custom UI controller

Spring Boot makes it super simple to stand up a custom web page. First, you need a Spring MVC controller.

.src/main/java/com/greglturnquist/payroll/HomeController.java
[source,java]
----
include::src/main/java/com/greglturnquist/payroll/HomeController.java[tag=code]
----

* `@Controller` marks this class as a Spring MVC controller.
* `@RequestMapping` flags the `index()` method to support the `/` route.
* It returns `index` as the name of the template, which Spring Boot's autoconfigured view resolver will map to `src/main/resources/templates/index.html`.

== Defining an HTML template

You are using Thymeleaf, although you won't really use many of its features.

.src/main/resources/templates/index.html
[source,html]
----
include::src/main/resources/templates/index.html[]
----

The key part in this template is the `<div id="react"></div>` component in the middle. It is where you will direct React to plug in the rendered output.

== Loading JavaScript modules

This tutorial won't go into extensive detail on how it uses https://webpack.github.io/[webpack] to load JavaScript modules. But thanks to the *frontend-maven-plugin*, you don't _have_ to install any of the node.js tools to build and run the code.

The following JavaScript modules will be used:

* webpack
* babel
* react.js
* rest.js

With the power of babel, the JavaScript is written in ES6.

If you're interested, the paths for the JavaScript moodules are defined in https://github.com/spring-guides/tut-react-and-spring-data-rest/blob/master/basic/src/main/resources/static/webpack.config.js[webpack.config.js]. This is then used by webpack to generate a JavaScript bundle, which is loaded inside the template. 

NOTE: Want to see your JavaScript changes automatically? Move into the `src/main/resource/static`, and run `npm run-script watch` to put webpack into watch mode. It will regenerate bundle.js as you edit the source. Assuming you've http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto-hotswapping[setup your IDE properly], *spring-boot-devtools* combined with this should speed up changes.

With all that in place, you can focus on the React bits which are fetched after the DOM is loaded. It's broken down into parts as below:

Since you are using webpack to assemble things, go ahead and fetch the modules you need:

