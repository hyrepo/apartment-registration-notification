import com.google.cloud.firestore.Firestore
import mu.KotlinLogging

data class Apartment(val date: String, val district: String, val type: String, val name: String)

class ApartmentRepository(private var db: Firestore) {
    private val logger = KotlinLogging.logger {}

    fun hasApartment(apartment: Apartment): Boolean {
        val apartmentsInDb = db.collection("apartments")
            .whereEqualTo("date", apartment.date)
            .whereEqualTo("district", apartment.district)
            .whereEqualTo("type", apartment.type)
            .whereEqualTo("name", apartment.name)
            .get()
            .get()
            .documents

        return when {
            apartmentsInDb.isEmpty() -> {
                false
            }
            apartmentsInDb.size == 1 -> {
                true
            }
            else -> {
                logger.error { "Apartment [${apartment.date}][${apartment.district}][${apartment.type}]${apartment.name} has ${apartmentsInDb.size} items in DB" }
                true
            }
        }

    }

    fun saveApartment(apartment: Apartment) {
        logger.info { "Saving $apartment" }
        db.collection("apartments")
            .add(
                mapOf(
                    "date" to apartment.date,
                    "district" to apartment.district,
                    "type" to apartment.type,
                    "name" to apartment.name,
                )
            )
    }
}