package info.offthecob.lambda

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import com.google.common.net.HttpHeaders.CONTENT_TYPE
import com.google.common.net.MediaType
import mu.KotlinLogging
import javax.inject.Inject

private val logger = KotlinLogging.logger {}

class RequestRouter @Inject constructor(private val service: GraphqlService) {
    companion object {
        const val BAD_REQUEST = "bad request"
    }

    fun response(input: APIGatewayV2HTTPEvent?): APIGatewayV2HTTPResponse {
        val response = APIGatewayV2HTTPResponse()
        try {
            when (input!!.requestContext!!.http!!.method) {
                "POST" -> {
                    logger.info { "handling POST" }
                    response.body = service.request(input)
                    response.headers = response.headers ?: mutableMapOf()
                    response.headers[CONTENT_TYPE] = MediaType.JSON_UTF_8.toString()
                    response.statusCode = 200
                }
                else -> {
                    logger.info { "unsupported http method" }
                    badRequest(response)
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Exception $e" }
            badRequest(response)
        }
        return response
    }

    private fun badRequest(response: APIGatewayV2HTTPResponse) {
        response.body = BAD_REQUEST
        response.statusCode = 400
    }
}
