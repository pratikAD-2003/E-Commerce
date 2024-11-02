package com.pycreation.e_commerce.retrofit

import com.pycreation.e_commerce.APIs
import com.pycreation.e_commerce.admin.models.UpdateAddressStatusModel
import com.pycreation.e_commerce.admin.models.UpdateDocStatusModel
import com.pycreation.e_commerce.admin.models.productModel.Product
import com.pycreation.e_commerce.admin.models.productModel.ProductModel
import com.pycreation.e_commerce.admin.models.productSpecifications.ProductSpecificationsItem
import com.pycreation.e_commerce.common.models.ForgetPassBodyModel
import com.pycreation.e_commerce.common.reviews.models.GetReviewByConReqModel
import com.pycreation.e_commerce.common.reviews.models.PostReviewReqModel
import com.pycreation.e_commerce.common.reviews.models.ReviewModelItem
import com.pycreation.e_commerce.common.reviews.models.ReviewWIthDetailsResModel
import com.pycreation.e_commerce.consumer.addToCart.request.AddToCartResModel
import com.pycreation.e_commerce.consumer.addToCart.request.PlacedOrderReqModel
import com.pycreation.e_commerce.consumer.addToCart.response.cartresmodel.CartItemResModel
import com.pycreation.e_commerce.consumer.address.model.req.PostAddressReqModel
import com.pycreation.e_commerce.consumer.address.model.req.RemoveAddressModel
import com.pycreation.e_commerce.consumer.address.model.req.UpdateAddressReqModel
import com.pycreation.e_commerce.consumer.address.model.res.AddressListModelResModel
import com.pycreation.e_commerce.consumer.dashboard.sub_category.res.SubCategoryResModel
import com.pycreation.e_commerce.consumer.models.User
import com.pycreation.e_commerce.consumer.models.UserOtpModel
import com.pycreation.e_commerce.consumer.orders.req.OrderByOrderIdReqModel
import com.pycreation.e_commerce.consumer.orders.res.Order
import com.pycreation.e_commerce.consumer.orders.res.OrderListModelRes
import com.pycreation.e_commerce.consumer.responseModels.LoginResModel
import com.pycreation.e_commerce.consumer.responseModels.RegisterResponse
import com.pycreation.e_commerce.consumer.responseModels.VerifyEmailResponseModel
import com.pycreation.e_commerce.consumer.wishlist.models.response.WishListItemResModel
import com.pycreation.e_commerce.consumer.wishlist.models.request.AddToWishListRequestModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST(APIs.REGISTER_USER)
    fun registerUser(@Body user: User?): Call<RegisterResponse?>?

    @POST(APIs.VERIFY_EMAIL)
    fun verifyEmail(@Body userOtpModel: UserOtpModel?): Call<VerifyEmailResponseModel?>?

    @POST(APIs.LOGIN_USER)
    fun loginUser(@Body user: User?): Call<LoginResModel?>?

    @POST("/logout")
    fun logoutUser(): Call<Void?>?

    @POST(APIs.FORGET_PASSWORD)
    fun forgetPassword(@Body forgetPassBodyModel: ForgetPassBodyModel?): Call<RegisterResponse?>?

    @PUT(APIs.VERIFY_EMAIL_FORGET_PASSWORD)
    fun verifyEmailForgetPassword(@Body userOtpModel: UserOtpModel?): Call<RegisterResponse?>?

    @PUT(APIs.UPDATE_DOCUMENT_STATUS)
    fun updateDocStatus(@Body updateDocStatusModel: UpdateDocStatusModel?): Call<RegisterResponse?>?

    @PUT(APIs.UPDATE_ADDRESS_STATUS)
    fun updateDocStatus(@Body updateAddressStatusModel: UpdateAddressStatusModel?): Call<RegisterResponse?>?

    @Multipart
    @POST(APIs.UPLOAD_PARTNER_DETAILS)
    fun uploadPartnerDetails(
        @Part("fullName") fullName: RequestBody,
        @Part("phoneNumber") phoneNumber: RequestBody,
        @Part("businessName") businessName: RequestBody,
        @Part("businessRegiNum") businessRegiNum: RequestBody,
        @Part("gstin") gstin: RequestBody,
        @Part("businessAddress") businessAddress: RequestBody,
        @Part("bankAccHolderName") bankAccHolderName: RequestBody,
        @Part("bankAccNumber") bankAccNumber: RequestBody,
        @Part("bankName") bankName: RequestBody,
        @Part("ifsc") ifsc: RequestBody,
        @Part issuedIdPicture: MultipartBody.Part,
        @Part businessRegiDocPicture: MultipartBody.Part
    ): Call<RegisterResponse?>?


    @Multipart
    @POST(APIs.POST_PRODUCT)
    fun postProduct(
        @Part("productName") productName: RequestBody,
        @Part("productDescription") productDescription: RequestBody,
        @Part("productPrice") productPrice: RequestBody,
        @Part("productSellingPrice") productSellingPrice: RequestBody,
        @Part("brand") brand: RequestBody,
        @Part("returnPolicy") returnPolicy: RequestBody,
        @Part("productCategory") productCategory: RequestBody,
        @Part("subCategory") subCategory: RequestBody,
        @Part("availableStock") availableStock: RequestBody,
        @Part("businessName") businessName: RequestBody,
        @Part("sellerUid") sellerUid: RequestBody,
        @Part("warranty") warranty: RequestBody, // Add warranty
        @Part("tags") tags: RequestBody,
        @Part("cashOnDelivery") cashOnDelivery: RequestBody,
        @Part("freeDelivery") freeDelivery: RequestBody,
        @Part("deliveryCharges") deliveryCharges: RequestBody,
        @Part productImages: List<MultipartBody.Part>
    ): Call<RegisterResponse?>?

    @GET(APIs.GET_BY_CATEGORIES)
    fun getProductsByCategory(@Path("category") category: String): Call<ProductModel>

    @GET(APIs.GET_BY_SELLER_UID)
    fun getProductsBySellerId(@Path("sellerUid") sellerUid: String): Call<ProductModel>

    @GET(APIs.GET_BY_SUB_CATEGORIES)
    fun getProductsBySubCategory(@Path("category") category: String): Call<ProductModel>

    @GET(APIs.GET_PRODUCT_SPECIFICATIONS)
    fun getProductSpecificationByUid(@Path("productUid") productUid: String): Call<List<ProductSpecificationsItem>>

    @POST(APIs.POST_PRODUCT_SPECIFICATIONS)
    fun postProductSpecsByUid(@Body productSpecificationsItem: ProductSpecificationsItem): Call<RegisterResponse?>?

    //reviews
    @GET(APIs.GET_REVIEW_BY_PRODUCT_ID)
    fun getReviewByProductId(@Path("productId") productId: String): Call<List<ReviewModelItem>?>?

    @POST(APIs.POST_REVIEW_BY_CONSUMER)
    fun postReviewByConsumer(@Body postReviewReqModel: PostReviewReqModel): Call<RegisterResponse?>?

    @POST(APIs.GET_REVIEW_BY_CONSUMER_ID)
    fun getReviewByConsumerId(@Body getReviewByConReqModel: GetReviewByConReqModel): Call<List<ReviewModelItem?>?>

    @GET(APIs.GET_ALL_CONSUMER_REVIEWS)
    fun getAllConsumerReviews(@Path("reviewerId") reviewerId: String): Call<ReviewWIthDetailsResModel?>

    @GET(APIs.FILTER_PRODUCTS)
    fun getProductsByFilter(
        @Query("sortByPrice") sortByPrice: String? = null,  // Optional: 'asc' or 'desc'
        @Query("sortByRatings") sortByRatings: String? = null,  // Optional: 'asc' or 'desc'
        @Query("sortByTimestamp") sortByTimestamp: String? = null,  // Optional: 'asc' or 'desc'
        @Query("brand") brand: String? = null,                 // Optional: brand filter
        @Query("search") search: String? = null,               // Optional: search keyword
        @Query("minPrice") minPrice: Int? = null,              // Optional: minimum price
        @Query("maxPrice") maxPrice: Int? = null,              // Optional: maximum price
        @Query("minRating") minRating: Int? = null,            // Optional: minimum rating
        @Query("maxRating") maxRating: Int? = null,            // Optional: maximum rating
        @Query("productCategory") productCategory: String? = null,               // Optional: productCategory keyword
        @Query("subCategory") subCategory: String? = null,               // Optional: subCategory keyword
        @Query("limit") limit: Int? = null,                   // Pagination: number of products
        @Query("page") page: Int? = null
    ): Call<List<Product>>

    @GET(APIs.GET_PRODUCT_BY_PRODUCT_ID)
    fun getProductByProductUid(@Path("productUid") productUid: String): Call<ProductModel>

    @POST(APIs.ADD_TO_WISHLIST_PRODUCT)
    fun addToWishListProduct(@Body addToWishListRequestModel: AddToWishListRequestModel): Call<RegisterResponse?>?

    @GET(APIs.GET_WISHLIST_ITEMS_BY_USER_ID)
    fun getWishListProducts(@Path("userId") userId: String): Call<WishListItemResModel?>?

