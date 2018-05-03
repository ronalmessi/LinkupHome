package com.ihomey.linkuphome.device

import android.app.DialogFragment
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.*
import com.ihomey.linkuphome.R
import com.ihomey.linkuphome.control.MeshControlViewModel
import com.ihomey.linkuphome.databinding.FragmentDialogDeviceRenameBinding
import com.ihomey.linkuphome.hideInput

/**
 * Created by dongcaizheng on 2018/4/14.
 */
class DeviceRenameFragment : DialogFragment(), View.OnClickListener {

    private lateinit var mViewDataBinding: FragmentDialogDeviceRenameBinding
    private var mViewModel: MeshControlViewModel? = null
    private var mDeviceName: String = ""
    private var mDeviceId: Int = -1
    private var mDeviceType: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mViewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_device_rename, container, false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mViewModel = ViewModelProviders.of(activity as AppCompatActivity).get(MeshControlViewModel::class.java)
        mDeviceName = arguments.getString("controlDeviceName", "")
        mDeviceId = arguments.getInt("controlDeviceId", -1)
        mDeviceType = arguments.getInt("controlDeviceType", -1)

        if (!TextUtils.isEmpty(mDeviceName)) {
            mViewDataBinding.deviceRenameEtName.setText(mDeviceName)
            mViewDataBinding.deviceRenameEtName.setSelection(mDeviceName.length)
        }
        mViewDataBinding.deviceRenameEtName.requestFocus()
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        mViewDataBinding.deviceRenameBtnConfirm.setOnClickListener(this)
        mViewDataBinding.deviceRenameBtnCancel.setOnClickListener(this)
        return mViewDataBinding.root
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.device_rename_btn_confirm) {
            if (mViewDataBinding.deviceRenameEtName.text != null && !TextUtils.equals(mDeviceName, mViewDataBinding.deviceRenameEtName.text.toString()) && !TextUtils.isEmpty(mViewDataBinding.deviceRenameEtName.text.toString())) {
                mViewModel?.updateDeviceName(mDeviceType,mDeviceId, mViewDataBinding.deviceRenameEtName.text.toString())
            }
        }
        v?.context?.hideInput(mViewDataBinding.deviceRenameEtName)
        dismiss()
    }
}