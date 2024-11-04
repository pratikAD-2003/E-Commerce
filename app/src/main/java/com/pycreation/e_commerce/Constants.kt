package com.pycreation.e_commerce

object Constants {
    // fireStore paths
    const val USER_AUTH = "UsersAuth"
    const val PARTNER_DETAILS = "PartnerAuth"

    // storage
    const val GOVERNMENT_ID_PROOF = "GovIDs"
    const val BUSINESS_REGISTRATION = "BusinessRegistrationIDs"

    // shared preference
    const val USER_SAVED = "UserSaved"
    const val userType = "userType"
    const val TOKEN = "tokenMyfasdfdhsjk"
    const val DOC_UPLOADED = "docUploadedStatus"
    const val ADDRESS_ADDED = "address_added"


    val returnPolicyList = listOf(
        "No return Policy", "7-Days", "10-Days", "15-Days"
    )

    val warrantyList = listOf(
        "No Warranty",
        "1-Year Warranty",
        "2-Year Warranty",
        "3-Year Warranty",
        "4-Year Warranty",
        "5-Year Warranty",
        "6-Year Warranty",
        "7-Year Warranty",
        "8-Year Warranty",
        "9-Year Warranty",
        "10-Year Warranty"
    )

    val codList = listOf(
        "Yes",
        "No"
    )

    val deliveryCharges = listOf(
        "40",
        "50",
        "60",
        "70",
        "80",
        "90",
        "100"
    )

    // categories & sub categories

    val productCategories = listOf<String>(
        "Electronics",
        "Fashion",
        "Home & Furniture",
        "Beauty & Personal Care",
        "Books & Stationery",
        "Sports,Fitness & Outdoors",
        "Toys,Kids & Baby Products",
        "Groceries & Essentials",
        "Automotive",
        "Health & Nutrition",
        "Gaming",
        "Travel & Luggage",
        "Pet Supplies",
        "Jewelry"
    )

    val electronicsCat = listOf(
        "Mobiles & Accessories",
        "Laptops & Computers",
        "Cameras & Accessories",
        "Televisions",
        "Home Appliances",
        "Headphones & Speakers",
        "Smartwatches & Fitness Gadgets",
        "Power Banks & Chargers",
        "Printers & Scanners"
    )

    val fashionCat = listOf(
        "Men’s Clothing",
        "Women’s Clothing",
        "Footwear (Men & Women)",
        "Watches",
        "Sunglasses & Eyewear",
        "Wallets, and Belts"
    )

    val homeAndFurnitureCat = listOf(
        "Furniture",
        "Home Decor",
        "Lighting & Lamps",
        "Kitchenware",
        "Bedsheets & Curtains",
        "Tools & Hardware"
    )

    val beautyAndPersonalCareCat = listOf(
        "Skincare",
        "Haircare",
        "Makeup",
        "Fragrances & Deodorants"
    )

    val booksAndStationeryCat = listOf(
        "Fiction & Non-Fiction Books",
        "School & Office Supplies",
        "Educational Books",
        "Art & Craft Supplies",
        "Magazines"
    )

    val sportFitnessOutdoorsCat = listOf(
        "Fitness Equipment",
        "Sports Gear & Apparel",
        "Outdoor Adventure Gear",
        "Yoga & Gym Accessories",
        "Bicycles"
    )

    val toysKidsBabyProCat = listOf(
        "Toys & Games",
        "Clothing for Kids & Babies",
        "Baby Care Essentials"
    )

    val groceriesAndEssentialsCat = listOf(
        "Fresh Fruits & Vegetables",
        "Snacks & Beverages",
        "Cooking Essentials",
        "Dairy Products",
        "Cleaning & Household Supplies"
    )

    val automotiveCat = listOf(
        "Car Accessories", "Motorcycle Accessories"
    )

    val healthAndNutritionCat = listOf(
        "Supplements & Proteins",
        "First Aid & Health Devices",
        "Personal Safety Equipment",
        "Healthcare Appliances"
    )

    val gamingCat = listOf(
        "Gaming Consoles", "Video Games", "Gaming Accessories", "PC Gaming Components"
    )

    val travelAndLuggageCat = listOf(
        "Suitcases & Travel Bags", "Backpacks & Rucksacks"
    )

    val petSuppliesCat = listOf(
        "Pet Food", "Pet Toys & Accessories"
    )

    val jewelryCat = listOf(
        "Gold & Diamond Jewelry", "Fashion Jewelry", "Precious & Semi-Precious Stones"
    )
}