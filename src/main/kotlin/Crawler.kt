import mu.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node


class Crawler(
    private val apartmentRepository: ApartmentRepository,
    private val notificationService: NotificationService
) {
    private val logger = KotlinLogging.logger {}

    fun crawl(targetUrl: String) {
        logger.info { "Start crawling..." }

        val doc: Document =
            Jsoup.connect(targetUrl).get()

        logger.info { "Finished crawling, parsing result..." }

        val table = doc.select(".table-l.table-la").first()
        val tableRowsInFirstPage = table.childNodes()[5].childNodes()[1].childNodes().filter { it is Element }

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
        notificationService.sendNotification(targetUrl)
    }

    private fun parseApartment(tableRowsInFirstPage: List<Node>, index: Int): Apartment {
        val apartmentRow = tableRowsInFirstPage[index]
        val apartmentAttributes = apartmentRow.childNodes().filter { it is Element }

        val name = apartmentAttributes[1].childNodes()[0].childNodes()[0].toString()
        val district = apartmentAttributes[2].childNodes()[0].childNodes()[0].toString()
        val type = apartmentAttributes[3].childNodes()[0].childNodes()[0].toString()
        val date = apartmentAttributes[5].childNodes()[0].childNodes()[0].toString()

        return Apartment(date, district, type, name)
    }

    private fun isInterestedDistrict(apartment: Apartment) =
        (apartment.district.contains("高新") || apartment.district.contains("天府")) &&
                (apartment.type.contains("住宅") || apartment.type.contains("公寓"))
}