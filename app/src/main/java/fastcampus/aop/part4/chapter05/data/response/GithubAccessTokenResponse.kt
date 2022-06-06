package fastcampus.aop.part4.chapter05.data.response

data class GithubAccessTokenResponse(
    val accessToken: String,
    val scope: String,
    val tokenType: String
)
