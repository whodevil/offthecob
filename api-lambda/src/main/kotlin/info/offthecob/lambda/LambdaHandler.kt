package info.offthecob.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import com.google.inject.Guice
import dev.misfitlabs.kotlinguice4.getInstance
import mu.KotlinLogging

class LambdaHandler : RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
    private val router: RequestRouter

    init {
        val injector = Guice.createInjector(ServiceModule())
        router = injector.getInstance()
    }

    override fun handleRequest(input: APIGatewayV2HTTPEvent?, context: Context?): APIGatewayV2HTTPResponse {
        return router.response(input)
    }
}
