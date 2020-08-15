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

package com.genonbeta.TrebleShot.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.genonbeta.TrebleShot.R;
import com.genonbeta.TrebleShot.app.IEditableListFragment;
import com.genonbeta.TrebleShot.graphics.drawable.TextDrawable;
import com.genonbeta.TrebleShot.object.LoadedMember;
import com.genonbeta.TrebleShot.object.Transfer;
import com.genonbeta.TrebleShot.object.TransferItem;
import com.genonbeta.TrebleShot.util.AppUtils;
import com.genonbeta.TrebleShot.util.DeviceLoader;
import com.genonbeta.TrebleShot.util.Transfers;
import com.genonbeta.TrebleShot.widget.EditableListAdapter;
import com.genonbeta.android.framework.widget.RecyclerViewAdapter;

import java.util.List;

/**
 * created by: veli
 * date: 06.04.2018 12:46
 */
public class TransferMemberListAdapter extends EditableListAdapter<LoadedMember, RecyclerViewAdapter.ViewHolder>
{
    private final Transfer mTransfer;
    private final TextDrawable.IShapeBuilder mIconBuilder;

    public TransferMemberListAdapter(IEditableListFragment<LoadedMember, ViewHolder> fragment, Transfer transfer)
    {
        super(fragment);
        mIconBuilder = AppUtils.getDefaultIconBuilder(fragment.getContext());
        mTransfer = transfer;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        ViewHolder holder = new ViewHolder(getInflater().inflate(isHorizontalOrientation() || isGridLayoutRequested()
                ? R.layout.list_transfer_member_grid : R.layout.list_transfer_member, parent, false));

        getFragment().registerLayoutViewClicks(holder);
        holder.itemView.findViewById(R.id.menu)
                .setOnClickListener(v -> getFragment().performLayoutLongClick(holder));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position)
    {
        LoadedMember member = getList().get(position);

        ImageView image = holder.itemView.findViewById(R.id.image);
        TextView text1 = holder.itemView.findViewById(R.id.text1);
        TextView text2 = holder.itemView.findViewById(R.id.text2);

        text1.setText(member.device.username);
        text2.setText(TransferItem.Type.INCOMING.equals(member.type) ? R.string.text_receiver : R.string.sender);
        DeviceLoader.showPictureIntoView(member.device, image, mIconBuilder);
    }

    @Override
    public List<LoadedMember> onLoad()
    {
        return Transfers.loadMemberList(getContext(), mTransfer.id, null);
    }
}