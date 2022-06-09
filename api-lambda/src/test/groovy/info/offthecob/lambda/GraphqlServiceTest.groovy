package info.offthecob.lambda

import com.google.inject.Guice
import groovy.json.JsonOutput
import spock.lang.Specification

class GraphqlServiceTest extends Specification {
    def "throw runtime exception on null data"() {
        given:
        def graphql = Guice.createInjector(new ServiceModule()).getInstance(GraphqlService.class)

        when:
        graphql.request(null, null)

        then:
        thrown RuntimeException
    }

    def "exception on bad data"() {
        given:
        def graphql = Guice.createInjector(new ServiceModule()).getInstance(GraphqlService.class)
        def query = new GraphqlRequest("{things}", [:], "")
        def json = JsonOutput.toJson(query)
        def request = TestUtils.buildRequest("POST", json)

        when:
        def response = graphql.request(json, request)

        then:
        response.contains("SubSelectionRequired")
    }

    def "happy path named operation"() {
        given:
        def graphql = Guice.createInjector(new ServiceModule()).getInstance(GraphqlService.class)
        def query = new GraphqlRequest("{ things { name } }", [:], "")
        def json = JsonOutput.toJson(query)
        def request = TestUtils.buildRequest("POST", json)

        when:
        def response = graphql.request(json, request)

        then:
        response.contains("thing1")
        response.contains("thing2")
    }
}
