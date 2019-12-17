package org.techtown.myapplication.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class DashboardFragment extends Fragment {

    private BookAdapter mBookAdapter;
    private List<BookItem> mBookArray;
    private LinearLayoutManager layoutManager;
    RecyclerView rViewEnglish;
    static View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        rViewEnglish = (RecyclerView) root.findViewById(R.id.rViewEnglish);

        dataInit();

        return root;
    }

    private void dataInit(){

        /*mBookArray = new ArrayList<BookItem>();

        for(int i=0;i<3;i++){
            BookItem item = new BookItem();
            item.setName("Boy and Cat");//TODO: set programmatically
            item.setImgSrc(R.mipmap.ic_action_crop_original); //TODO: set programmatically
            item.setTitle_server("cat");//TODO: set programmatically
            mBookArray.add(item);
        }*/

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rViewEnglish.setLayoutManager(layoutManager);
        mBookAdapter = new BookAdapter(getActivity(),"eng");

        rViewEnglish.setAdapter(mBookAdapter);
        rViewEnglish.setItemAnimator(new DefaultItemAnimator());

        mBookAdapter.setOnItemClickListener(new BookAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(BookAdapter.ViewHolder holder, View view, int position) {
                BookItem item = BookAdapter.getItem(position);
                CardView cv = (CardView) view.findViewById(R.id.cView1);
                //Toast.makeText(getContext(),"position = "+position,Toast.LENGTH_LONG).show();
            }
        });
    }
}