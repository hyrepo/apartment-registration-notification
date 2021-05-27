import mu.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Node


class Crawler(
    private val apartmentRepository: ApartmentRepository,
    private val notificationService: NotificationService
) {
    private val logger = KotlinLogging.logger {}

    fun start() {
        logger.info { "Start crawling..." }

        val doc: Document =
            Jsoup.connect("https://zw.cdzj.chengdu.gov.cn/zwdt/SCXX/Default.aspx?action=ucSCXXShowNew2").get()

        logger.info { "Finished crawling, parsing result..." }

        val table = doc.select("#ID_ucSCXXShowNew2_gridView").first()!!
        val tableRowsInFirstPage = table.childNodes()[1].childNodes()

        val newApartments = mutableSetOf<Apartment>()
        for (index in 1 until tableRowsInFirstPage.size - 1) {
            val apartment = parseApartment(tableRowsInFirstPage, index)

            if (isInterestedDistrict(apartment) && !apartmentRepository.hasApartment(apartment)) {
                newApartments.add(apartment)
            }
        }

        logger.info { "Found [${newApartments.size}] new apartment" }

        newApartments.forEach {
            apartmentRepository.saveApartment(it)
            notificationService.addIntoQueue(it)
        }
        notificationService.sendNotification()
    }

    private fun parseApartment(
        tableRowsInFirstPage: MutableList<Node>,
        index: Int
    ): Apartment {
        val apartmentRow = tableRowsInFirstPage[index]
        val apartmentAttributes = apartmentRow.childNodes()

        val name = apartmentAttributes[2].childNodes()[0].toString()
        val district = apartmentAttributes[3].childNodes()[0].toString()
        val type = apartmentAttributes[5].childNodes()[0].toString()
        val date = apartmentAttributes[8].childNodes()[1].childNodes()[0].toString()

        return Apartment(date, district, type, name)
    }

    private fun isInterestedDistrict(apartment: Apartment) =
        apartment.district.contains("高新") || apartment.district.contains("天府")
}