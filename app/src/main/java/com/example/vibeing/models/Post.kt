package com.example.vibeing.models

data class Post(val postUrl: String = "", val postCaption: String = "", val postedBy: String = "", val postVisibility: Int = 0, val postTime: Long = 0) {
}