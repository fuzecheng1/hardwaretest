package com.hollyland.hardwaretest.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hollyland.hardwaretest.R;
import com.hollyland.hardwaretest.entity.TestItem;

import java.util.ArrayList;
import java.util.List;


/**
 * 硬件测试工具主界面的菜单选项Adapter
 */
public class TestItemRecyclerViewAdpater extends RecyclerView.Adapter<TestItemRecyclerViewAdpater.ViewHolder> {


    private List<TestItem> testItems = new ArrayList<>();

    private onClickItemListener listener;

    public void setOnClickItemListener(onClickItemListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_activity_test_item,null);
        return new ViewHolder(view);
    }

    public TestItem getTestItem(int position){
        return testItems.get(position);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         TestItem testItem = testItems.get(position);
         holder.button.setText(testItem.getName());
         holder.button.setOnClickListener(view -> {
                if (listener != null){
                    listener.onClick(position);
                }
         });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTestItems(List<TestItem> items){
        testItems.clear();
        testItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return testItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.btn_main);
        }
    }

    public interface onClickItemListener{

       void onClick(int position);
    }
}
