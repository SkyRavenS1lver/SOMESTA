package com.example.somesta.utility;

import androidx.recyclerview.widget.DiffUtil;

import com.example.somesta.Marker.Perusahaan;

import java.util.List;


public class GroupDiffCallBack extends DiffUtil.Callback {
    private final List<String> oldListData;
    private final List<String> newListData;

    public GroupDiffCallBack(List<String> oldListData, List<String> newListData) {
        this.oldListData = oldListData;
        this.newListData = newListData;
    }

    @Override
    public int getOldListSize() {
        return oldListData.size();
    }

    @Override
    public int getNewListSize() {
        return newListData.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldListData.get(oldItemPosition).equals(newListData.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldListData.get(oldItemPosition).equals(newListData.get(newItemPosition));
    }
}
