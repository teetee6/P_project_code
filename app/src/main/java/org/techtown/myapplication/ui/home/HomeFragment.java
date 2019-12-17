package org.techtown.myapplication.ui.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.techtown.myapplication.BookAdapter;
import org.techtown.myapplication.BookItem;
import org.techtown.myapplication.R;


import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private BookAdapter mBookAdapter;
    private List<BookItem> mBookArray;
    private LinearLayoutManager layoutManager;
    RecyclerView rViewKorean;
    static View root;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        rViewKorean = (RecyclerView) root.findViewById(R.id.rViewKorean);

        dataInit();

        return root;
    }

    private void dataInit(){

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rViewKorean.setLayoutManager(layoutManager);
        mBookAdapter = new BookAdapter(getActivity(),"kor");
        mBookAdapter.notifyDataSetChanged();

        rViewKorean.setAdapter(mBookAdapter);
        rViewKorean.setItemAnimator(new DefaultItemAnimator());

        mBookAdapter.setOnItemClickListener(new BookAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(BookAdapter.ViewHolder holder, View view, int position) {
                BookItem item = BookAdapter.getItem(position);
                CardView cv = (CardView) view.findViewById(R.id.cView1);
                Toast.makeText(getActivity(),"p:"+position,Toast.LENGTH_SHORT).show();
            }
        });
    }
}