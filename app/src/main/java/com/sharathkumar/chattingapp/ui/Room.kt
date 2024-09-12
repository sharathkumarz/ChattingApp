package com.sharathkumar.chattingapp.ui

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.room.Upsert
import java.sql.Timestamp

@Dao
interface MessageDao{
    @Query("SELECT * FROM ChattingApp WHERE sender = :senderId AND receiver = :receiverId OR sender = :receiverId AND receiver = :senderId")
    suspend fun getUserChat(senderId: String,receiverId: String):List<Message1>

    @Query("DELETE FROM ChattingApp WHERE sender = :senderId AND receiver = :receiverId OR sender = :receiverId AND receiver = :senderId")
    suspend fun deleteUserChat(senderId: String,receiverId: String)

    @Query("DELETE FROM ChattingApp WHERE  messageId = :messageId")
    suspend fun deleteMessage(messageId: Int)

    @Query("SELECT * FROM ChattingApp")
    suspend fun getAllChat():List<Message1>

    @Insert
    suspend fun insertMessage(vararg messages: Message1)

}


@Dao
interface ContactDao{

    @Query("SELECT * FROM contact")
    suspend fun getAllContacts():List<Contact>

    @Query("DELETE FROM contact WHERE phone = :userId")
    suspend fun deleteContact(userId : String)

    @Insert
    suspend fun insertContact(vararg contact: Contact)

    @Update
    suspend fun updateContact(vararg contact: Contact)

}

@Dao
interface UserdataDao {

    @Query("SELECT * FROM userdata WHERE id = 1")
    suspend fun getUserProfile(): UserProfile?  // Returns the single profile, or null if not present

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(userProfile: UserProfile)  // Replaces the existing profile
}


@Database(entities = [Message1::class,Contact::class,UserProfile::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun messageDao() : MessageDao
    abstract fun contactDao() : ContactDao
    abstract fun userDao() : UserdataDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ChatsApp"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}