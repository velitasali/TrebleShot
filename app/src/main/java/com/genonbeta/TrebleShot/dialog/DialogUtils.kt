/*
 * Copyright (C) 2019 Veli Tasalı
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.genonbeta.TrebleShot.dialog

import android.app.Activity
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.genonbeta.TrebleShot.R
import com.genonbeta.TrebleShot.dataobject.Transfer
import com.genonbeta.TrebleShot.dataobject.TransferItem
import com.genonbeta.TrebleShot.util.AppUtils
import java.util.*

/**
 * created by: veli
 * date: 7/3/19 7:53 PM
 */
object DialogUtils {
    fun showGenericCheckBoxDialog(
        activity: Activity?, @StringRes title: Int, content: String?,
        @StringRes positiveButton: Int, @StringRes checkBox: Int,
        positiveListener: ClickListener, vararg textArgs: String?
    ) {
        val view = LayoutInflater.from(activity).inflate(
            R.layout.abstract_layout_dialog_text_option,
            null
        )
        val text1: TextView = view.findViewById<TextView>(R.id.text1)
        val checkBox1: CheckBox = view.findViewById<CheckBox>(R.id.checkbox1)
        text1.setText(content)
        if (checkBox == 0) checkBox1.setVisibility(View.GONE) else checkBox1.setText(checkBox)
        AlertDialog.Builder(activity!!)
            .setTitle(title)
            .setView(view)
            .setNegativeButton(R.string.butn_cancel, null)
            .setPositiveButton(positiveButton) { dialog: DialogInterface?, which: Int ->
                positiveListener.onClick(
                    dialog, which,
                    checkBox1
                )
            }
            .show()
    }

    fun showRemoveDialog(activity: Activity, transfer: Transfer) {
        showGenericCheckBoxDialog(activity, R.string.ques_removeAll,
            activity.getString(R.string.text_removeTransferGroupSummary),
            R.string.butn_remove, R.string.text_alsoDeleteReceivedFiles,
            ClickListener { dialog: DialogInterface?, which: Int, checkBox: CheckBox ->
                transfer.deleteFilesOnRemoval = checkBox.isChecked()
                AppUtils.getKuick(activity)!!.removeAsynchronous(activity, transfer, null)
            })
    }

    fun showRemoveDialog(activity: Activity, `object`: TransferItem) {
        val checkBox = if (TransferItem.Type.INCOMING == `object`.type) R.string.text_alsoDeleteReceivedFiles else 0
        showGenericCheckBoxDialog(activity, R.string.ques_removeTransfer, activity.getString(
            R.string.text_removeTransferSummary, `object`.name
        ),
            R.string.butn_remove, checkBox, ClickListener { dialog: DialogInterface?, which: Int, checkBox1: CheckBox ->
                `object`.setDeleteOnRemoval(checkBox1.isChecked())
                AppUtils.getKuick(activity)!!.removeAsynchronous(activity, `object`, null)
            })
    }

    fun showRemoveTransferObjectListDialog(
        activity: Activity,
        objects: List<TransferItem>
    ) {
        val copiedObjects: List<TransferItem> = ArrayList(objects)
        showGenericCheckBoxDialog(activity, R.string.ques_removeTransfer,
            activity.resources.getQuantityString(R.plurals.text_removeQueueSummary, objects.size, objects.size),
            R.string.butn_remove, R.string.text_alsoDeleteReceivedFiles,
            ClickListener { dialog: DialogInterface?, which: Int, checkBox: CheckBox ->
                val isChecked: Boolean = checkBox.isChecked()
                for (`object` in copiedObjects) `object`.setDeleteOnRemoval(isChecked)
                AppUtils.getKuick(activity)!!.removeAsynchronous(activity, copiedObjects, null)
            })
    }

    fun showRemoveTransferGroupListDialog(
        activity: Activity,
        groups: List<Transfer>?
    ) {
        val copiedTransfers: List<Transfer> = ArrayList(groups)
        showGenericCheckBoxDialog(activity, R.string.ques_removeAll, activity.getString(R.string.text_removeSelected),
            R.string.butn_remove, R.string.text_alsoDeleteReceivedFiles,
            ClickListener { dialog: DialogInterface?, which: Int, checkBox: CheckBox ->
                val isChecked: Boolean = checkBox.isChecked()
                for (transfer in copiedTransfers) transfer.deleteFilesOnRemoval = isChecked
                AppUtils.getKuick(activity)!!.removeAsynchronous(activity, copiedTransfers, null)
            })
    }

    interface ClickListener {
        fun onClick(dialog: DialogInterface?, which: Int, checkBox: CheckBox?)
    }
}