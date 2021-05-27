import mu.KotlinLogging
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.PublishRequest

interface NotificationBroker {
    fun send(title: String, message: String)
}

class AwsSns : NotificationBroker {
    private val logger = KotlinLogging.logger {}
    private var snsClient = SnsClient.builder().region(Region.AP_NORTHEAST_1).build()

    override fun send(title: String, message: String) {
        val topicArn = "arn:aws:sns:ap-northeast-1:197892137344:apartment-registration-alert"
        val publishRequest = PublishRequest.builder().topicArn(topicArn).subject(title).message(message).build()
        snsClient.publish(publishRequest)
        logger.info { "Email sent, title: [${title}]" }
    }
}

class NotificationService(private val brokers: List<NotificationBroker>) {
    private val apartments = mutableListOf<Apartment>()
    private val logger = KotlinLogging.logger {}

    fun addIntoQueue(apartment: Apartment) {
        apartments.add(apartment)
    }

    fun sendNotification() {
        if (apartments.isEmpty()) {
            return
        }

        logger.info { "Sending notification for [${apartments.size}] new apartments" }

        val title = "开盘提醒: " + apartments.joinToString(separator = ", ") { it.name }
        val message = buildContentMessage()
        brokers.forEach { it.send(title, message) }
        apartments.clear()
    }

    private fun buildContentMessage() =
        apartments.joinToString(separator = "\n") { "[${it.date}][${it.district}][${it.type}]${it.name}" } +
                "\n\n\n" +
                "URL: https://zw.cdzj.chengdu.gov.cn/zwdt/SCXX/Default.aspx?action=ucSCXXShowNew2"
}