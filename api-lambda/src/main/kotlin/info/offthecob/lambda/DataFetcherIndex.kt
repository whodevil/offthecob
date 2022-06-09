package info.offthecob.lambda

import graphql.schema.DataFetcher

class DataFetcherIndex {
    fun thing() = DataFetcher {
        listOf(Thing("thing1"), Thing("thing2"))
    }
}

data class Thing(val name: String)
