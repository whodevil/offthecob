package info.offthecob.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
class Graphql : RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    override fun handleRequest(input: APIGatewayV2HTTPEvent?, context: Context?): APIGatewayV2HTTPResponse {
        logger.info { "input: ${input?.requestContext?.http?.method}" }
        logger.info { "body: ${input?.body}"}
        val response = APIGatewayV2HTTPResponse()
        when(input?.requestContext?.http?.method) {
            "POST" -> {
                response.body = "success"
                response.statusCode = 200
            }
            else -> {
                response.body = "not sure what happened"
                response.statusCode = 400
            }
        }
        return response
    }
}
