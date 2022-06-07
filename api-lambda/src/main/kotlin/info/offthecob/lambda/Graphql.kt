package info.offthecob.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler

class Graphql : RequestHandler<Any, String> {
    override fun handleRequest(input: Any?, context: Context?): String {
        return when (input!!) {
            is String -> return "hello $input"
            is Map<*, *> -> return "hello Map"
            else -> "unsupported"
        }
    }
}
