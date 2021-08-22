package com.blogapp.ui.main.blogs

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.blogapp.R
import com.blogapp.databinding.FragmentUpdateBlogBinding
import com.blogapp.models.mappers.BlogPostMapper
import com.blogapp.ui.main.blogs.state.BlogStateEvent
import com.blogapp.ui.main.blogs.viewModel.getBlogPost
import com.blogapp.ui.main.blogs.viewModel.handleIncomingBlogListData
import com.blogapp.ui.main.blogs.viewModel.onBlogPostUpdateSuccess
import com.blogapp.ui.main.blogs.viewModel.setUpdatedBlogFields
import com.blogapp.ui.main.createPost.state.CreateBlogStateEvent
import com.blogapp.utils.Const
import com.domain.dataState.DataState
import com.domain.dataState.MessageType
import com.domain.dataState.StateMessage
import com.domain.dataState.UIComponentType
import com.domain.utils.*
import com.domain.viewState.BlogViewState
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@SuppressLint("UnsafeRepeatOnLifecycleDetector")
class UpdateBlogFragment : BaseBlogFragment<FragmentUpdateBlogBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentUpdateBlogBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        subscribeObservers()
        handleOnCLickEvents()
    }


    private fun handleOnCLickEvents() {
        binding.blogImage.setOnClickListener {
            if (stateChangeListener.isStoragePermissionGranted()) {
                pickFromGallery()
            }
        }
    }

    private fun subscribeObservers() {

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.dataState.collect { dataState ->
                        when (dataState) {
                            is DataState.SUCCESS -> {
                                dataState.data?.let {
                                    viewModel.handleIncomingBlogListData(it)
                                }
                            }
                            else -> stateChangeListener.dataStateChange(dataState)
                        }
                    }
                }

                launch {
                    viewModel.viewState.collect {
                        it.updateBlogFields.let { blogProperties ->
                            setBlogProperties(
                                blogProperties.blogTitle,
                                blogProperties.blogBody,
                                blogProperties.imageUri
                            )
                        }
                    }
                }
            }
        }

    }

    private fun setBlogProperties(blogTitle: String?, blogBody: String?, imageUri: Uri?) {
        requestManager.load(imageUri).into(binding.blogImage)
        binding.blogTitle.setText(blogTitle)
        binding.blogBody.setText(blogBody)
    }

    private fun saveChanges() {
        viewModel.setStateEvent(
            BlogStateEvent.UpdateBlogStateEvent(
                binding.blogTitle.text.toString(),
                binding.blogBody.text.toString(),
                resultUri ?: viewModel.getBlogPost().image.toUri()
            )
        )
        stateChangeListener.hideSoftKeyboard()
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, Const.GALLERY_REQUEST_CODE)
    }

    private fun launchImageCrop(uri: Uri?) {
        context?.let {
            CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(it, this)
        }
    }

    var resultUri: Uri? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Const.GALLERY_REQUEST_CODE -> {
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
                    viewModel.setUpdatedBlogFields(
                        title = null,
                        body = null,
                        imageUri = resultUri
                    )
                }

                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    showErrorDialog("error....")
                }
            }
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                saveChanges()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}