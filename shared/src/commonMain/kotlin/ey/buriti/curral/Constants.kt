package ey.buriti.curral

const val SERVER_PORT = 8080

/** URL base da API Ktor (ajustar para produção via build config). */
const val API_BASE_URL = "http://10.0.2.2:$SERVER_PORT" // localhost no emulador Android

/** URL do projeto Supabase — substituir pelo projeto real. */
const val SUPABASE_URL = "https://zxxjkyehuvaoscepacjr.supabase.co"

/** Chave anônima pública do Supabase (não é segredo). */
const val SUPABASE_ANON_KEY = "sb_publishable_DcwOVITbsW6-eA5aQ8BUSw_9WSxMmli"
