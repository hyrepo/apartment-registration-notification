import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.FirestoreOptions
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun main() {
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