import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.NoCredentials
import com.google.cloud.firestore.FirestoreOptions

fun main() {
    val env = System.getenv("ENV")
    val projectId = "apartment-registration-alert"
    val credentials = if (env != "prod") {
        NoCredentials.getInstance()
    } else {
        GoogleCredentials.getApplicationDefault()
    }
    val db = FirestoreOptions.getDefaultInstance().toBuilder()
        .setProjectId(projectId)
        .setCredentials(credentials)
        .build()
        .service

    val crawler = Crawler(ApartmentRepository(db), NotificationService())

    crawler.start()
}