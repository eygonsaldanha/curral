package ey.buriti.curral

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform