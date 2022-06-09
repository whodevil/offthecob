package info.offthecob.lambda

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent

class TestUtils {
    static def buildRequest(String method, String body) {
        def http = APIGatewayV2HTTPEvent.RequestContext.Http
                .builder()
                .withMethod(method)
                .build()
        def requestContext = APIGatewayV2HTTPEvent.RequestContext
                .builder()
                .withHttp(http)
                .build()
        return APIGatewayV2HTTPEvent
                .builder()
                .withBody(body)
                .withRequestContext(requestContext)
                .build()
    }
}
