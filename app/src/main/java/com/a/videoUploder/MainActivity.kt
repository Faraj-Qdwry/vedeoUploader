package com.a.videoUploder

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import java.io.File


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        selectVideo()
    }

    fun selectVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_CODE_PICKER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //playVideo(data?.data?.path)
        uploadFile(data?.data)
    }

    private fun uploadFile(uri: Uri?) {
        val file = File(uri?.path)

        val descriptionPart = RequestBody.create(MultipartBody.FORM, "hello, this is description")

        //todo check if this part works fine
        val orignalFile = FileUtils.getFile(this, uri)

        val filePart = RequestBody.create(
                MediaType.parse(contentResolver.getType(uri)),
                orignalFile
        )

        val bodyFileTOUpload = MultipartBody.Part.createFormData("video", file.name, filePart)

        val API = Retrofit.Builder()
                .baseUrl("trarara.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(retRofitClint::class.java)

        API.uploadVideo(descriptionPart, bodyFileTOUpload)
    }

    companion object {
        private const val REQUEST_CODE_PICKER = 10
    }

    internal interface retRofitClint {
        @Multipart
        @POST("upload/{videoId}")
        fun uploadVideo(@Path("videoId") videoId: RequestBody, @Part file: MultipartBody.Part): Single<videoUploadResp>
    }

    internal class videoUploadResp(
            @SerializedName("path") val path: String)


//    private fun playVideo(path: String?) {
//        videoPlayer.setVideoPath(path)
//        videoPlayer.start()
//    }
}
