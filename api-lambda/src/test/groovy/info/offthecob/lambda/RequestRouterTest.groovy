package info.offthecob.lambda

import com.google.common.base.Charsets
import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import spock.lang.Specification

import static com.google.common.net.HttpHeaders.CONTENT_TYPE

class RequestRouterTest extends Specification {
    GraphqlService service
    RequestRouter router

    def setup() {
        service = Mock(GraphqlService)
        router = new RequestRouter(service, "*")
    }

    def "null data returns 400"() {
        when:
        def response = router.response(null)

        then:
        response.statusCode == 400
        response.body == RequestRouter.BAD_REQUEST
    }

    def "NON-POST http methods result in 400"() {
        given:
        def request = TestUtils.buildRequest(method, "")

        when:
        def response = router.response(request)

        then:
        response.statusCode == 400
        response.body == RequestRouter.BAD_REQUEST

        where:
        method   | _
        "GET"    | _
        "PUT"    | _
        "DELETE" | _
    }

    def "happy path produces expected values" () {
        given:
        String mockBody = "MOCK_VALUE"
        def encodedString = Base64.getEncoder().encodeToString(mockBody.getBytes(Charsets.UTF_8))
        def request = TestUtils.buildRequest("POST", encodedString)
        service.request(mockBody,  request) >> mockBody

        when:
        def response = router.response(request)

        then:
        response.statusCode == 200
        response.body == mockBody
        response.headers[CONTENT_TYPE] == MediaType.JSON_UTF_8.toString()
    }

    def "handle options"() {
        given:
        def request = TestUtils.buildRequest("OPTIONS", "")

        when:
        def response = router.response(request)

        then:
        response.headers[HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN] == "*"
        response.headers[HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS] == RequestRouter.ALLOW_METHODS
        response.headers[HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS] == RequestRouter.ALLOW_HEADERS
        response.statusCode == 204
    }
}
