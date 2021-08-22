package com.blogapp.ui.main.createPost

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.blogapp.R
import com.blogapp.databinding.FragmentCreateNewBlogBinding
import com.blogapp.ui.main.createPost.state.CreateBlogStateEvent
import com.blogapp.utils.Const.GALLERY_REQUEST_CODE
import com.blogapp.utils.Const.SUCCESS_BLOG_CREATED
import com.domain.dataState.DataState
import com.domain.dataState.MessageType
import com.domain.dataState.StateMessage
import com.domain.dataState.UIComponentType
import com.domain.utils.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@SuppressLint("UnsafeRepeatOnLifecycleDetector")
class CreateNewBlogFragment : BaseCreateBlogFragment<FragmentCreateNewBlogBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentCreateNewBlogBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        handleClickEvents()
        subscribeToObservers()
    }

    override fun onPause() {
        super.onPause()
        viewModel.setNewBlogFields(
            binding.blogTitle.text.toString(),
            binding.blogBody.text.toString(),
            null
        )
    }

    private fun handleClickEvents() {
        binding.blogImage.setOnClickListener {
            if (stateChangeListener.isStoragePermissionGranted()) {
                pickFromGallery()
            }
        }

        binding.updateTextview.setOnClickListener {
            if (stateChangeListener.isStoragePermissionGranted()) {
                pickFromGallery()
            }
        }
    }

    private fun subscribeToObservers() {

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.dataState.collect { dataState ->
                        stateChangeListener.dataStateChange(dataState)
                        when (dataState) {
                            is DataState.SUCCESS, is DataState.ERROR -> {
                                dataState.stateMessage?.let { stateMessage ->
                                    stateMessage.message?.let { msg ->
                                        if (msg == SUCCESS_BLOG_CREATED) {
                                            viewModel.clearNewBlogFields()
                                            findNavController().popBackStack()
                                        }else{
                                            Log.d(TAG, "subscribeToObservers: some error occurred: $msg")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                launch {
                    viewModel.viewState.collect{viewState ->
                        viewState.newBlogFields.let {
                            setBlogProperties(it.newBlogTitle, it.newBlogBody, it.newBlogImageUri)
                        }
                    }
                }
            }
        }
    }

    private fun publishNewBlog() {
        resultUri?.let {
            viewModel.setStateEvent(
                CreateBlogStateEvent.CreateNewBlogEvent(
                    binding.blogTitle.text.toString(),
                    binding.blogBody.text.toString(),
                    it
                )
            )
            stateChangeListener.hideSoftKeyboard()
        } ?: showErrorDialog("Please select image")
    }

    private fun setBlogProperties(title: String?, body: String?, imageUri: Uri?) {
        imageUri?.let {
            requestManager.load(imageUri)
                .into(binding.blogImage)
        }
        title?.let { binding.blogTitle.setText(it) }
        body?.let { binding.blogBody.setText(it) }
    }

    private fun setDefaultImage() {
        requestManager.load(R.drawable.ic_launcher_background)
            .into(binding.blogImage)
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun launchImageCrop(uri: Uri?) {
        context?.let {
            CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(it, this)
        }
    }

    private fun showErrorDialog(errorMsg: String) {
        stateChangeListener.dataStateChange(
            DataState.ERROR<CreateBlogStateEvent>(
                stateMessage = StateMessage(
                    message = errorMsg,
                    uiComponentType = UIComponentType.DIALOG,
                    messageType = MessageType.ERROR
                )
            )
        )
    }

    var resultUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        activity.let {
                            launchImageCrop(uri)
                        }
                    } ?: showErrorDialog("error....")
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    Log.d(TAG, "onActivityResult: CROP: CROP_IMAGE_ACTIVITY_REQUEST_CODE")
                    val result = CropImage.getActivityResult(data)
                    resultUri = result.uri
                    viewModel.setNewBlogFields(
                        title = null,
                        body = null,
                        uri = resultUri
                    )
                }

                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    showErrorDialog("error....")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.publish_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.publish -> publishNewBlog()
        }
        return super.onOptionsItemSelected(item)
    }
}