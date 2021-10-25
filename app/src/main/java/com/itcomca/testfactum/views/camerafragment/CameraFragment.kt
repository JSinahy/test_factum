package com.itcomca.testfactum.views.camerafragment


import android.Manifest
import android.R.attr.*
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.itcomca.testfactum.R
import com.itcomca.testfactum.base.BaseApplication
import com.itcomca.testfactum.databinding.FragmentCameraBinding
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Environment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.*
import android.app.ProgressDialog
import android.graphics.BitmapFactory
import android.os.FileUtils
import android.util.Log
import androidx.core.content.FileProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.itcomca.testfactum.repositories.models.ImageStorageModel
import com.itcomca.testfactum.views.camerafragment.adapters.ImagesStorageAdapter
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CameraFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CameraFragment : Fragment() {

    lateinit var binding: FragmentCameraBinding

    private val CAMERA_REQUEST = 1888
    private val CAMERA_SELECT_REQUEST = 2000
    private val MY_CAMERA_PERMISSION_CODE = 100

    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null

    lateinit var mActivity:Activity

    lateinit var destinationFile: File
    val photoFileName = "photo.jpg"
    var photoFile: File? = null
    val APP_TAG = "TESTFACTUM"
    var isSelect: Boolean = false

    lateinit var progressDialog: ProgressDialog
    var model:ArrayList<ImageStorageModel> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun Init(){
        /** Se inicializan los objetos para manejar el FIreStore **/
        progressDialog = ProgressDialog(activity)
        progressDialog.setCancelable(false)
        mActivity = requireActivity()
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference
        GetListImages()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false)
        Init()
        SetupListener()

        return binding.root
    }

    /** Listener click al momento de dar click en Capturar o Elegir y se solicitan permisos **/
    fun SetupListener(){
        binding.btnCapture.setOnClickListener {
            isSelect = false
            /** Se inicializan la soliictud de permisos, solo se solicita la CAMARA **/
            if(ContextCompat.checkSelfPermission(BaseApplication.applicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.CAMERA), MY_CAMERA_PERMISSION_CODE);
            }else{
                /**
                 * En caso de ya tener el permiso se manda a abrir la aplicacion de camara
                 * Para su funcionamiento en caso de Android 11 se tiene que crear un FILE PROVIDER
                 * Y se tiene que agregar un query para que la palicacion pueda tener acceso y compartir
                 * la informacion entre la aplicacion de Camara y mi app que se esta desarrollando
                 * **/
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                photoFile = getPhotoFileUri(photoFileName)

                /** Si el archivo devuelto no es null, se continua con el proceso quiere decir que si se creo la ruta exitosamente **/
                if (photoFile != null) {
                    /** Se obtiene la ruta desde la aplicacion de Camara mediante el FileProvider creado **/
                    val fileProvider: Uri = FileProvider.getUriForFile(BaseApplication.applicationContext(), "com.itcomca.testfactum.fileprovider", photoFile!!)

                    /** Se setea el extraput para la salida del archivo desde la app de camara hasta la app que la manda a llamar **/
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

                    /** Se manda a llamar la camara **/
                    if (cameraIntent.resolveActivity(BaseApplication.applicationContext().packageManager) != null) {
                        startActivityForResult(cameraIntent, CAMERA_REQUEST)
                    }
                }
            }
        }
        binding.btnSelect.setOnClickListener {
            isSelect = true
            val mIntent = Intent(Intent.ACTION_PICK)
            mIntent.setType("image/*")
            mIntent.setAction(Intent.ACTION_GET_CONTENT);
            mIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(
                Intent.createChooser(
                    mIntent,
                    "Seleccione una imagen"
                ), CAMERA_SELECT_REQUEST)

        }
        binding.btnSave.setOnClickListener {
            if(isSelect) uploadSelectImageToFireStore()
            else uploadImageToFireStore()
        }
    }

    /** Metodo para subir informacion al FireStore **/
    fun uploadImageToFireStore(){
        if(photoFile != null){
            val imageReference = storageReference!!.child("uploads/" + UUID.randomUUID().toString())
            progressDialog.show()
            imageReference.putFile(Uri.fromFile(photoFile))
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(BaseApplication.applicationContext(), "El archivo se ha cargado exitosamente", Toast.LENGTH_LONG).show()
                    GetListImages()
                }
                .addOnFailureListener{
                    progressDialog.dismiss()
                    Toast.makeText(BaseApplication.applicationContext(), "Ha ocurrido un error mientras se cargaba el archivo " + it.message, Toast.LENGTH_LONG).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred/taskSnapshot.totalByteCount
                    progressDialog.setMessage("Cargando..." + progress.toInt() + "%")
                }
        }
    }

    /** Metodo para subir informacion al FireStore **/
    fun uploadSelectImageToFireStore(){
        if(destinationFile != null){
            val imageReference = storageReference!!.child("uploads/" + UUID.randomUUID().toString())
            progressDialog.show()
            imageReference.putFile(Uri.fromFile(destinationFile))
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(BaseApplication.applicationContext(), "El archivo se ha cargado exitosamente", Toast.LENGTH_LONG).show()
                    GetListImages()
                }
                .addOnFailureListener{
                    progressDialog.dismiss()
                    Toast.makeText(BaseApplication.applicationContext(), "Ha ocurrido un error mientras se cargaba el archivo " + it.message, Toast.LENGTH_LONG).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred/taskSnapshot.totalByteCount
                    progressDialog.setMessage("Cargando..." + progress.toInt() + "%")
                }
        }
    }

    fun getPhotoFileUri(fileName: String): File? {
        /** Se crea una ruta de carpeta donde se almacenara la informacion, si se pone null sera la raiz de la carpeta de la app **/
        val mediaStorageDir =  File(BaseApplication.applicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG)

        /** Se valida la creacion de la carpeta, y en caso de no poderse se muestra el error **/
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory")
            return null
        }

        /** Se regresa el objeto tipo File con la ruta donde se guardaran las imagenes **/
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    /** Resultado de los permisos **/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_CAMERA_PERMISSION_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(BaseApplication.applicationContext(), "camera permission granted", Toast.LENGTH_LONG).show()
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, CAMERA_REQUEST)
            } else {
                Toast.makeText(BaseApplication.applicationContext(), "camera permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    /** Resultado del intent de camara mandado a llamar **/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /** Se valida para la version de Android si es menor al Android 10, se hace de la forma tradicional **/
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
                filePath = data?.data
                val bitMap = MediaStore.Images.Media.getBitmap(mActivity.contentResolver, filePath)
                binding.imgPreview.setImageBitmap(bitMap)
            }else{
                /** En caso de que sea Android 11 se decodifica la imagen mediante el BitmapFatory y la ruta absoluta del archivo creado **/
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)

                /** Se setea en el preview de la aplicacion **/
                binding.imgPreview.setImageBitmap(takenImage)
            }

        }

        if(requestCode == CAMERA_SELECT_REQUEST && resultCode == Activity.RESULT_OK){
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
                filePath = data?.data
                val bitMap = MediaStore.Images.Media.getBitmap(mActivity.contentResolver, filePath)
                binding.imgPreview.setImageBitmap(bitMap)
            }else{
                val destinationPath = File(BaseApplication.applicationContext().getExternalFilesDir(null)?.getAbsoluteFile().toString(), "TESTFACTUM")
                if (!destinationPath.exists()) destinationPath.mkdirs()
                destinationFile = File(destinationPath, data?.data?.getLastPathSegment())
                try {
                    Files.deleteIfExists(destinationFile.toPath())
                    FileUtils.copy(
                        data?.data?.let {
                            BaseApplication.applicationContext().contentResolver.openInputStream(
                                it
                            )
                        }!!,
                        FileOutputStream(destinationFile)
                    )
                    /** En caso de que sea Android 11 se decodifica la imagen mediante el BitmapFatory y la ruta absoluta del archivo creado **/
                    val takenImage = BitmapFactory.decodeFile(destinationFile!!.absolutePath)

                    /** Se setea en el preview de la aplicacion **/
                    binding.imgPreview.setImageBitmap(takenImage)

                } catch (ex: IOException) {
                    ex.printStackTrace()
                }


            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CameraFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CameraFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun GetListImages(){
        val storage = Firebase.storage
        val listRef = storage.reference.child("uploads/")
        model.clear()
        listRef.listAll()
            .addOnSuccessListener { (items, prefixes) ->
                items.forEach { item ->
                    item.downloadUrl.addOnSuccessListener {
                      model.add(ImageStorageModel(it.toString().trim(), item.name))
                    }.addOnSuccessListener {
                        val imagesAdapter = ImagesStorageAdapter(model)
                        imagesAdapter.notifyDataSetChanged()
                        binding.recyclerImages.adapter = imagesAdapter
                    }
                }

            }
            .addOnFailureListener {
                Toast.makeText(BaseApplication.applicationContext(), "Ha ocurrido un error mientras se obtenia la informacion",  Toast.LENGTH_LONG).show()
            }
    }
}