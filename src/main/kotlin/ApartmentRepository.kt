import com.google.cloud.firestore.Firestore
import mu.KotlinLogging

data class Apartment(val date: String, val district: String, val type: String, val name: String)

interface ApartmentRepository {
    fun hasApartment(apartment: Apartment): Boolean
    fun saveApartment(apartment: Apartment)
}

class GcpApartmentRepository(private var db: Firestore) : ApartmentRepository {
    private val logger = KotlinLogging.logger {}

    override fun hasApartment(apartment: Apartment): Boolean {
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

    override fun saveApartment(apartment: Apartment) {
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