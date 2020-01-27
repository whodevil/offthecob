package theagainagain

import com.google.inject.Provider
import com.google.inject.name.Names
import dev.misfitlabs.kotlinguice4.KotlinModule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import theagainagain.configuration.ServiceConfiguration
import java.io.File

class ServiceModule : KotlinModule() {
    companion object {
        const val SERVICE_CONFIG_LOGGER: String = "ServiceConfigurationLogger"
        const val SERVICE_VERSION: String = "SERVICE_VERSION"
    }

    override fun configure() {
        super.configure()
        bind<Logger>().annotatedWith(Names.named(SERVICE_CONFIG_LOGGER)).toProvider<ServiceConfigurationLoggerProvider>()
        bind<String>().annotatedWith(Names.named(SERVICE_VERSION)).toProvider<ServiceVersionProvider>()
    }
}

class ServiceConfigurationLoggerProvider: Provider<Logger> {
    override fun get(): Logger {
        return LoggerFactory.getLogger(ServiceConfiguration.javaClass)
    }
}

class ServiceVersionProvider: Provider<String> {
    override fun get(): String {
        return try {
            File("build", "version").readText()
        } catch (e: Exception) {
            "r888"
        }
    }
}