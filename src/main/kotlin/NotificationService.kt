class NotificationService {
    private val apartments = mutableListOf<Apartment>()

    fun addIntoQueue(apartment: Apartment) {
        apartments.add(apartment)
    }

    fun sendNotification() {
        apartments.forEach { println("[${it.date}][${it.district}][${it.type}]${it.name}") }
        apartments.clear()
    }
}
