package com.itcomca.testfactum.views.camerafragment

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.itcomca.testfactum.base.BaseViewModel

class CameraViewModel: BaseViewModel() {
    var storage = Firebase.storage
    var storageRef = storage.reference
    var imagesRef: StorageReference? = storageRef.child("images")
    var spaceRef= storageRef.child("images/space.jpg")

}