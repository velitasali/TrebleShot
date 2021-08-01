/*
 * Copyright (C) 2021 Veli Tasalı
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

package org.monora.uprotocol.client.android.model

import android.os.Parcelable
import com.genonbeta.android.framework.io.DocumentFile
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.monora.uprotocol.client.android.backend.Destination
import org.monora.uprotocol.client.android.backend.OperationBackend
import org.monora.uprotocol.client.android.backend.SharingBackend

@Parcelize
class FileModel(
    val file: DocumentFile,
    val indexCount: Int = 0
) : ContentModel, Parcelable {
    @IgnoredOnParcel
    var isSelected = false

    override fun canCopy(): Boolean = file.canRead()

    override fun canMove(): Boolean = file.canWrite()

    override fun canShare(): Boolean = file.canRead()

    override fun canRemove(): Boolean = file.canWrite()

    override fun canRename(): Boolean = file.canWrite()

    override fun copy(operationBackend: OperationBackend, destination: Destination): Boolean {
        TODO("Not yet implemented")
    }

    override fun dateCreated(): Long = file.getLastModified()

    override fun dateModified(): Long = file.getLastModified()

    override fun dateSupported(): Boolean = true

    override fun equals(other: Any?): Boolean {
        return other is FileModel && file == other.file
    }

    override fun filter(charSequence: CharSequence): Boolean = file.getName().contains(charSequence)

    override fun hashCode(): Int {
        return file.hashCode()
    }

    // TODO: 6/26/21 Absolute path may not be enough to create unique ids.
    override fun id(): Long = file.getUri().hashCode().toLong()

    override fun length(): Long = file.getLength()

    override fun lengthSupported(): Boolean = true

    override fun move(operationBackend: OperationBackend, destination: Destination): Boolean {
        TODO("Not yet implemented")
    }

    override fun remove(operationBackend: OperationBackend): Boolean {
        TODO("Not yet implemented")
    }

    override fun share(operationBackend: OperationBackend, sharingBackend: SharingBackend): Boolean {
        TODO("Not yet implemented")
    }

    override fun canSelect(): Boolean = true

    override fun selected(): Boolean = isSelected

    override fun select(selected: Boolean) {
        isSelected = selected
    }

    override fun name(): String = file.getName()
}