package com.pdinc.chitchat.Modals

import com.google.firebase.firestore.FieldValue

data class User(
    val name:String, val imageUrl:String, val thumbImage:String,
    val deviceToken:String, val status:String, val onlineStatus: String, val uid:String){
    //Empty constructor for firebase
    constructor():this("","","","","", "","")

    constructor(name:String,imageUrl: String,thumbImage: String,uid: String): this(
        name, imageUrl, thumbImage, "", "Hey there", "", uid
    )
}
