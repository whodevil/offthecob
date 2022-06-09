package info.offthecob.lambda

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import com.google.common.net.HttpHeaders.*
import com.google.common.net.MediaType
import info.offthecob.lambda.ServiceModule.Companion.ALLOWED_ORIGIN
import mu.KotlinLogging
import java.util.*
import javax.inject.Inject
import javax.inject.Named

private val logger = KotlinLogging.logger {}

class RequestRouter @Inject constructor(
    private val service: GraphqlService,
    @Named(ALLOWED_ORIGIN) private val allowedOrigin: String
) {
    companion object {
        const val BAD_REQUEST = "bad request"
        const val ALLOW_METHODS = "OPTIONS, POST"
        const val ALLOW_HEADERS = "Content-Type, X-Requested-With"
    }

    fun response(input: APIGatewayV2HTTPEvent?): APIGatewayV2HTTPResponse {
        val response = APIGatewayV2HTTPResponse()
        try {
            when (input!!.requestContext!!.http!!.method) {
                "POST" -> {
                    logger.info { "handling POST" }
                    val decodedBody = String(Base64.getDecoder().decode(input.body))
                    response.body = service.request(decodedBody, input)
                    response.headers = response.headers ?: mutableMapOf()
                    response.headers[CONTENT_TYPE] = MediaType.JSON_UTF_8.toString()
                    response.statusCode = 200
                }
                "OPTIONS" -> {
                    logger.info { "handling OPTIONS" }
                    response.headers = response.headers ?: mutableMapOf()
                    response.headers[ACCESS_CONTROL_ALLOW_ORIGIN] = allowedOrigin
                    response.headers[ACCESS_CONTROL_ALLOW_METHODS] = ALLOW_METHODS
                    response.headers[ACCESS_CONTROL_ALLOW_HEADERS] = ALLOW_HEADERS
                    response.statusCode = 204
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
