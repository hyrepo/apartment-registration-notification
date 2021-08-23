import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.FirestoreOptions
import com.google.cloud.functions.Context
import com.google.cloud.functions.RawBackgroundFunction
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Application : RawBackgroundFunction {
    override fun accept(json: String?, context: Context?) {
        logger.info { "Application started" }

        val repository = GcpApartmentRepository(
            FirestoreOptions.getDefaultInstance().toBuilder()
                .setProjectId("apartment-registration-alert")
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .build()
                .service
        )

        val crawler = Crawler(repository, NotificationService(listOf(AwsSns())))

        crawler.crawl()

        logger.info { "application finished" }
    }
}