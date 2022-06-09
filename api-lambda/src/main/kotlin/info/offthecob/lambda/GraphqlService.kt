package info.offthecob.lambda

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.beust.klaxon.Klaxon
import graphql.ExecutionInput
import graphql.GraphQL
import info.offthecob.common.OpenForTesting
import mu.KotlinLogging
import javax.inject.Inject

private val logger = KotlinLogging.logger {}

@OpenForTesting
class GraphqlService @Inject constructor(private val graphql: GraphQL, private val klaxon: Klaxon) {
    fun request(input: String, context: Any): String {
        logger.info { "Body: ${input}" }
        return executeRequest(buildExecutionInput(input).context(context).build())
    }

    private fun executeRequest(executionInput: ExecutionInput): String {
        val result = graphql.execute(executionInput)
        logger.info { "finished query execution" }
        return when {
            result.isDataPresent -> klaxon.toJsonString(result.toSpecification())
            result.errors.isNotEmpty() -> "{\"error\": \"${result.errors.joinToString { it.message }}\"}"
            else -> "{}"
        }
    }

    private fun buildExecutionInput(body: String): ExecutionInput.Builder {
        val request = klaxon.parse<GraphqlRequest>(body) ?: throw RuntimeException("unable to parse input body")
        logger.info { "parsed request: $request"}
        val builder = ExecutionInput.newExecutionInput(request.query)
        if (request.variables.isNotEmpty()) {
            builder.variables(request.variables)
        }
        if (request.operationName.isNotEmpty()) {
            builder.operationName(request.operationName)
        }
        return builder
    }
}

data class GraphqlRequest(val query: String, val variables: Map<String, Any> = mapOf(), val operationName: String = "")
