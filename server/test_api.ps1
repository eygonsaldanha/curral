# =============================================================================
# Curral API — Script de testes
# =============================================================================
# Pré-requisitos:
#   1. Docker rodando: docker compose up -d
#   2. Servidor rodando com a variável de ambiente correta:
#      $env:SUPABASE_JWT_SECRET="test_secret"; .\gradlew.bat :server:run
#   3. Constants.kt deve ter SUPABASE_URL = "https://<seu-projeto>.supabase.co"
#      (ou o valor real do seu projeto Supabase)
#
# Para gerar um novo token (expira em 30 dias):
   node -e "
     const c=require('crypto'),s='test_secret',iss='https://<seu-projeto>.supabase.co',n=Math.floor(Date.now()/1000);
     const h=Buffer.from(JSON.stringify({alg:'HS256',typ:'JWT'})).toString('base64url');
     const p=Buffer.from(JSON.stringify({sub:'farm-dev-001',iss,iat:n,exp:n+86400*30})).toString('base64url');
     console.log(h+'.'+p+'.'+c.createHmac('sha256',s).update(h+'.'+p).digest('base64url'));
   "
# =============================================================================

$BASE = "http://localhost:8080"

# Cole aqui o token gerado acima (ou o JWT real do Supabase)
$TOKEN = "eyJhbGciOiJFUzI1NiIsImtpZCI6IjBiNmY5ZDc3LTJjMzktNDQwNC1hNGQ0LTgzNzBiMGMzOGRhMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL3p4eGpreWVodXZhb3NjZXBhY2pyLnN1cGFiYXNlLmNvL2F1dGgvdjEiLCJzdWIiOiIxYzU3YzYxYi00ODg0LTRlOWEtYThlYy1jMjA3MWZlYjk5OGYiLCJhdWQiOiJhdXRoZW50aWNhdGVkIiwiZXhwIjoxNzc5MTYxMzk2LCJpYXQiOjE3NzkxNTc3OTYsImVtYWlsIjoiZXlnb24xMUBnbWFpbC5jb20iLCJwaG9uZSI6IiIsImFwcF9tZXRhZGF0YSI6eyJwcm92aWRlciI6ImVtYWlsIiwicHJvdmlkZXJzIjpbImVtYWlsIl19LCJ1c2VyX21ldGFkYXRhIjp7ImVtYWlsIjoiZXlnb24xMUBnbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwicGhvbmVfdmVyaWZpZWQiOmZhbHNlLCJzdWIiOiIxYzU3YzYxYi00ODg0LTRlOWEtYThlYy1jMjA3MWZlYjk5OGYifSwicm9sZSI6ImF1dGhlbnRpY2F0ZWQiLCJhYWwiOiJhYWwxIiwiYW1yIjpbeyJtZXRob2QiOiJwYXNzd29yZCIsInRpbWVzdGFtcCI6MTc3OTE1Nzc5Nn1dLCJzZXNzaW9uX2lkIjoiMGZiMmYxYTktZDhiZC00ZjM0LTgzMWUtOGZiNTA1Y2FlMjJhIiwiaXNfYW5vbnltb3VzIjpmYWxzZX0.gDmS5G6fNMA83kRtcKthWxNW9CiJ3LIde9jpSdIIa89gh6mL4FG56Aigqz-PusNt17ahHAhwTFGnUutaDGxg-A"

$H = @{Authorization = "Bearer $TOKEN"; "Content-Type" = "application/json"}

Write-Host "`n=== HEALTH CHECK ===" -ForegroundColor Cyan
Invoke-RestMethod -Uri "$BASE/" -Method Get

Write-Host "`n=== ANIMALS ===" -ForegroundColor Cyan

# GET todos os animais
Write-Host "`n[GET] /api/animals"
Invoke-RestMethod -Uri "$BASE/api/animals" -Headers $H

# GET com filtro de data
Write-Host "`n[GET] /api/animals?since=2024-01-01T00:00:00Z"
Invoke-RestMethod -Uri "$BASE/api/animals?since=2024-01-01T00:00:00Z" -Headers $H

# POST criar animal
Write-Host "`n[POST] /api/animals"
$animal = @{
    id = "animal-001"
    farmId = "farm-dev-001"
    name = "Mimosa"
    type = "Bovino"
    breed = "Nelore"
    status = "Ativo"
    sex = "Fêmea"
    tagNumber = "001"
    birthDate = "2022-03-15"
    weightKg = 450.0
    groupIds = @()
    offspringIds = @()
    version = 0
    updatedAt = (Get-Date -Format "yyyy-MM-ddTHH:mm:ssZ")
} | ConvertTo-Json
Invoke-RestMethod -Uri "$BASE/api/animals" -Method Post -Headers $H -Body $animal

# GET animal por ID
Write-Host "`n[GET] /api/animals/animal-001"
Invoke-RestMethod -Uri "$BASE/api/animals/animal-001" -Headers $H

