package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val phoneNumber: String,
    val name: String,
    val passwordHash: String,
    val hasDeposited: Boolean = false,
    val depositAmount: Double = 0.0,
    val isVerified: Boolean = false,
    val balance: Double = 0.0,
    val verificationCode: String = ""
)

@Entity(tableName = "shares")
data class ShareEntity(
    @PrimaryKey val symbol: String,
    val name: String,
    val price: Double,
    val priceChangePercent: Double
)

@Entity(tableName = "user_shares", primaryKeys = ["phoneNumber", "symbol"])
data class UserShareEntity(
    val phoneNumber: String,
    val symbol: String,
    val quantity: Int
)

@Entity(tableName = "daily_votes")
data class VoteEntity(
    @PrimaryKey val id: Int,
    val question: String,
    val yesVotes: Int = 0,
    val noVotes: Int = 0
)

@Entity(tableName = "user_votes", primaryKeys = ["phoneNumber", "voteId"])
data class UserVoteEntity(
    val phoneNumber: String,
    val voteId: Int,
    val choice: String // "YES" or "NO"
)
