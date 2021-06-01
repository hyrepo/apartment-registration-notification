import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.FirestoreOptions
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class Application : RequestHandler<Any, Unit> {
    override fun handleRequest(input: Any?, context: Context?) {
        logger.info { "Application started" }

        val db = FirestoreOptions.getDefaultInstance().toBuilder()
            .setProjectId("apartment-registration-alert")
            .setCredentials(GoogleCredentials.getApplicationDefault())
            .build()
            .service

        val crawler = Crawler(ApartmentRepository(db), NotificationService(listOf(AwsSns())))

        crawler.start()

        logger.info { "application finished" }
    }
}