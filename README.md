# clojars scraper

A simple script that parses pom files as JSON and inserts them into MongoDB

## Requirements

* A MongoDB installation (By default it listens on localhost and no password is required)
* Download all the POM files with `rsync -av --delete clojars.org::clojars my-wonderful-copy-of-clojars --include="*/" --include="*.pom" --exclude="*"`. The script assumes this directory is present at `/home/ubuntu/` . It downloads around 1GB of data.
* Latest clojure deps tool. Refer https://clojure.org/guides/getting_started

## Installation

* Clone the repo
* Run `clj -m scraper`
* Use can use MongoDB shell to query data. Sample structure as below :

```
➜  ~ mongo
> use lein
switched to db lein
> db.clojars.findOne()
{
	"_id" : ObjectId("5aab80f424a4d605180c99fb"),
	"modelVersion" : "4.0.0",
	"groupId" : "audiogum",
	"artifactId" : "clj-lazy-json",
	"packaging" : "jar",
	"version" : "0.0.3",
	"name" : "clj-lazy-json",
	"description" : "Jackson-based lazy JSON parsing library for Clojure.",
	"scm" : {
		"tag" : "url",
		"attrs" : null,
		"content" : [
			"https://github.com/audiogum-forks/clj-lazy-json"
		]
	},
	"dependencies" : [
		{
			"groupId" : "org.clojure",
			"artifactId" : "clojure",
			"version" : "1.9.0-RC2"
		},
		{
			"groupId" : "org.codehaus.jackson",
			"artifactId" : "jackson-core-asl",
			"version" : "1.8.6"
		}
	]
}
```
