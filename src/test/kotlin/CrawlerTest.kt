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
        mockkStatic(Jsoup::class)
        every {
            Jsoup.connect("https://zw.cdzj.chengdu.gov.cn/zwdt/SCXX/Default.aspx?action=ucSCXXShowNew2").get()
        } returns apartmentDocument

        every { apartmentRepository.hasApartment(any()) } returns false

        crawler.crawl()

        verify(exactly = 1) {
            apartmentRepository.saveApartment(withArg { assertTrue(it.name == "爱情东麓玖里") })
            apartmentRepository.saveApartment(withArg { assertTrue(it.name == "都会森林一期") })

            notificationService.addIntoQueue(withArg { assertTrue(it.name == "爱情东麓玖里") })
            notificationService.addIntoQueue(withArg { assertTrue(it.name == "都会森林一期") })
            notificationService.sendNotification()
        }
    }

    @Test
    internal fun `should not save apartment when interested apartments already existed in database`() {
        val apartmentPageHtml = this.javaClass.getResource("apartment-page-with-interested-apartment.html").readText()
        val apartmentDocument = Jsoup.parse(apartmentPageHtml)
        mockkStatic(Jsoup::class)
        every {
            Jsoup.connect("https://zw.cdzj.chengdu.gov.cn/zwdt/SCXX/Default.aspx?action=ucSCXXShowNew2").get()
        } returns apartmentDocument

        every { apartmentRepository.hasApartment(any()) } returns true

        crawler.crawl()

        verify(exactly = 0) {
            apartmentRepository.saveApartment(any())
            notificationService.addIntoQueue(any())
        }
    }
}