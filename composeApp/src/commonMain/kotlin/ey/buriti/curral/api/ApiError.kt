package ey.buriti.curral.api

sealed class ApiError : Exception() {
    data class Http(val code: Int, override val message: String) : ApiError()
    data class Network(override val cause: Throwable?) : ApiError()
    data class Unknown(override val cause: Throwable?) : ApiError()
}
