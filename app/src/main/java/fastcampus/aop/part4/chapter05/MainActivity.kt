package fastcampus.aop.part4.chapter05

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isGone
import fastcampus.aop.part4.chapter05.databinding.ActivityMainBinding
import fastcampus.aop.part4.chapter05.utility.AuthTokenProvider
import fastcampus.aop.part4.chapter05.utility.RetrofitUtil
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private val TAG = "로그"

    private lateinit var binding: ActivityMainBinding

    private val authTokenProvider: AuthTokenProvider by lazy {
        AuthTokenProvider(this)
    }

    val job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() = with(binding) {
        loginButton.setOnClickListener {
            loginGithub()
        }
    }

    // note : https://github.com/login/oauth/authorize?client_id=###
    private fun loginGithub() {
        val loginUri = Uri.Builder().scheme("https").authority("github.com")
            .appendPath("login")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
            .build()

        CustomTabsIntent.Builder().build().also {
            it.launchUrl(this, loginUri)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.e(TAG, "onNewIntent: ")
        intent?.data?.getQueryParameter("code")?.let {
            // todo getAccessToken
            launch {
                showProgress()
                getAccessToken(it)
                dismissProgress()
            }
        }
    }

    private suspend fun showProgress() = withContext(coroutineContext) {
        with(binding) {
            loginButton.isGone = true
            progressBar.isGone = false
            progressTextView.isGone = false
        }
    }

    private suspend fun dismissProgress() = withContext(coroutineContext) {
        with(binding) {
            loginButton.isGone = false
            progressBar.isGone = true
            progressTextView.isGone = true
        }
    }

    private suspend fun getAccessToken(code: String) = withContext(Dispatchers.IO) {
        val response = RetrofitUtil.authApiService.getAccessToken(
            clientId = BuildConfig.GITHUB_CLIENT_ID,
            clientSecret = BuildConfig.GITHUB_CLIENT_SECRET,
            code = code
        )

        if (response.isSuccessful) {
            val accessToken = response.body()?.accessToken ?: ""
            Log.e(TAG, "getAccessToken: ${accessToken}")
            if (accessToken.isNotEmpty()) {
                authTokenProvider.updateToken(accessToken)
            } else {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "accessToken이 존재하지 않습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}