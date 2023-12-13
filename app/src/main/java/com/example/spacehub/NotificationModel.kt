package com.example.spacehub

data class Notification(
    val messageID: String,
    val messageBody: String,
    val messageType: String,
    val messageIssueTime: String,
    val messageId: Int,
    val messageURL: String
)
