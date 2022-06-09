package info.offthecob.lambda

import com.google.inject.Guice
import spock.lang.Specification

class ServiceModuleTest extends Specification {

    def "service can be initialized"() {
        when:
        def router = Guice.createInjector(new ServiceModule()).getInstance(RequestRouter.class)

        then:
        router!=null
    }
}
