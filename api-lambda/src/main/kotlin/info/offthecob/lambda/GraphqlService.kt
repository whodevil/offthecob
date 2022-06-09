package info.offthecob.lambda

import com.google.gson.Gson
import graphql.ExecutionInput
import graphql.GraphQL
import info.offthecob.common.OpenForTesting
import mu.KotlinLogging
import javax.inject.Inject

private val logger = KotlinLogging.logger {}

@OpenForTesting
class GraphqlService @Inject constructor(private val graphql: GraphQL, private val gson: Gson) {
    fun request(input: String, context: Any): String {
        logger.info { "Body: $input" }
        return executeRequest(buildExecutionInput(input).context(context).build())
    }

    private fun executeRequest(executionInput: ExecutionInput): String {
        val result = graphql.execute(executionInput)
        logger.info { "finished query execution" }
        return when {
            result.isDataPresent -> gson.toJson(result.toSpecification())
            result.errors.isNotEmpty() -> "{\"error\": \"${result.errors.joinToString { it.message }}\"}"
            else -> "{}"
        }
    }

    private fun buildExecutionInput(body: String): ExecutionInput.Builder {
        val request = gson.fromJson(body, GraphqlRequest::class.java)
        logger.info { "parsed request: $request" }
        val builder = ExecutionInput.newExecutionInput(request.query)
        if (request.variables?.isNotEmpty() == true) {
            logger.info { "handling variables" }
            builder.variables(request.variables)
        }
        if (request.operationName?.isNotEmpty() == true) {
            logger.info { "operation name" }
            builder.operationName(request.operationName)
        }
        return builder
    }
}

data class GraphqlRequest(val query: String, val variables: Map<String, Any>?, val operationName: String?)
