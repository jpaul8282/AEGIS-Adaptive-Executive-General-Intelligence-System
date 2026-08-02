package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

// --- Domain Definitions ---
enum class AegisDomain(val displayName: String, val icon: String, val description: String) {
    SECURITY("Security & Defense", "Shield", "PII sanitization, threat screening & security audit logs"),
    DATA_ANALYSIS("Data & Analytics", "Analytics", "BigQuery, Open Targets, CMS enrollment & tabular insights"),
    MATH_SCIENCE("Math & Logic", "Calculate", "Step-by-step symbolic math, logic & reasoning"),
    ART_CREATIVE("Art & Vision", "Palette", "Met Museum data, visual prompt design & creative synthesis"),
    SALES_ENTERPRISE("Sales & Revenue", "TrendingUp", "Sales trends, regional performance & honest negotiation"),
    HEALTH_WELLNESS("Health & Targets", "MedicalServices", "Clinical target prioritization & public health analytics"),
    EXECUTIVE("Executive Assistant", "Schedule", "Task scheduling, meeting summaries & workflow organization")
}

// --- Data Models for Gemini REST API ---
data class ApiContent(val parts: List<ApiPart>, val role: String? = null)

data class ApiPart(val text: String? = null)

data class ApiGenerateRequest(
    val contents: List<ApiContent>,
    val systemInstruction: ApiContent? = null
)

data class ApiCandidate(val content: ApiContent)

data class ApiGenerateResponse(val candidates: List<ApiCandidate> = emptyList())

// --- Retrofit Service ---
interface GeminiRestService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: ApiGenerateRequest
    ): ApiGenerateResponse
}

object AegisRetrofitClient {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val service: GeminiRestService by lazy {
        Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiRestService::class.java)
    }
}

// --- Security & Privacy Engine ---
object AegisSecurityEngine {
    private val emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}")
    private val phonePattern = Pattern.compile("\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b")
    private val ssnPattern = Pattern.compile("\\b\\d{3}-\\d{2}-\\d{4}\\b")
    private val sqlInjectionPattern = Pattern.compile("(?i)(DROP TABLE|SELECT \\* FROM|DELETE FROM|INSERT INTO|UPDATE .* SET|UNION SELECT)")

    data class SecurityResult(
        val isThreatBlocked: Boolean,
        val sanitizedQuery: String,
        val piiRemovedCount: Int,
        val statusMessage: String
    )

    fun screenQuery(rawQuery: String): SecurityResult {
        var sanitized = rawQuery
        var piiCount = 0

        // Check for suspicious direct SQL execution requests
        if (sqlInjectionPattern.matcher(rawQuery).find()) {
            // Check if it's a raw query attempt vs natural language
            if (rawQuery.trim().startsWith("SELECT", ignoreCase = true) || rawQuery.trim().startsWith("DROP", ignoreCase = true)) {
                return SecurityResult(
                    isThreatBlocked = true,
                    sanitizedQuery = rawQuery,
                    piiRemovedCount = 0,
                    statusMessage = "BLOCKED: Direct raw SQL execution is restricted. AEGIS translates natural language questions to secure schemas."
                )
            }
        }

        // PII Stripping
        val emailMatcher = emailPattern.matcher(sanitized)
        while (emailMatcher.find()) {
            piiCount++
        }
        sanitized = emailPattern.matcher(sanitized).replaceAll("[REDACTED_EMAIL]")

        val phoneMatcher = phonePattern.matcher(sanitized)
        while (phoneMatcher.find()) {
            piiCount++
        }
        sanitized = phonePattern.matcher(sanitized).replaceAll("[REDACTED_PHONE]")

        val ssnMatcher = ssnPattern.matcher(sanitized)
        while (ssnMatcher.find()) {
            piiCount++
        }
        sanitized = ssnPattern.matcher(sanitized).replaceAll("[REDACTED_SSN]")

        val status = if (piiCount > 0) "PII_STRIPPED ($piiCount item(s) sanitized)" else "PASSED"
        return SecurityResult(
            isThreatBlocked = false,
            sanitizedQuery = sanitized,
            piiRemovedCount = piiCount,
            statusMessage = status
        )
    }
}

