import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class NotificationServiceTest {
    @MockK(relaxed = true)
    private lateinit var awsSns: AwsSns

    private lateinit var notificationService: NotificationService

    @BeforeEach
    internal fun setUp() {
        MockKAnnotations.init(this)
        notificationService = NotificationService(listOf(awsSns))
    }

    @Test
    internal fun `should send notification and only send once`() {
        notificationService.addIntoQueue(Apartment("2020-08-22", "District A", "Type A", "Apartment A"))
        notificationService.addIntoQueue(Apartment("2020-08-23", "District B", "Type B", "Apartment B"))

        notificationService.sendNotification()
        notificationService.sendNotification()

        verify(exactly = 1) {
            awsSns.send(
                withArg { assertTrue(it == "开盘提醒: Apartment A, Apartment B") },
                withArg {
                    assertTrue(
                        it == "[2020-08-22][District A][Type A]Apartment A\n" +
                                "[2020-08-23][District B][Type B]Apartment B\n" +
                                "\n" +
                                "\n" +
                                "URL: https://zw.cdzj.chengdu.gov.cn/zwdt/SCXX/Default.aspx?action=ucSCXXShowNew2"
                    )
                }
            )
        }
    }
}