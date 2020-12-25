# Git Gud
 
A CLI application that allows pulling various information from GitHub REST API.

---

## Available Commands
- commit-info   - Get info about one or more commits.
- search-users  - Search for GitHub users with different criteria.
- stargazers    - Get users starring a repository.
- starred-by    - Get repositories starred by a user.
- unstar-repo   - Unstar a repository (auth token is required in .properties file).
- star-repo     - Star a repository. (auth token is required in .properties file).
- branches      - Get branches available in a repository.
- search-repos  - Search for GitHub repositories with different criteria.

#### Examples
```
commit-info -v -o pawel-szopinski -r implementers-toolbox e00b9d96964d110e09fdd8816c8b5ce0efc6b40e
search-users -u m -l java -l python -L germany -r ">10"
stargazers -o graphql -r graphql.github.io
starred-by -u pawel-szopinski -s
unstar-repo -o graphql -r graphql.github.io
star-repo -o graphql -r graphql.github.io
branches -s -r budgie-extras -o UbuntuBudgie
search-repos -p snake -l java -l python -c ">2020-03-20" -a
```
Tip: use command name with ```-h``` parameter to discover all available options.

---

## Building and running the application

The app requires Git, Apache Maven and JDK **11** to build and run.

```
# Clone the repository with Git
$ git clone https://github.com/pawel-szopinski/git-gud
   
# Go into the repository
$ cd git-gud
   
# Build JAR file with Maven 
$ mvn package

# Go into the directory with JAR file
$ cd target
   
# Run the app
$ java -jar git-gud-1.0.jar
```
#### Important
- If you plan on moving the application to a different folder and running it from there, please make sure to also copy ```git-gud.properties``` file. Without it the application won't run.
- Also, regarding the ```git-gud.properties``` file - some commands require auth token e.g. ```star-repo```, which can be added there. If you exceed GitHub's request limit, adding the auth token will allow you to continue executing more commands.
- If you plan on running the application from an IDE of choice, make sure to execute ```mvn compile``` beforehand. It will copy the ```git-gud.properties``` file to ```target``` folder.

---

## The app was built with
- [picocli](https://github.com/remkop/picocli) - Command line interface
- [Maven](https://maven.apache.org/) - Dependency Management
- [json-io](https://github.com/jdereg/json-io) - JSON formatting for verbose output
- [gson](https://github.com/google/gson) - JSON deserialization
- JDK 11's new HTTP client - processing HTTP requests

---