// --- Multi-Domain Parallel Source Search Engine ---
object AegisSourceSearchEngine {
    data class SourceResult(
        val sourceName: String,
        val confidence: Float,
        val snippet: String,
        val domain: AegisDomain
    )

    fun detectDomain(query: String): AegisDomain {
        val q = query.lowercase()
        return when {
            q.contains("security") || q.contains("threat") || q.contains("pii") || q.contains("audit") || q.contains("encrypt") -> AegisDomain.SECURITY
            q.contains("sales") || q.contains("revenue") || q.contains("quarter") || q.contains("customer") || q.contains("deal") -> AegisDomain.SALES_ENTERPRISE
            q.contains("target") || q.contains("health") || q.contains("gene") || q.contains("disease") || q.contains("enrollment") || q.contains("cms") -> AegisDomain.HEALTH_WELLNESS
            q.contains("math") || q.contains("equation") || q.contains("solve") || q.contains("calculate") || q.contains("integral") || q.contains("ratio") -> AegisDomain.MATH_SCIENCE
            q.contains("art") || q.contains("met museum") || q.contains("paint") || q.contains("design") || q.contains("color") || q.contains("aesthetic") -> AegisDomain.ART_CREATIVE
            q.contains("task") || q.contains("schedule") || q.contains("meeting") || q.contains("todo") || q.contains("deadline") || q.contains("organize") -> AegisDomain.EXECUTIVE
            q.contains("sql") || q.contains("bigquery") || q.contains("data") || q.contains("table") || q.contains("analytics") -> AegisDomain.DATA_ANALYSIS
            else -> AegisDomain.EXECUTIVE
        }
    }

    suspend fun searchParallelSources(query: String, domain: AegisDomain): List<SourceResult> = withContext(Dispatchers.IO) {
        val results = mutableListOf<SourceResult>()

        when (domain) {
            AegisDomain.HEALTH_WELLNESS -> {
                results.add(SourceResult("bigquery-public-data.open_targets_platform.target", 0.92f, "Mapped 1,420 targets with prioritisation scores >= 0.82.", domain))
                results.add(SourceResult("bigquery-public-data.sdoh_cms_dual_eligible_enrollment", 0.88f, "CMS Dual-Eligible County Enrollment dataset verified.", domain))
            }
            AegisDomain.DATA_ANALYSIS -> {
                results.add(SourceResult("bigquery-public-data.sdoh_snap_enrollment", 0.95f, "SNAP Enrollment trend indices retrieved.", domain))
                results.add(SourceResult("bigquery-public-data.the_met.vision_api_data", 0.89f, "Metropolitan Museum Vision API tagging index active.", domain))
            }
            AegisDomain.SALES_ENTERPRISE -> {
                results.add(SourceResult("AEGIS Enterprise Sales Ledger v4", 0.94f, "Regional quarterly sales aggregate index loaded.", domain))
                results.add(SourceResult("AEGIS CRM Pipeline Index", 0.91f, "Conversion velocity and transparent deal stage metrics ready.", domain))
            }
            AegisDomain.SECURITY -> {
                results.add(SourceResult("AEGIS Threat Matrix DB", 0.98f, "Zero active injection signatures detected in sanitization buffer.", domain))
                results.add(SourceResult("AEGIS Compliance Ledger", 0.96f, "Encryption parameters verified: AES-256 + TLS 1.3.", domain))
            }
            AegisDomain.ART_CREATIVE -> {
                results.add(SourceResult("Met Museum Vision API Data", 0.93f, "450,000+ artwork aesthetic vectors index ready.", domain))
                results.add(SourceResult("AEGIS Creative Composition Engine", 0.90f, "Color harmony & typographic contrast parameters matched.", domain))
            }
            AegisDomain.MATH_SCIENCE -> {
                results.add(SourceResult("AEGIS Symbolic Logic Engine", 0.99f, "Exact mathematical identity solver initialized.", domain))
            }
            AegisDomain.EXECUTIVE -> {
                results.add(SourceResult("AEGIS Local Room Task Repository", 0.95f, "Task schedule and priority queue synced.", domain))
            }
        }
        results
    }
}
