package com.happydev.sampleapp.models

data class Business(
    val alias: String? = null,
    val categories: List<Category>? = null,
    val coordinates: Coordinates? = null,
    val display_phone: String? = null,
    val distance: Double? = null,
    val id: String? = null,
    val image_url: String? = null,
    val is_closed: Boolean? = null,
    val location: Location? = null,
    val name: String? = null,
    val phone: String? = null,
    val price: String? = null,
    val rating: Double? = null,
    val review_count: Int? = null,
    val transactions: List<String>? = null,
    val url: String? = null
)