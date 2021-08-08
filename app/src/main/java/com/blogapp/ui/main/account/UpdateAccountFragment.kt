package com.blogapp.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.blogapp.R
import com.blogapp.databinding.FragmentUpdateAccountBinding
import com.blogapp.models.AccountProperties
import com.blogapp.models.mappers.AccountPropertiesMapper
import com.blogapp.ui.main.account.state.AccountStateEvent

class UpdateAccountFragment : BaseAccountFragment<FragmentUpdateAccountBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentUpdateAccountBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, { dataState ->
            stateChangeListener.dataStateChange(dataState)
            Log.d(TAG, "UpdateAccount: DataState: $dataState")
        })

        viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
            viewState?.let {
                viewState.accountProperties?.let {
                    Log.d(TAG, "UpdateAccount: VieState: $it")
                    setAccountProperties(AccountPropertiesMapper.toAccountProperties(it))
                }
            }
        })
    }

    private fun setAccountProperties(accountProperties: AccountProperties) {
        binding.inputEmail?.let {
            binding.inputEmail.setText(accountProperties.email)
        }
        binding.inputUsername?.let {
            binding.inputUsername.setText(accountProperties.username)
        }
    }

    private fun saveChanges() {
        viewModel.setStateEvent(
            AccountStateEvent.UpdateAccountProperties(
                binding.inputEmail.text.toString(),
                binding.inputUsername.text.toString()
            )
        )
        stateChangeListener.hideSoftKeyboard()
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