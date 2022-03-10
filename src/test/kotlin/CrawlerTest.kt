import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import io.mockk.verify
import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CrawlerTest {
    @MockK(relaxed = true)
    private lateinit var apartmentRepository: ApartmentRepository

    @MockK(relaxed = true)
    private lateinit var notificationService: NotificationService

    private lateinit var crawler: Crawler


    @BeforeEach
    internal fun setUp() {
        MockKAnnotations.init(this)
        crawler = Crawler(apartmentRepository, notificationService)
    }

    @Test
    internal fun `should save new apartment and send notification when find new interested apartments`() {
        val apartmentPageHtml = this.javaClass.getResource("apartment-page-with-interested-apartment.html").readText()
        val apartmentDocument = Jsoup.parse(apartmentPageHtml)
        val targetUrl = "https://test.com"

        mockkStatic(Jsoup::class)
        every { Jsoup.connect(targetUrl).get() } returns apartmentDocument

        every { apartmentRepository.hasApartment(any()) } returns false

        crawler.crawl(targetUrl)

        verify(exactly = 1) {
            apartmentRepository.saveApartment(withArg { assertTrue(it.name == "高新区公寓") })
            apartmentRepository.saveApartment(withArg { assertTrue(it.name == "高新区住宅") })
            apartmentRepository.saveApartment(withArg { assertTrue(it.name == "天府新区住宅") })
            apartmentRepository.saveApartment(withArg { assertTrue(it.name == "天府新区公寓") })

            notificationService.addIntoQueue(withArg { assertTrue(it.name == "高新区公寓") })
            notificationService.addIntoQueue(withArg { assertTrue(it.name == "高新区住宅") })
            notificationService.addIntoQueue(withArg { assertTrue(it.name == "天府新区住宅") })
            notificationService.addIntoQueue(withArg { assertTrue(it.name == "天府新区公寓") })
            notificationService.sendNotification(targetUrl)
        }
    }

    @Test
    internal fun `should not save apartment when interested apartments already existed in database`() {
        val apartmentPageHtml = this.javaClass.getResource("apartment-page-with-interested-apartment.html").readText()
        val apartmentDocument = Jsoup.parse(apartmentPageHtml)
        val targetUrl = "https://test.com"

        mockkStatic(Jsoup::class)
        every {
            Jsoup.connect(targetUrl).get()
        } returns apartmentDocument

        every { apartmentRepository.hasApartment(any()) } returns true

        crawler.crawl(targetUrl)

        verify(exactly = 0) {
            apartmentRepository.saveApartment(any())
            notificationService.addIntoQueue(any())
        }
    }
}