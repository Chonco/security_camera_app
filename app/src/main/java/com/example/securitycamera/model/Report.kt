package com.example.securitycamera.model

import java.io.Serializable
import java.util.Date

class Report(
    var createdAt: Date,
    var byUser: Boolean,
    var imageUrl: String
): Serializable