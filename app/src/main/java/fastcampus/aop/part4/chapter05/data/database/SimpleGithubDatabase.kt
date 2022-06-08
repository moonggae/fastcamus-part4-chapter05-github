package fastcampus.aop.part4.chapter05.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import fastcampus.aop.part4.chapter05.data.dao.RepositoryDao
import fastcampus.aop.part4.chapter05.data.entity.GithubRepoEntity

@Database(entities = [GithubRepoEntity::class], version = 1)
abstract class SimpleGithubDatabase : RoomDatabase(){
    abstract fun repositoryDao() : RepositoryDao


}