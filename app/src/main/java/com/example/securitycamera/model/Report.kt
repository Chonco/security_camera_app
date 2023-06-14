package com.example.securitycamera.model

import java.io.Serializable

class Report(
    var createdAt: String? = null,
    var byUser: Boolean? = null,
    var imageURL: String? = null
): Serializable