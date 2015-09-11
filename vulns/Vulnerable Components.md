## Using Components with Known Vulnerabilities

#### Description

Components with Known Vulnerabilities are libraries that contain published or publically known exploits. In general, it can be assumed that outdated libraries are significantly more likely to have known security issues that can be used to exploit your application. Issues with outdated components are often difficult to track down, and can be difficult to fix until the original developer releases an update.

As developers create more complex applications, they are relying more heavily on 3rd party code in the form of application libraries. Yet, they often pay little attention to the internals or implementation of such libraries. Worse, dependencies often have other dependencies themselves, so it is difficult to even know all the libraries you are using! Keeping a focus on updates for 3rd party code is crucial in preventing your application from being suddenly exploitable.

#### Walkthrough

1. Open up the ```build.gradle``` file in the main directory of the application.
2. Notice that the dependencies section contains version numbers for some key components
3. Using a CVE search website, such as [CVE Details](http://www.cvedetails.com/), [National Vulnerability Database](https://nvd.nist.gov) or [MITRE's CVE Search](http://www.cve.mitre.org/find/index.html), search for the versions used in the MoneyX application
4. Two recent vulnerabilities in Spring and Spring Boot include [CVE-2015-3192](https://nvd.nist.gov/nvd.cfm?cvename=CVE-2015-3192) and [CVE-2014-3578](http://www.cvedetails.com/cve/CVE-2014-3578/). Since the components in MoneyX are vulnerable, both could be used to attack the application without having any vulnerabilies in MoneyX code itself

#### Problem
URL: None

For MoneyX, dependencies are specified in the ```build.gradle``` file, used by the Gradle build system. The developer has used versions of each dependency that were up to date at the time of release. However, since the app was developed, [new published security issues](https://pivotal.io/security/cve-2015-3192) have been released to make them out of date.

#### Code Snippet
build.gradle

```
// ...

dependencies {
    compile("org.springframework.boot:spring-boot-starter-data-jpa")
    compile("org.hibernate:hibernate-core:4.3.10.Final")
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("org.springframework.security:spring-security-taglibs:4.0.2.RELEASE")
    compile("org.springframework.boot:spring-boot-starter-security")
    compile("org.springframework.session:spring-session-data-redis:1.0.1.RELEASE")
    compile("javax.servlet:jstl:1.2")
    compile("com.h2database:h2")
    runtime("mysql:mysql-connector-java")
    testCompile("org.springframework.boot:spring-boot-starter-test")
    runtime("org.apache.tomcat.embed:tomcat-embed-jasper")
}

// ...

```

#### Solution

Keep track of dependencies or libraries used in the system and ensure they are up to date. While Gradle doesn't native feature this functionality, it is possible to use a third party plugin like the [gradle-versions-plugin](https://github.com/ben-manes/gradle-versions-plugin) to do so. Avoid using dependencies that are not maintained or not widely used; these will be more likely to cause issues down the line.