# PUT atualizar animal
Write-Host "`n[PUT] /api/animals/animal-001"
$animalUpdate = @{
    id = "animal-001"
    farmId = "farm-dev-001"
    name = "Mimosa"
    type = "Bovino"
    breed = "Nelore"
    status = "Ativo"
    sex = "Fêmea"
    tagNumber = "001"
    birthDate = "2022-03-15"
    weightKg = 480.0
    groupIds = @()
    offspringIds = @()
    version = 1
    updatedAt = (Get-Date -Format "yyyy-MM-ddTHH:mm:ssZ")
} | ConvertTo-Json
Invoke-RestMethod -Uri "$BASE/api/animals/animal-001" -Method Put -Headers $H -Body $animalUpdate

Write-Host "`n=== GROUPS ===" -ForegroundColor Cyan

# POST criar grupo
Write-Host "`n[POST] /api/groups"
$group = @{
    id = "group-001"
    farmId = "farm-dev-001"
    name = "Lote A"
    description = "Bezerros desmamados"
    animalIds = @("animal-001")
    version = 0
    updatedAt = (Get-Date -Format "yyyy-MM-ddTHH:mm:ssZ")
} | ConvertTo-Json
Invoke-RestMethod -Uri "$BASE/api/groups" -Method Post -Headers $H -Body $group

# GET grupos
Write-Host "`n[GET] /api/groups"
Invoke-RestMethod -Uri "$BASE/api/groups" -Headers $H

Write-Host "`n=== EVENTS ===" -ForegroundColor Cyan

# POST criar evento
Write-Host "`n[POST] /api/events"
$event = @{
    id = "event-001"
    farmId = "farm-dev-001"
    animalId = "animal-001"
    type = "Pesagem"
    date = "2024-05-18"
    time = "08:00"
    notes = "Pesagem mensal"
    weightKg = 480.0
    version = 0
    updatedAt = (Get-Date -Format "yyyy-MM-ddTHH:mm:ssZ")
} | ConvertTo-Json
Invoke-RestMethod -Uri "$BASE/api/events" -Method Post -Headers $H -Body $event

Write-Host "`n=== GESTAÇÕES ===" -ForegroundColor Cyan

# POST criar gestação
Write-Host "`n[POST] /api/gestations"
$gestation = @{
    id = "gest-001"
    farmId = "farm-dev-001"
    animalId = "animal-001"
    startDate = "2024-03-01"
    expectedBirthDate = "2024-12-01"
    notes = "Primeira gestação"
    version = 0
    updatedAt = (Get-Date -Format "yyyy-MM-ddTHH:mm:ssZ")
} | ConvertTo-Json
Invoke-RestMethod -Uri "$BASE/api/gestations" -Method Post -Headers $H -Body $gestation

Write-Host "`n=== PRODUÇÃO ===" -ForegroundColor Cyan

# POST criar entrada de produção
Write-Host "`n[POST] /api/producao"
$producao = @{
    id = "prod-001"
    farmId = "farm-dev-001"
    productType = "Leite"
    quantity = 25.5
    unit = "Litros"
    date = "2024-05-18"
    notes = "Ordenha da manhã"
    version = 0
    updatedAt = (Get-Date -Format "yyyy-MM-ddTHH:mm:ssZ")
} | ConvertTo-Json
Invoke-RestMethod -Uri "$BASE/api/producao" -Method Post -Headers $H -Body $producao

Write-Host "`n=== ESTOQUE ===" -ForegroundColor Cyan

# POST criar item de estoque
Write-Host "`n[POST] /api/stock"
$stock = @{
    id = "stock-001"
    farmId = "farm-dev-001"
    name = "Ração Bovina"
    category = "RACAO"
    quantity = 100
    unit = "kg"
    lowStockThreshold = 20
    version = 0
    updatedAt = (Get-Date -Format "yyyy-MM-ddTHH:mm:ssZ")
} | ConvertTo-Json
Invoke-RestMethod -Uri "$BASE/api/stock" -Method Post -Headers $H -Body $stock

# GET estoque
Write-Host "`n[GET] /api/stock"
Invoke-RestMethod -Uri "$BASE/api/stock" -Headers $H

Write-Host "`n=== SYNC ===" -ForegroundColor Cyan

# POST sync/push — enviar dados do mobile
Write-Host "`n[POST] /api/sync/push"
$syncPush = @{
    animals = @()
    groups = @()
    events = @()
    gestations = @()
    producao = @()
    stock = @(@{
        id = "stock-002"
        farmId = "farm-dev-001"
        name = "Ivermectina"
        category = "REMEDIOS"
        quantity = 5
        unit = "frascos"
        lowStockThreshold = 2
        version = 0
        updatedAt = (Get-Date -Format "yyyy-MM-ddTHH:mm:ssZ")
    })
} | ConvertTo-Json -Depth 5
Invoke-RestMethod -Uri "$BASE/api/sync/push" -Method Post -Headers $H -Body $syncPush

# GET sync/pull — buscar atualizações
Write-Host "`n[GET] /api/sync/pull?since=1970-01-01T00:00:00Z"
Invoke-RestMethod -Uri "$BASE/api/sync/pull?since=1970-01-01T00:00:00Z" -Headers $H

Write-Host "`n=== DELETE ===" -ForegroundColor Cyan

# DELETE animal (soft delete)
Write-Host "`n[DELETE] /api/animals/animal-001"
Invoke-RestMethod -Uri "$BASE/api/animals/animal-001" -Method Delete -Headers $H

Write-Host "`n✅ Testes concluídos!" -ForegroundColor Green
