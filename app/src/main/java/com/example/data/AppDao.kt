package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // Users
    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber")
    suspend fun getUser(phoneNumber: String): UserEntity?

    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber")
    fun getUserFlow(phoneNumber: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    // Shares
    @Query("SELECT * FROM shares")
    fun getAllSharesFlow(): Flow<List<ShareEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShares(shares: List<ShareEntity>)

    @Update
    suspend fun updateShare(share: ShareEntity)

    // User Shares (Holdings)
    @Query("SELECT * FROM user_shares WHERE phoneNumber = :phoneNumber")
    fun getUserSharesFlow(phoneNumber: String): Flow<List<UserShareEntity>>

    @Query("SELECT * FROM user_shares WHERE phoneNumber = :phoneNumber AND symbol = :symbol")
    suspend fun getUserShare(phoneNumber: String, symbol: String): UserShareEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserShare(userShare: UserShareEntity)

    @Update
    suspend fun updateUserShare(userShare: UserShareEntity)

    @Delete
    suspend fun deleteUserShare(userShare: UserShareEntity)

    // Daily Votes
    @Query("SELECT * FROM daily_votes")
    fun getAllVotesFlow(): Flow<List<VoteEntity>>

    @Query("SELECT * FROM daily_votes WHERE id = :id")
    suspend fun getVote(id: Int): VoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVotes(votes: List<VoteEntity>)

    @Update
    suspend fun updateVote(vote: VoteEntity)

    // User Votes Tracking
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserVote(userVote: UserVoteEntity)

    @Query("SELECT * FROM user_votes WHERE phoneNumber = :phoneNumber")
    fun getUserVotesFlow(phoneNumber: String): Flow<List<UserVoteEntity>>

    @Query("SELECT * FROM user_votes WHERE phoneNumber = :phoneNumber AND voteId = :voteId")
    suspend fun getUserVote(phoneNumber: String, voteId: Int): UserVoteEntity?
}
