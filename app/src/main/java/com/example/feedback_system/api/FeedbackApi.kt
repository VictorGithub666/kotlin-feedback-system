package com.example.feedback_system.api

import com.example.feedback_system.models.Question
import com.example.feedback_system.models.Trainer
import com.example.feedback_system.models.Module
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


data class LoginResponse(
    val token: String,
    val user: UserResponse
)

data class UserResponse(
    val email: String,
    val username: String,
    val role: Int
)

interface FeedbackApi {
    @POST("/api/auth/register")
    fun register(@Body body: Map<String, String>): Call<Any>

    @POST("/api/auth/login")
    fun login(@Body body: Map<String, String>): Call<Map<String, Any>>

    @POST("/api/admin/adminregister")
    fun adminRegister(@Body body: Map<String, String>): Call<Any>

    @POST("/api/admin/adminlogin")
    fun adminLogin(@Body body: Map<String, String>): Call<Any>

    @GET("/api/questions/select-questions")
    fun getQuestions(): Call<List<Question>>

    @POST("/api/feedback/add-feedback")
    fun submitFeedback(@Body body: Map<String, @JvmSuppressWildcards Any>): Call<Any>

    @GET("/api/auth/users")
    fun getUsers(): Call<List<Map<String, Any>>>

    @GET("/api/feedback/get-overall-performance")
    fun getOverallPerformance(): Call<List<Map<String, Any>>>

    @GET("/api/feedback/get-trainer-knowledge")
    fun getTrainerKnowledge(): Call<List<Map<String, Any>>>

    @GET("/api/feedback/get-trainer-handson")
    fun getTrainerHandson(): Call<List<Map<String, Any>>>

    @GET("/api/feedback/get-trainer-assistance")
    fun getTrainerAssistance(): Call<List<Map<String, Any>>>

    @GET("/api/feedback/get-module-content")
    fun getModuleContent(): Call<List<Map<String, Any>>>

    @GET("/api/trainer/get-trainers")
    fun getTrainers(): Call<List<Trainer>>

    @GET("/api/feedback/get-trainer-punctuality")
    fun getTrainerPunctuality(): Call<List<Map<String, Any>>>

    @GET("/api/feedback/get-overall-trainer-interaction")
    fun getTrainerInteraction(): Call<List<Map<String, Any>>>

    @GET("/api/feedback/get-module-understanding")
    fun getModuleUnderstanding(): Call<List<Map<String, Any>>>

    @GET("/api/feedback/get-trainer-overall")
    fun getTrainerOverall(): Call<List<Map<String, Any>>>

    @POST("/api/module/create-module")
    fun createModule(@Body body: Map<String, String>): Call<Module>

    @GET("/api/module/get-modules")
    fun getModules(): Call<List<Module>>

    @DELETE("/api/module/delete-module/{id}")
    fun deleteModule(@Path("id") id: Int): Call<Any>

    @GET("/api/questions/get-all-questions")
    fun getAllQuestions(): Call<List<Question>>

    @GET("/api/questions/get-question/{id}")
    fun getQuestion(@Path("id") id: Int): Call<Question>

    @POST("/api/questions/create-question")
    fun createQuestion(@Body body: Map<String, @JvmSuppressWildcards Any>): Call<Question>

    @PUT("/api/questions/update-question/{id}")
    fun updateQuestion(@Path("id") id: Int, @Body body: Map<String, @JvmSuppressWildcards Any>): Call<Question>

    @DELETE("/api/questions/delete-question/{id}")
    fun deleteQuestion(@Path("id") id: Int): Call<Void>
}

object ApiClient {
    private const val BASE_URL = "http://192.168.156.247:4000"

    val apiService: FeedbackApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FeedbackApi::class.java)
    }
}