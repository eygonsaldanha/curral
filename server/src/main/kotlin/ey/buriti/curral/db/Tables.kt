package ey.buriti.curral.db

import org.jetbrains.exposed.sql.Table

object Farms : Table("farms") {
    val id = text("id")
    val name = text("name")
    val ownerUserId = text("owner_user_id")
    val createdAt = text("created_at")
    val updatedAt = text("updated_at")
    override val primaryKey = PrimaryKey(id)
}

object UserFarms : Table("user_farms") {
    val userId = text("user_id")
    val farmId = text("farm_id")
    val role = text("role")
    val createdAt = text("created_at")
    override val primaryKey = PrimaryKey(userId, farmId)
}

object Animals : Table("animals") {
    val id = text("id")
    val farmId = text("farm_id")
    val name = text("name")
    val type = text("type")
    val breed = text("breed")
    val status = text("status")
    val sex = text("sex")
    val tagNumber = text("tag_number")
    val birthDate = text("birth_date")
    val weightKg = double("weight_kg")
    val groupIds = text("group_ids").default("[]")
    val motherId = text("mother_id").nullable()
    val fatherId = text("father_id").nullable()
    val offspringIds = text("offspring_ids").default("[]")
    val gestationId = text("gestation_id").nullable()
    val version = long("version").default(0)
    val updatedAt = text("updated_at")
    val deletedAt = text("deleted_at").nullable()
    override val primaryKey = PrimaryKey(id)
}

object AnimalGroups : Table("animal_groups") {
    val id = text("id")
    val farmId = text("farm_id")
    val name = text("name")
    val description = text("description")
    val animalIds = text("animal_ids").default("[]")
    val version = long("version").default(0)
    val updatedAt = text("updated_at")
    val deletedAt = text("deleted_at").nullable()
    override val primaryKey = PrimaryKey(id)
}

object AnimalEvents : Table("animal_events") {
    val id = text("id")
    val farmId = text("farm_id")
    val animalId = text("animal_id")
    val type = text("type")
    val date = text("date")
    val time = text("time")
    val notes = text("notes")
    val weightKg = double("weight_kg").nullable()
    val groupId = text("group_id").nullable()
    val version = long("version").default(0)
    val updatedAt = text("updated_at")
    val deletedAt = text("deleted_at").nullable()
    override val primaryKey = PrimaryKey(id)
}

object Gestations : Table("gestations") {
    val id = text("id")
    val farmId = text("farm_id")
    val animalId = text("animal_id")
    val startDate = text("start_date")
    val expectedBirthDate = text("expected_birth_date")
    val notes = text("notes")
    val fatherId = text("father_id").nullable()
    val version = long("version").default(0)
    val updatedAt = text("updated_at")
    val deletedAt = text("deleted_at").nullable()
    override val primaryKey = PrimaryKey(id)
}

object ProducaoEntries : Table("producao_entries") {
    val id = text("id")
    val farmId = text("farm_id")
    val productType = text("product_type")
    val quantity = double("quantity")
    val unit = text("unit")
    val date = text("date")
    val notes = text("notes")
    val version = long("version").default(0)
    val updatedAt = text("updated_at")
    val deletedAt = text("deleted_at").nullable()
    override val primaryKey = PrimaryKey(id)
}

object StockItems : Table("stock_items") {
    val id = text("id")
    val farmId = text("farm_id")
    val name = text("name")
    val category = text("category")
    val quantity = integer("quantity")
    val unit = text("unit")
    val expiryDate = text("expiry_date").nullable()
    val lowStockThreshold = integer("low_stock_threshold").nullable()
    val version = long("version").default(0)
    val updatedAt = text("updated_at")
    val deletedAt = text("deleted_at").nullable()
    override val primaryKey = PrimaryKey(id)
}
