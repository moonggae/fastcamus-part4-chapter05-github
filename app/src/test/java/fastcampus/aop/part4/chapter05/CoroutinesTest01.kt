package fastcampus.aop.part4.chapter05

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.system.measureTimeMillis

class CoroutinesTest01 {

    @Test
    fun test01() = runBlocking {
        val time = measureTimeMillis {
            val name = getFirstName()
            val lastName = getLastName()
            println("Hello, $name, $lastName")
        }

        println("measure time : $time")
    }

    @Test
    fun test02() = runBlocking {
        val time = measureTimeMillis {
            val name = async { getFirstName() }
            val lastName = async { getLastName() }
            println("Hello ${name.await()}, ${lastName.await()}")
        }
        println("measure time : $time")
    }

    suspend fun getFirstName() : String {
        delay(1000)
        return "eg"
    }

    suspend fun getLastName() : String{
        delay(1000)
        return "choi"
    }


}