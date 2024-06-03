+++
title = "My Approach to Domain Specific Language"
lastmod = 2024-06-02T13:35:07-07:00
tags = ["kotlin", "groovy", "dsl", "functional", "programming"]
draft = false
comments = true
description = "A exploration of how DSLs can enable positive DevX."
+++

Building domain specific languages has been a way for me to enable better developer experiences in the last couple projects I lead. Having built them in both Groovy and Kotlin, I'd like to compare and contrast the differences, and provide an approach that might be useful if you are exploring this for your project. My approach is to use functional programming to accomplish the goals.


## Kotlin Example {#kotlin-example}

In this example, I'll look at how to provide a simple DSL around setting up an apache spark session.


### Spark session wrapper {#spark-session-wrapper}

First we'll setup a data class to hold our configuration, in a more real world example this configuration should come from environment variables.

```kotlin
data class Configuration(
    val appName: String = "myAppName",
    val postgresJdbcUrl: String = "some jdbc url",
    val postgresUser: String = "some postgres user",
    val postgresPassword: String = "some postgres password",
    val masterUrl: String = "local[*]"
)
```

The spark wrapper class is a do nothing class that is essentially a placeholder for functionality we'll apply using Kotlin's functional extensions. As you'll see later, this class having the configuration and spark session enables access to that state from the downstream functions.

```kotlin
class SparkWrapper(
    val spark: SparkSession,
    val configuration: Configuration,
)
```

This is where things start to get interesting. This is saying `withSpark` takes a configuration, and a lambda. The lambda is expected to be a functional extension of `SparkWrapper`. We're creating a spark session, then instantiating the spark wrapper inside the context of the spark session. Finally we run the lambda and then end the spark session.

```kotlin
fun withSpark(
    configuration: Configuration,
    func: SparkWrapper.() -> Unit,
) = SparkSession
        .builder()
        .master(configuration.masterUrl)
        .appName(configuration.appName)
        .getOrCreate()
        .apply {
            SparkWrapper(this, configuration).apply {
                func()
                spark.stop()
            }
        }
```


### Spark wrapper extension functions {#spark-wrapper-extension-functions}

These extension functions take advantage of the fact that the spark wrapper is already initialized with the configuration and spark session already. In practice, Intellij will understand these contexts and give the developer authoring this code auto completions, documentation, and click through to definition. The `withFileCache` functionality is an example of using spark in a prototyping setting, I've use this type of pattern extensively in Kotlin Notebooks. `withFileCache` could also be extended to provide s3 support, by adding configuration and accessing that configuration in this function. As you can see, the spark session doesn't need to be passed into this function because it already has it in the context of the spark wrapper in which it will be executed.

```kotlin
fun SparkWrapper.withFileCache(
    path: String,
    fileType: String = "parquet",
    func: () -> Dataset<Row>
): Dataset<Row> =
    when (fileType) {
        "parquet" -> return if (File("cache/$path").exists()) {
            spark.read().parquet("cache/$path")
        } else {
            val dataset = func()
            dataset.write().parquet("cache/$path")
            dataset
        }
        "json" -> return if (File("cache/$path").exists()) {
            spark.read().json("cache/$path")
        } else {
            val dataset = func()
            dataset.write().json("cache/$path")
            dataset
        }
        else -> throw RuntimeException("Not Implemented")
    }
```

Another practical example of enabling a common spark feature in a DSL fluent way.

```kotlin
fun SparkWrapper.postgresQuery(
    sourceQuery: String,
): Dataset<Row> =
    spark.read()
        .format("jdbc")
        .option("url", configuration.postgresJdbcUrl)
        .option("driver", "org.postgresql.Driver")
        .option("query", sourceQuery)
        .option("user", configuration.postgresUser)
        .option("password", configuration.postgresPassword)
        .load()
```


### Putting it all together {#putting-it-all-together}

Here's the payoff for the work we've done above. The actual spark job that's being written can focus on the business logic. Using functional extensions can provide functionality like common transformations that might want to be implemented across multiple spark jobs. I've used this pattern for enabling spark user defined functions as well. This is a powerful pattern, and once your project hits a critical mass with this type of functionality it makes rinse and repeat operations straight forward.

