package info.offthecob.lambda

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.beust.klaxon.Klaxon
import graphql.ExecutionInput
import graphql.GraphQL
import info.offthecob.common.OpenForTesting
import javax.inject.Inject

@OpenForTesting
class GraphqlService @Inject constructor(private val graphql: GraphQL, private val klaxon: Klaxon) {
    fun request(input: APIGatewayV2HTTPEvent): String {
        return executeRequest(buildExecutionInput(input.body).context(input).build())
    }

    private fun executeRequest(executionInput: ExecutionInput): String {
        val result = graphql.execute(executionInput)
        return when {
            result.isDataPresent -> klaxon.toJsonString(result.toSpecification())
            result.errors.isNotEmpty() -> "{\"error\": \"${result.errors.joinToString { it.message }}\"}"
            else -> "{}"
        }
    }

    private fun buildExecutionInput(body: String): ExecutionInput.Builder {
        val request = klaxon.parse<GraphqlRequest>(body) ?: throw RuntimeException("unable to parse input body")
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
