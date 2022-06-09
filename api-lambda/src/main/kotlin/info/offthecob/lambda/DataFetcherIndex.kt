package info.offthecob.lambda

import graphql.schema.DataFetcher
import javax.inject.Inject
import javax.inject.Named

class DataFetcherIndex @Inject constructor(@Named("serviceVersion") private val serviceVersion: String) {
    fun thing() = DataFetcher {
        listOf(Thing("thing1"), Thing("thing2"))
    }

    fun serviceDefinition() = DataFetcher {
        ServiceDefinition(serviceVersion)
    }
}

data class Thing(val name: String)
data class ServiceDefinition(var version: String)
