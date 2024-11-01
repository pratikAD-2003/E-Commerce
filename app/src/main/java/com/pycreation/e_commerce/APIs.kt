package com.pycreation.e_commerce

object APIs {
    const val BASE_URL: String = "http://192.168.96.145:3030"

    const val REGISTER_USER = "/registerUser"
    const val VERIFY_EMAIL = "/verify-email"
    const val LOGIN_USER = "/loginUser"
    const val FORGET_PASSWORD = "/forgetPassword"
    const val VERIFY_EMAIL_FORGET_PASSWORD = "/verify-email-forget-password"

    const val UPDATE_DOCUMENT_STATUS = "/update-documentStatus"
    const val UPLOAD_PARTNER_DETAILS = "/uploadPartnerDetails"
    const val UPDATE_ADDRESS_STATUS = "/update-address-added-status"

    const val FILTER_PRODUCTS = "/product/filter"

    // seller side
    const val POST_PRODUCT = "/postProductBySeller"
    const val GET_ALL_PRODUCTS = "/getAllProducts"
    const val GET_BY_CATEGORIES = "/getProductsByCategory/{category}" // input category
    const val GET_BY_SUB_CATEGORIES = "/getProductsBySubCategory/{category}" // input category
    const val GET_BY_SELLER_UID = "/getProductsBySellerId/{sellerUid}" // input sellerUid

    const val POST_REVIEW_BY_CONSUMER = "/postReviewByConsumer"
    const val GET_REVIEW_BY_PRODUCT_ID = "/getReviewByProductId/{productId}" // input productId
    const val GET_REVIEW_BY_CONSUMER_ID = "/getReviewByConsumer"
    const val GET_ALL_CONSUMER_REVIEWS = "/getAllConsumerReviews/{reviewerId}" // get

    const val GET_PRODUCT_SPECIFICATIONS = "/getProductSpecificationByProductUid/{productUid}"
    const val POST_PRODUCT_SPECIFICATIONS = "/postProductSpecifications"

    const val GET_PRODUCT_BY_PRODUCT_ID = "/getProductByProductId/{productUid}" // get

    const val ADD_TO_WISHLIST_PRODUCT = "/savedProductInWishList" // post
    const val CHECK_PRODUCT_IS_IN_WISHLIST = "/checkProductPresentInWishlist"
    const val DELETE_PRODUCT_FROM_WISHLIST = "/deleteProductFromWishList" // delete
    const val GET_WISHLIST_ITEMS_BY_USER_ID = "/getWishListItemsByUserId/{userId}" // get

    const val ADD_TO_CART_PRODUCT = "/cart/addToCart" // post
    const val GET_CART_PRODUCTS = "/cart/getCartWithProductDetails/{userId}" //get
    const val DELETE_CART_ITEM = "/cart/deleteFromCart"
    const val CHECK_PRODUCT_ALREADY_IN_CART = "/cart/checkProductAlreadyInCart" //post
    const val PLACED_ORDER = "/cart/checkoutAndPay" // post

    const val ADD_ADDRESS = "/addAddress" // post
    const val GET_ADDRESSES = "/getAddress/{userId}" // get
    const val REMOVE_ADDRESS = "/removeAddress" // post
    const val UPDATE_ADDRESS = "/updateAddress" //post

    const val INCREASE_QUANTITY = "/cart/increaseQuantity"
    const val DECREASE_QUANTITY = "/cart/decreaseQuantity"

    const val GET_MY_ORDERS = "/cart/getOrderProducts/{userId}"

    const val GET_ORDER_BY_ORDER_ID = "/cart/getOrderByOrderId"
}