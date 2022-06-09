package info.offthecob.lambda

import com.google.inject.Provides
import dev.misfitlabs.kotlinguice4.KotlinModule
import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

class ServiceModule : KotlinModule() {

    @Provides
    @Singleton
    @Named("schema")
    fun schema(): String {
        return if (System.getProperty("graphqlSchema") == null) {
            ServiceModule::class.java.getResource("/schema.graphql")?.readText(Charsets.UTF_8)
                ?: throw RuntimeException("no schema found")
        } else {
            File(System.getProperty("graphqlSchema")).readText(Charsets.UTF_8)
        }
    }

    @Provides
    @Singleton
    fun typeDefinitionRegistry(@Named("schema") schema: String): TypeDefinitionRegistry {
        return SchemaParser().parse(schema)
    }

    @Provides
    @Singleton
    fun graphql(
        typeDefinitionRegistry: TypeDefinitionRegistry,
        schemaGenerator: SchemaGenerator,
        runtimeWiring: RuntimeWiring
    ): GraphQL {
        val graphqlSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring)
        return GraphQL.newGraphQL(graphqlSchema).build()
    }

    @Provides
    @Singleton
    fun runtimeWiring(dataFetcherIndex: DataFetcherIndex): RuntimeWiring {
        return RuntimeWiring.newRuntimeWiring().type("Query") { builder ->
            builder.dataFetcher("things", dataFetcherIndex.thing())
        }.build()
    }
}
