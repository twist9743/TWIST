package com.example.lunin_ia_191_352.ui.Lab6

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lunin_ia_191_352.MainActivity
import com.example.lunin_ia_191_352.R
import com.example.lunin_ia_191_352.ui.Lab5.Lab5ViewModel
import com.example.lunin_ia_191_352.ui.dashboard.DashboardFragment
import java.net.URI
import android.os.Environment
import android.util.Base64.encode
import android.widget.EditText
import android.widget.TextView
import androidx.core.net.toFile
import com.google.android.gms.common.util.Base64Utils.encode
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.w3c.dom.Text
import java.io.*
import java.security.Security
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.math.BigInteger
import java.security.MessageDigest

class Lab6Fragment : Fragment() {

    private lateinit var lab6ViewModel: Lab6ViewModel
    private lateinit var keyEditText : EditText
    private lateinit var result : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lab6ViewModel =
            ViewModelProvider(this).get(Lab6ViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_lab6, container, false)
        keyEditText = root.findViewById<EditText>(R.id.Key)
        result = root.findViewById<TextView>(R.id.Result)
        val chose_file = root.findViewById<Button>(R.id.chose_file)
        val decrypt_file = root.findViewById<Button>(R.id.decrypt)
        chose_file.setOnClickListener {
            if (allPermissionsGranted()) {
                openFileForEncrypt()

            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    Lab6Fragment.REQUIRED_PERMISSIONS,
                    Lab6Fragment.REQUEST_CODE_PERMISSIONS
                )

            }
        }
        decrypt_file.setOnClickListener {
            if (allPermissionsGranted()) {
                openFileForDecrypt()

            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    Lab6Fragment.REQUIRED_PERMISSIONS,
                    Lab6Fragment.REQUEST_CODE_PERMISSIONS
                )

            }
        }


        return root
    }

    private fun allPermissionsGranted() = Lab6Fragment.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun openFileForEncrypt() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"

// Optionally, specify a URI for the file that should appear in the
// system file picker when it loads.
            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, ENCRYPT)
    }

    fun openFileForDecrypt() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"

// Optionally, specify a URI for the file that should appear in the
// system file picker when it loads.
            //putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, DECRYPT)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 111 && resultCode == RESULT_OK) {
            data?.data?.let {
                val selectedFile = it
                var fileName = selectedFile.lastPathSegment!!
                fileName = fileName.substring(fileName.lastIndexOf("/"), fileName.lastIndexOf("."))

                val text = readTextFromUri(selectedFile)

                val key = keyGen(keyEditText.text.toString())

                val encText = encrypt(text, key)

                val newFile = File(getOutputDirectory(), "${fileName}_enc.txt")


                if (encText != null) {
                    newFile.writeText(encText)
                    result.text = encText
                }


                // Perform operations on the document using its URI.
            }
        } //The uri with the location of the file
        if (requestCode == 222 && resultCode == RESULT_OK){
            data?.data?.let {
                val selectedFile = it
                var fileName = selectedFile.lastPathSegment!!
                fileName = fileName.substring(fileName.lastIndexOf("/"), fileName.lastIndexOf("."))

                val text = readTextFromUri(selectedFile)

                val key = keyGen(keyEditText.text.toString())

                val decText = decrypt(text, key)

                val newFile = File(getOutputDirectory(), "${fileName}_dec.txt")


                if (decText != null) {
                    result.text = decText
                    newFile.writeText(decText)
                }


                // Perform operations on the document using its URI.
            }
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireActivity().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireActivity().filesDir
    }



    private fun readTextFromUri(uri: Uri): String {
        val contentResolver = context?.contentResolver
        val stringBuilder = StringBuilder()
        contentResolver?.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line+"\n")
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }

    fun encrypt(strToEncrypt: String, secret_key: String): String? {
        Security.addProvider(BouncyCastleProvider())
        var keyBytes: ByteArray

        keyBytes = secret_key.toByteArray(charset("UTF8"))
        val skey = SecretKeySpec(keyBytes, "AES_256")
        val input = strToEncrypt.toByteArray(charset("UTF8"))

        synchronized(Cipher::class.java) {
            val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, skey)

            val cipherText = ByteArray(cipher.getOutputSize(input.size))
            var ctLength = cipher.update(
                input, 0, input.size,
                cipherText, 0
            )
            ctLength += cipher.doFinal(cipherText, ctLength)

            return encode(cipherText).toString()

        }

        return null
    }


    fun decrypt(strToDecrypt: String?, key: String): String? {
        Security.addProvider(BouncyCastleProvider())
        var keyBytes: ByteArray

            keyBytes = key.toByteArray(charset("UTF8"))
            val skey = SecretKeySpec(keyBytes, "AES_256")
            val input = org.bouncycastle.util.encoders.Base64
                .decode(strToDecrypt?.trim { it <= ' ' }?.toByteArray(charset("UTF8")))

            synchronized(Cipher::class.java) {
                val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                cipher.init(Cipher.DECRYPT_MODE, skey)

                val plainText = ByteArray(cipher.getOutputSize(input.size))
                var ptLength = cipher.update(input, 0, input.size, plainText, 0)
                ptLength += cipher.doFinal(plainText, ptLength)
                val decryptedString = String(plainText)
                return decryptedString.trim { it <= ' ' }
            }
        return null
    }

    private fun keyGen(key: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(key.toByteArray())).toString(16).padStart(32, '0')
    }



    companion object {
        const val ENCRYPT = 111
        const val DECRYPT = 222
        private const val REQUEST_CODE_PERMISSIONS = 11
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }



}
