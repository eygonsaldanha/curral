package ey.buriti.curral.api

import ey.buriti.curral.API_BASE_URL
import ey.buriti.curral.auth.IAuthRepository
import ey.buriti.curral.dto.AnimalDto
import ey.buriti.curral.dto.AnimalEventDto
import ey.buriti.curral.dto.AnimalGroupDto
import ey.buriti.curral.dto.GestationDto
import ey.buriti.curral.dto.ProducaoEntryDto
import ey.buriti.curral.dto.StockItemDto
import ey.buriti.curral.dto.SyncPushPayload
import ey.buriti.curral.dto.SyncResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KtorApiService(
    private val authRepository: IAuthRepository,
    private val baseUrl: String = API_BASE_URL,
) : IApiService {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private suspend fun token() = authRepository.currentToken()

    override suspend fun pushSync(payload: SyncPushPayload): SyncResponse =
        client.post("$baseUrl/api/sync/push") {
            bearerAuth(token())
            contentType(ContentType.Application.Json)
            setBody(payload)
        }.body()

    override suspend fun pullSync(since: String): SyncResponse =
        client.get("$baseUrl/api/sync/pull") {
            bearerAuth(token())
            parameter("since", since)
        }.body()

    override suspend fun getAnimals(since: String?): List<AnimalDto> =
        client.get("$baseUrl/api/animals") {
            bearerAuth(token())
            since?.let { parameter("since", it) }
        }.body()

    override suspend fun upsertAnimal(dto: AnimalDto): AnimalDto =
        client.put("$baseUrl/api/animals/${dto.id}") {
            bearerAuth(token())
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body()

    override suspend fun deleteAnimal(id: String) {
        client.delete("$baseUrl/api/animals/$id") { bearerAuth(token()) }
    }

    override suspend fun getGroups(since: String?): List<AnimalGroupDto> =
        client.get("$baseUrl/api/groups") {
            bearerAuth(token())
            since?.let { parameter("since", it) }
        }.body()

    override suspend fun upsertGroup(dto: AnimalGroupDto): AnimalGroupDto =
        client.put("$baseUrl/api/groups/${dto.id}") {
            bearerAuth(token())
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body()

    override suspend fun deleteGroup(id: String) {
        client.delete("$baseUrl/api/groups/$id") { bearerAuth(token()) }
    }

    override suspend fun getEvents(since: String?): List<AnimalEventDto> =
        client.get("$baseUrl/api/events") {
            bearerAuth(token())
            since?.let { parameter("since", it) }
        }.body()

    override suspend fun upsertEvent(dto: AnimalEventDto): AnimalEventDto =
        client.put("$baseUrl/api/events/${dto.id}") {
            bearerAuth(token())
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body()

    override suspend fun deleteEvent(id: String) {
        client.delete("$baseUrl/api/events/$id") { bearerAuth(token()) }
    }

    override suspend fun getGestations(since: String?): List<GestationDto> =
        client.get("$baseUrl/api/gestations") {
            bearerAuth(token())
            since?.let { parameter("since", it) }
        }.body()

    override suspend fun upsertGestation(dto: GestationDto): GestationDto =
        client.put("$baseUrl/api/gestations/${dto.id}") {
            bearerAuth(token())
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body()

    override suspend fun deleteGestation(id: String) {
        client.delete("$baseUrl/api/gestations/$id") { bearerAuth(token()) }
    }

    override suspend fun getProducao(since: String?): List<ProducaoEntryDto> =
        client.get("$baseUrl/api/producao") {
            bearerAuth(token())
            since?.let { parameter("since", it) }
        }.body()

    override suspend fun upsertProducaoEntry(dto: ProducaoEntryDto): ProducaoEntryDto =
        client.put("$baseUrl/api/producao/${dto.id}") {
            bearerAuth(token())
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body()

    override suspend fun deleteProducaoEntry(id: String) {
        client.delete("$baseUrl/api/producao/$id") { bearerAuth(token()) }
    }

    override suspend fun getStock(since: String?): List<StockItemDto> =
        client.get("$baseUrl/api/stock") {
            bearerAuth(token())
            since?.let { parameter("since", it) }
        }.body()

    override suspend fun upsertStockItem(dto: StockItemDto): StockItemDto =
        client.put("$baseUrl/api/stock/${dto.id}") {
            bearerAuth(token())
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body()

    override suspend fun deleteStockItem(id: String) {
        client.delete("$baseUrl/api/stock/$id") { bearerAuth(token()) }
    }
}
