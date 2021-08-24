import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.QueryDocumentSnapshot
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GcpApartmentRepositoryTest {
    @MockK(relaxed = true)
    private lateinit var firestore: Firestore

    private lateinit var gcpApartmentRepository: GcpApartmentRepository

    @BeforeEach
    internal fun setUp() {
        MockKAnnotations.init(this)
        gcpApartmentRepository = GcpApartmentRepository(firestore)
    }

    @Test
    internal fun `should return true when verify if apartment exist given the apartment in DB`() {
        every {
            firestore.collection("apartments")
                .whereEqualTo("date", "2020-01-01")
                .whereEqualTo("district", "district A")
                .whereEqualTo("type", "type A")
                .whereEqualTo("name", "apartment A")
                .get()
                .get()
                .documents
        } returns listOf(object : QueryDocumentSnapshot(null, null, null, null, null, null) {})

        assertTrue(
            gcpApartmentRepository.hasApartment(
                Apartment(
                    date = "2020-01-01",
                    district = "district A",
                    type = "type A",
                    name = "apartment A"
                )
            )
        )
    }

    @Test
    internal fun `should return true when verify if apartment exist given the apartment has multiple records in DB`() {
        every {
            firestore.collection("apartments")
                .whereEqualTo("date", "2020-01-01")
                .whereEqualTo("district", "district A")
                .whereEqualTo("type", "type A")
                .whereEqualTo("name", "apartment A")
                .get()
                .get()
                .documents
        } returns listOf(
            object : QueryDocumentSnapshot(null, null, null, null, null, null) {},
            object : QueryDocumentSnapshot(null, null, null, null, null, null) {}
        )

        assertTrue(
            gcpApartmentRepository.hasApartment(
                Apartment(
                    date = "2020-01-01",
                    district = "district A",
                    type = "type A",
                    name = "apartment A"
                )
            )
        )
    }

    @Test
    internal fun `should return false when verify if apartment exist given the apartment not in DB`() {
        every {
            firestore.collection("apartments")
                .whereEqualTo("date", "2020-01-01")
                .whereEqualTo("district", "district A")
                .whereEqualTo("type", "type A")
                .whereEqualTo("name", "apartment A")
                .get()
                .get()
                .documents
        } returns listOf()

        assertFalse(
            gcpApartmentRepository.hasApartment(
                Apartment(
                    date = "2020-01-01",
                    district = "district A",
                    type = "type A",
                    name = "apartment A"
                )
            )
        )
    }

    @Test
    internal fun `should save apartment into DB`() {
        gcpApartmentRepository.saveApartment(
            Apartment(
                date = "2020-01-01",
                district = "district A",
                type = "type A",
                name = "apartment A"
            )
        )

        verify(exactly = 1) {
            firestore.collection("apartments")
                .add(
                    mapOf(
                        "date" to "2020-01-01",
                        "district" to "district A",
                        "type" to "type A",
                        "name" to "apartment A",
                    )
                )
        }
    }
}