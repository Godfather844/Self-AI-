package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    fun getActiveUserFlow(): Flow<UserEntity?>

    @Query("SELECT * FROM users ORDER BY xp DESC")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getActiveUser(): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun logoutAllUsers()

    @Query("UPDATE users SET isLoggedIn = 1 WHERE email = :email")
    suspend fun setLoggedInUser(email: String)

    @Query("UPDATE users SET xp = xp + :addedXp, totalQuestsSolved = totalQuestsSolved + 1, currentStreak = currentStreak + :streakDelta WHERE email = :email")
    suspend fun incrementUserStats(email: String, addedXp: Int, streakDelta: Int = 1)

    @Query("UPDATE users SET heartsRemaining = :hearts WHERE email = :email")
    suspend fun updateHearts(email: String, hearts: Int)

    @Query("DELETE FROM users WHERE email = 'aarav.cs26@college.edu' OR name LIKE '%Aarav%'")
    suspend fun deleteDummySeedUser()

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}

@Dao
interface QuestHistoryDao {

    @Query("SELECT * FROM quest_history WHERE userEmail = :email ORDER BY completedAt DESC")
    fun getQuestHistoryForUser(email: String): Flow<List<QuestHistoryEntity>>

    @Insert
    suspend fun insertQuestRecord(record: QuestHistoryEntity)
}
