package info.offthecob.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
class Graphql : RequestHandler<Any, String> {
    override fun handleRequest(input: Any?, context: Context?): String {
        return when (input!!) {
            is String -> return "hello $input"
            is Map<*, *> -> {
                logger.info { "###### input: $input" }
                return "hello Map"
            }
            else -> "unsupported"
        }
    }
}