//    @DELETE(APIs.DELETE_PRODUCT_FROM_WISHLIST)
//    fun removeProductFromWishList(@Body addToWishListRequestModel: AddToWishListRequestModel): Call<RegisterResponse?>?

    @POST(APIs.CHECK_PRODUCT_IS_IN_WISHLIST)
    fun checkProductPresentInWishlist(@Body addToWishListRequestModel: AddToWishListRequestModel): Call<RegisterResponse?>?

    @POST(APIs.ADD_TO_CART_PRODUCT)
    fun addToCartProduct(@Body addToCartResModel: AddToCartResModel): Call<RegisterResponse?>?

    @GET(APIs.GET_CART_PRODUCTS)
    fun getCartProducts(@Path("userId") userId: String): Call<CartItemResModel?>?

    @POST(APIs.PLACED_ORDER)
    fun placedOrderProducts(@Body placedOrderReqModel: PlacedOrderReqModel): Call<RegisterResponse?>?

    @POST(APIs.DELETE_CART_ITEM)
    fun deleteCartProduct(@Body addToWishListRequestModel: AddToWishListRequestModel): Call<RegisterResponse?>?

    @POST(APIs.CHECK_PRODUCT_ALREADY_IN_CART)
    fun checkProductInCart(@Body addToWishListRequestModel: AddToWishListRequestModel): Call<RegisterResponse?>?

    @POST(APIs.ADD_ADDRESS)
    fun addAddress(@Body postAddressReqModel: PostAddressReqModel): Call<RegisterResponse?>?

    @GET(APIs.GET_ADDRESSES)
    fun getAddresses(@Path("userId") userId: String): Call<AddressListModelResModel?>

    @POST(APIs.REMOVE_ADDRESS)
    fun removeAddress(@Body removeAddressModel: RemoveAddressModel): Call<RegisterResponse?>?

    @POST(APIs.UPDATE_ADDRESS)
    fun updateAddress(@Body updateAddressReqModel: UpdateAddressReqModel): Call<RegisterResponse?>?

    @POST(APIs.INCREASE_QUANTITY)
    fun increaseQuantity(@Body addToWishListRequestModel: AddToWishListRequestModel): Call<RegisterResponse?>?

    @POST(APIs.DECREASE_QUANTITY)
    fun decreaseQuantity(@Body addToWishListRequestModel: AddToWishListRequestModel): Call<RegisterResponse?>?

    @GET(APIs.GET_MY_ORDERS)
    fun getOrderedProducts(@Path("userId") userId: String): Call<OrderListModelRes?>?

    @POST(APIs.GET_ORDER_BY_ORDER_ID)
    fun getOrderByOrderId(@Body orderByOrderIdReqModel: OrderByOrderIdReqModel): Call<OrderListModelRes?>?

    @GET(APIs.GET_SUB_CATEGORIES)
    fun getSubCategories(@Path("category") category: String): Call<SubCategoryResModel?>?
}