```kotlin
withSpark(Configuration()) {
    val myDataSet = withFileCache("myCachedDataSet") {
        postgreQuery("SELECT * FROM my_table")
    }
    myDataSet.show(false)
}
```


## Groovy example {#groovy-example}

In this section I'm going to focus on an example DSL for interacting with MongoDB. This can be useful for dealing with tests assets and maintaining contracts with a contract-less database. There are some limitations of the Groovy example, I've never figured out how to make Intellij auto-complete things inside the `withMongo` block you'll see described below. It is probably possible, so if someone reads this, please let me know how to do that in the comments. This code is depending on the dynamic nature of Groovy, so generally speaking I wouldn't recommend this type of thing in production, and would likely reach for the Kotlin example for production use cases.


### The DSL Class {#the-dsl-class}

First we need a class to hang our functionality off of. In this example we'll call it `MongoDSL`. I'm fudging the lines here a little and assuming that `MyObject` is the contractual object and it has a method that serializes the object to a map.

```groovy
package info.offthecob.dsl

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import com.mongodb.client.result.InsertOneResult

class MongoDSL {
    private final MongoClient mongoClient
    private String databaseName
    MongoDSL(MongoClient mongoClient, String databaseName) {
        this.mongoClient = mongoClient
        this.databaseName = databaseName
    }

    InsertOneResult put(String collectionName, MyObject myObject) {
        MongoDatabase database = mongoClient.getDatabase(databaseName)
        def collection = database.getCollection(collectionName)
        return collection.insertOne(new Document(myObject.asMap()))
    }
}
```


### Delegation {#delegation}

I'm a big fan of `spockframework` for testing, so I'll show how to use `MongoDSL` in that context. The part that's interesting in here is the way we delegate the closure to the DSL. Which is similar to what we are doing inside the `withSpark` block in the Kotlin example. The effect of this is that the closure runs in the context of the DSL object.

```groovy
package info.offthecob.dsl

import com.mongodb.client.MongoClients
import spock.lang.Specification
import spock.lang.Shared
import org.testcontainers.containers.MongoDBContainer

class IntegrationSpecification extends Specification {
    @Shared
    MongoDBContainer mongoDBContainer = null

    void setupSpec() {
        mongoDBContainer = new MongoDBContainer(parse("mongo:6"))
        mongoDBContainer.start()
    }

    void cleanupSpec() {
        if (mongoDBContainer != null) {
            mongoDBContainer.stop()
        }
    }

    void withMongo(String databaseName, Closure closure) {
        try (MongoClient mongoClient = MongoClients.create(mongoDBContainer.getConnectionString()) {
            def dsl = new MongoDSL(mongoClient, databaseName)
            closure.delegate = dsl
            closure()
        }
    }
}
```


### DSL Usage {#dsl-usage}

Now the payoff is when this stuff gets put into practice. As you can see the `MongoDSL` can be extended in interesting ways, keeping the readability of the test and test data interactions simple.

```groovy
package info.offthecob.dsl

import spock.lang.Specification

class MyTest extends IntegrationSpecification {

   def "my happy path test"() {
        given:
        def givenName = "the name of the object"
        def givenAttribute =  "and some other attribute"
        def myObject = new MyObject(givenName, givenAttribute)
        withMongo("SomeMongoDbName") {
            put("SomeCollectionName", myObject)
        }

        when:
        def observedAttribute = new MyService(mongoDBContainer.getConnectionString())
            .someBusinessProcessApiToGetTheAttribute(givenName)

        then:
        observedAttribute == givenAttribute
    }
}
```


## Conclusion {#conclusion}

I've used these patterns extensively in my career, and the common sentiment from the other developers on my team is, "this is really fun to work with." It enables us to focus on the business value we are trying to deliver and hide the boiler plate. Because of it's fluent nature, you can model the DSL after the problems your business is asking you to solve. Admittedly, spark's builder syntax is a perfect fit for this type of pattern, and I'm sure I'm not the first person to come up with something like this because I believe Scala also has similar functionality. Meet the problems with the tools you have, and I know Kotlin and Groovy, so that's what I've employed here.
